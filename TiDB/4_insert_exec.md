Reference: https://pingcap.com/blog-cn/tidb-source-code-reading-4/

In this post, we trace through the execution of a simple `INSERT` statement. 

## Notation

```
Foo()
    Bar()
    Blah()
Baz()
```

The notation above means Both Bar() and Blah() are called by Foo(), and they are executed serially.



## Execution flow

The entire execution flow of `INSERT INTO t VALUES ("pingcap001", "pingcap", 3);`: parsing, planning, optimizing, building executor, executing.

![image-20210317102838892](./4_execution_flow.png)

The text version is in `4_execution_flow.txt`.