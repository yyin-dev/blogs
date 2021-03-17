Reference: https://pingcap.com/blog-cn/tidb-source-code-reading-2/



## Overview

In one sentence, TiDB is a MySQL-compatible NewSQL database for HTAP worloads, using some distributed KV-storage that supports transactions as underlying storage engine. 

Thus, the three most important functions of TiDB source code are:

- Interact with MySQL client
- Execute SQL queries
- Use underlying storage engine



## Source code

To copmile and run TiDB, run `make && ./bin/tidb-server`.

The `main()` in `tidb-server/main.go` parses flags, initializes thingks like underlying storage engines, creates the server, and starts the server handling incoming request. `createServer() -> NewServer()` creates a server using the [`net` package](https://golang.org/pkg/net/). In simplest form, to start a server:

```go
listener, err := net.Listen("tcp", ":8080")
if err != nil { }
for {
	conn, err := listener.Accept()
	if err != nil { }
	go handleConnection(conn)
}
```

`NewServer` creates the listener (as a field of `Server`). The main logic is as follows.

```go
func NewServer() (*Server, error) {
    s = &Server{ ... }
    loadConfigurations()
    
    if s.cfg.Host != "" && s.cfg.Port != 0 {
        addr := fmt.Sprintf("%s:%d", s.cfg.Host, s.cfg.Port)
        tcpProto := "tcp"
        // a net.Listener, which you can use listener.Accept() to accept incoming requests.
        if s.listener, err = net.Listen(tcpProto, addr); err == nil {
            // if cfg contains both Host&Port and Socket, do the following:
            // Listen on Host&Port, and forwards all request to the socket to the Host&Port
            if cfg.Socket != "" {
                if s.socket, err = net.Listen("unix", s.cfg.Socket); err == nil {
                    go s.forwardUnixSocketToTCP()
                }
            }
        }
    } else if cfg.Socket != "" {
        // If Host&Port is not specified, listen on socket
        s.listener, err = net.Listen("unix", cfg.Socket)
    } else {
        err = errors.New("Server not configured to listen on either -socket or -host and -port")
    }

    return s, err
}
```

`runServer() -> Run()`, which accepts connection ( `s.listener.Accept()`) and handles each connection ( `go s.onConn(clientConn)`) in a separate goroutine.

### Protocol Layer

The protocol layer is responsible for interacting with MySQL client. `onConn` calls `conn.Run(ctx)` to read client query and dispatch it to specific handler (`cc.dispatch(ctx, data)`). 

### SQL Layer

To execute query, you need the following steps: parsing, validation, query planning, query plan optimization, construct query executor using query plan, execute the plan. Following posts will go through this in more detail. 

### KV API Layer

TiDB relies on underlying storage engine that must implement certain interfaces (not necessarikly TiKV). Currently, TiDB supports TiKV, MockTiKV, and Unistore (in memory storage). How does TiDB which one to use?

This is by configured by `cfg.Store` and `cfg.Path` (see `createStoreAndDomain`), stored in `globalConf`. If not overwritten, TiDB use the default configuration `defaultConf` in `config/config.go`, which uses Unistore. See `init()` in  `config/config.go`.

The set of interfaces the underlying KV storages are required to implement is defined in `kv/kv.go`. Following posts will go through this in more detail. 











