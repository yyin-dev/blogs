Reference: https://pingcap.com/blog-cn/tidb-source-code-reading-3/

This post talks about the entire lifecycle of SQL execution, still closely related with the three topics introduced earlier: support MySQL protocol and interact with client; parse-validate-plan-optimize-execute the query; how to use the underlying storage engine.

This post only introduces the overall structure of SQL execution. Next post will walk through the execution of one concrete SQL.



## Protocol Layer

A goroutine `server.go: onConn` is started for each client connection. 

```
server.go: onConn
    conn.go: Run
        conn.go: readPacket
        conn.go: dispatch
        	  conn.go: handleQuery
        	  	  session.go: cc.ctx.Parse
                conn.go: handleStmt
                    driver_tidb.go: ExecuteStmt
                        session.go: ExecuteStmt <- execution actually starts
										conn.go: writeResultSet
```



## SQL Layer

An important interface is `Session`, contained in `ctx` (`TiDBContext`) of  `clientConn`. 

```go
type clientConn struct {
	...
	ctx          *TiDBContext      // an interface to execute sql statements.
	...
}

type TiDBContext struct {
	session.Session
	currentDB string
	stmts     map[int]*TiDBStatement
}

type Session interface {
	...
	// ExecuteStmt executes a parsed statement.
	ExecuteStmt(context.Context, ast.StmtNode) (sqlexec.RecordSet, error)
	// Parse is deprecated, use ParseWithParams() instead.
	Parse(ctx context.Context, sql string) ([]ast.StmtNode, error)
	...
	Close()
	...
}
```

### Parsing

See `session.go: Parse`.

### Validation, Plan, Optimization

See `session.go: ExecuteStmt -> compiler.go: Compile`.

- `plannercore.Preprocess`
- `plannercore.Optimize`
- construct `executor.ExecStmt` , which is the foundation of following executions.

### Execution

`session.go: ExecuteStmt -> runStmt -> executor/adapter.go: Exec -> buildExecutor `.  This converts from `ExecStmt` to `Executor`. Then return value of `Exec` is a `RecordSet`, which is an abstraction for the query result. You can call `Next` repeatedly to get each tuple.

TiDB is implemented in the Volcano model, i.e., calling `Next` repeatedly in a top-down fashion. The question is who's the root node making the 1st call to `Next`? There're two types of statements. The first type is like `SELECT`, where TiDB needs to return result to the client.

In `executor/adapter.go: Exec`, after getting `e, err := a.buildExecutor()`, it makes the following call:

```go
if handled, result, err := a.handleNoDelay(ctx, e, isPessimistic); handled {
		return result, err
}
```

`handleNoDelay` only handles queries that can be immediately executed, like `INSERT`, `UPDATE`, but not including `SELECT`. For example, `INSERT` will execute the first if-branch, since its output schema is empty. For the 2nd branch, see this about [DO statement](https://dev.mysql.com/doc/refman/8.0/en/do.html).

```go
func (a *ExecStmt) handleNoDelay(ctx context.Context, e Executor, isPessimistic bool) (handled bool, rs sqlexec.RecordSet, err error) {
	// ...
  
  toCheck := e

  // If the executor doesn't return any result to the client, we execute it without delay.
  if toCheck.Schema().Len() == 0 {
    handled = !isExplainAnalyze
    if isPessimistic {
      return handled, nil, a.handlePessimisticDML(ctx, toCheck)
    }
    r, err := a.handleNoDelayExecutor(ctx, toCheck)
    return handled, r, err
  } else if proj, ok := toCheck.(*ProjectionExec); ok && proj.calculateNoDelay {
    // Currently this is only for the "DO" statement. Take "DO 1, @a=2;" as an example:
    // the Projection has two expressions and two columns in the schema, but we should
    // not return the result of the two expressions.
    r, err := a.handleNoDelayExecutor(ctx, e)
    return true, r, err
  }

  return false, nil, nil
}
```

If `handleNoDelay` returns false, `Exec` returns a `RecordSet`. This RecordSet is returned upwards to `session.go: handleStmt`, where `writeResultSet` repeatedly calls `Next` to send results back to client lazily.



