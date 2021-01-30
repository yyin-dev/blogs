# *Design Data-Intensive Applications* Notes



## Part I. Foundations of Data Systems

Introduces fundamental concepts for data systems, no matter on a single machine or distributed.



### Chapter 1. Reliable, Scalable and Maintainable Applications

Data-intensive vs. compute-intensive.

- Reliability
  - Hardware redundancy
  - Distributed fault-tolerance
- Scalability
  - Describing load
  - Describing performance
  - Vertical/Horizontal scaling
  - The architecture of a large-scale system is usually highly specific to the application, relying on correct assumptions of the system's load.
- Maintainability
  - Operability: make life easy for Ops
  - Simplicity: make life easy for new engineers
  - Evolvability: make it easy to make new changes



### Chapter 2. Data Models and Query Languages

- Relational model

  - Relations (tables) + tuples (rows).

  - Good support for joins
  - Good support for many-to-one and many-to-many.

- Document model

  - Store self-contained records as *documents*, not split across tables
  - Good support for one-to-many
  - Weak support for joins
  - Weak support for many-to-one and many-to-many (*)
  - Schema flexibility
  - Better locality

- Graph model

  - Edges & vertices
    - Edge = unique ID + outgoing edges + incoming edges + properties
    - Vertex = unique ID + starting edge + ending edge + properties
    - Vertices represent entities, while edges represent relationships between entities
  - By giving vertices different lables, you can model different kinds of information
  - High evolvability and high flexibility

- Query language

  - Declarative vs Imperative: Declarative allows for optimization, abstraction, and parallelism

- Relational model, document model, or graph model for your application?

  - Document-like structure and relationships between documents are rare -> document model
  - Mostly one-to-many -> document model
  - Requires many-to-one, many-to-many relationships, or joins (highly interconnected data) -> relational model
  - Anything is related with everything -> graph model
  - Requires high flexibility in schema: document model



*: Why many-to-one and many-to-many doesn't fit nicely with document model? Reason: The document model has weak support of joins, but many-to-one and many-to-many relationships is highly dependent on joins (by IDs).



### Chapter 3. Storage and Retrieval

A database does two things: (1) store data given by user; (2) give back data when asked. Chapter 2 discusses the two questions from the user's perspective, while Chapter 3 discusses from the DB's perspective. 

- Simple idea: Hash index

  - Mechanism: in-memory hashtable `<key, offset>` + append-only log
  - How to avoid huge log file? Compaction + merging
  - Problem: Hashtable must fit in memory; doesn't support range query.

- Sorted String Table (SSTable) and Log-Structured Merge-Tree (LSM-Tree)

  - Key idea: systematically turn random-access writes into sequential writes on disk
  - Like append-only log, with additional requirements: 
    - Each log-structured segment is sorted by keys
    - Each key only appears once in each segment
  - Advantages:
    - Merging is efficient even if the file is larger than memory
    - Sorted keys allow sparse index, so the size of the in-memory hashtable can be reduced
  - Mechanism: in-memory balanced tree (called *memtable*) + SSTable files
  - Crash recovery? One separate sequential log to restore memtable after a crash
  -  The principle of merging and compacting sorted files are called Log-Structured Merge-Tree (LSM-Tree)
  - Files are sorted, so LSM-Tree supports range query; Disk writes are sequential, so high write throughput.

- B+ tree index

  - Each update might requires writing to several disk pages, so a *write-ahead log* (WAL) is commonly used
  - Different from LSM-trees, in-place/random-access updates to the index

- Comparing B+ trees and LSM-Trees

  - Typically, LSM-Trees faster for writes, while B+ trees faster for reads
  - However, performance is sensitive to the workload, and empirical testings are needed for your own workload
  - Pro for LSM-Trees: smaller *write amplification*; sequential writes; Compressed better and spce-efficient
  - Con for LSM-Trees: compacting/merging process can downgrade performance; multiple copies of the same key could be in  different segments, so transaction isolation must use latches on a range of keys

- OLTP (OnLine Transaction Processing) vs OLAP (OnLine Analytics Processing)

  Due to the differenece in workload and requirement, typically different DBs are used.

- Stars and Snowflakes schema for OLAP

  - *Fact table*: table for events, containing foreign key references to ...
  - *Dimension table*: contains more attributes

- Column-Oriented Storage

  - An analytics job queries millions of rows, each having hundreds of columns - but only 4 or 5 columns accessed at a time.
  - Column-oriented storage stores columns, instead of rows, together
  - Column compression
  - If a column is sorted, we can use the column as an index. The orders of all columns must be synced.
  - Multiple sort orders: Data replication is required anyway, so store copies in different orders, each as a secondary index



### Chapter 4. Encoding and Evolution

- When a data format or schema changes (called *schema evolution*), the application code needs to change. However, code changes rarely happen instantaneously. Old and new versions of the code, and old and new data formats, coexist at the same time. We need compatibilities in both directions:

  - Backward compatibility: newer code can read data written by older code
  - Forward compatibility: older code can read data written by newer code

- Programs need two representations of data: in-memory representation; disk/network representation as self-contained bytes. The translation between the two representations are called *encoding* and *decoding*.

- Language-specific formats are insufficient

  - Tied to a particular language, disallowing different languages to communicate
  - To restore data, the decoding process needs to instantiate arbitrary class (security issue)
  - Typically not efficient

- JSON, XML, and their binary variants

  - Ambiguity round encodings of numbers
  - Weak support for binary strings
  - Weak support for schema

  - Not too efficient. As they don't prescribe a schema, they need to include all field names

- Thrift and Probobuf

  - Require a schema
  - Use field tags (numbers) to avoid encoding field names
  - Field tags are critical to the meaning of the encoded data, as the encoded data doesn't refer to field names, but only tags. You can change field names but not tags, for compatibility. 
  - Forward compatibility: You can add new fields with new tags. If old code tries to read data written by new code, including a new field with unrecognized tag name, the field is ignored. 

  - Backward compatibility: New code can read data written by old code, if the new fields are not marked as `required`. 

- Avro

  - Require a schema, but no tag numbers
  - The encoded data has nothing to identify fields or datatypes. To parse the data, go through the fields in the order as the schema used for encoding
  - In Avro, the *writer's schema* and *reader's schema* don't have to be the same, but only needs to be compatible. Avro library resolves the difference by comparing the two schemas and translate from the writer's schema to the reader's schema.
  - How does the reader kwow the writer's schema?
    - Include writer's schema with every piece of encoded data is infeasible
    - The context where Avro is used
      - In Hadoop, sending large files with many records. Including the writer's schema at the beginning of the file is ok.
      - Database records written with different schema. Include a version number for each encoded record, and keep a list of all schema versions.
      - Database records sent over network. The two processes can negotiate the schema version on connection setup.
    - Avor's schema doesn't contain tag numbers, so it's friendly to *dynamically generated* (not pre-agreed) schemas.
  - Merits of schemas
    - Compacted encoding
    - Valuable form of documention (that doesn't get outdated)
    - Keeping a database of schema allows checking for forward and backward compatibility
    - For statically typed programming language, code generation from schema is useful for compile-time type checking



## Part II. Distributed Data

For scalability, fault-tolerance and latency, you need distributed systems.

### Chapter 5. Replication

- Choices: single-leader/multi-leader/leaderless; synchronous/asynchronous replication?
- Single-leader replication
  - Writes go to the leader, while reads can go to followers. 
  - Set up new followers by snapshot
  - Follower failure: catch-up recovery
  - Leader failure (*failover*): Determine leader has failed; choose a new leader; reconfigure followers to the new leader;
  - Possible problems
    - For asynchronous replication, writes to old leader might not be replicated to the new leader yet. If former leader rejoins the cluster, what happens to the conflicting writes? Simply discard them (and violates durability guarantee to client)?
    - How to reliably detect failed leaders and avoid split brain?
    - What is the right timeout for failure detection?
  - Replication logs implementation
    - Statement-based replication: problem with non-deterministic functions and side effects; many edge cases; rarely used.
    - Write-ahead log (WAL) shipping: WAL used by the database itself is shipped to followers. Problem: very lower-level, closely coupled with the storage engine. The followers must be able to interpret WAL from the leader.
    - Logical log replication: log (*logical log*) decoupled from the storage engine, to distinguish from storage engine's (*physical*) data representation. For relational databases, logical log is typically row-based, describing changes to the row. Example: MySQL's binlog. Pro: easier to be compatible between leader and followers; can be parsed by external applications.  

- Problem with replication log

  - Cannot use synchronous replication, as it kills performance if more nodes are added. However, log might not be replicated to followers in time. This cause temporary inconsistency - the followers will eventually catch up and become consistent replica. This is *eventual consistency*.
  - *Read-your-writes consisten*cy. The single-leader approach doesn't guarantee this. Main idea of providing read-you-writes in single-leader approach: use business logic (whether a user may change certain data) or metadata (did the user recently update the data?) to make certain writes go to leader.
  - *Monotonic reads*. States read by the user should never go back in time. Eventual consistency < monotonic reads < strong consistency. 
  - *Consistent prefix reads*. If a sequence of writes happen in a certain order, anyone reading those should see them appear in the same order. 

- Multi-leader replication

  - Why multi-leader? In single-leader approach, all writes go through the single-leader, which can become the bottleneck
  - Pro: the single leader is no longer the bottleneck.
  - Con: concurrent modifications to the same piece of data (through different leaders) requires conflict resolution.
  - Use cases
    - Multi-datacenter: regular leader-replication is used within each datacenter; between datacenters, each datacenter's leader asynchronously replicates its changes to the leaders in other datacenters.
    - Clients with offline operations: calendar apps on different devices
    - Real-time collaborative editing
  - Biggest problem: Conflict Resolution
    - Synchronous conflict detection (with locks)? No, multi-leader aims to allow replicas accept writes independently.
    - Conflict avoidance: writes to the same piece of data is always handled by one leader. Not always possible. Also, sometimes you might want to change the designated leader (if one datacenter failed).
    - Let the application handle the conflict.

- Leaderless replication

  - Fashion started by Amazon's DynamoDB, calle *Dynamo-style*.

  - Usually, the client sends reads/writes to **several replicas**, or some coordinator does this for the client. 

  - If the client sends writes to all replicas, but some are rebooting, those replicas miss the writes. If the client reads from those replicas, it can get stale data. To solve the problem, reads are **also** sent to multiple replicas. Version numbers are used to determine which values are fresher.

  - Two approaches to make temporarily-unavailable nodes catch up:

    - Read repair: When reading from several replicas in parallel, use fresh data to overwrite stale data.
    - Anti-entropy: Have a background process that constantly scans replicas and copies missing data.

    The read-repair works well for frequently read data. Dynamo-style datastores without anti-entropy process has reduced durability, since some data are not replicated (until read-repair happens at read).

  - Quorums for reads and writes

    - Requirement: `r + w > n`
    - `r`/ `w` can be increased to favor write/read
    - With smaller `r` , `w` (e.g. `r + w <= n`), more likely to read stale data, but better fault-tolerance, higher performance
    - Even with `r + w > n`, may read stale data. Refer to page 181 for details.
    - Dynamo-style datastores only provide eventual consistency

  - Detecting concurrent writes

    - In leaderless replication, nodes accept writes concurrently. However, write events can arrive at different nodes in different orders. If those writes are not handled carefully, we don't even have eventual consistency.

    - Last write wins (LWW): as each key corresponds to only one value, we only need the last write. However, for concurrent writes, the order is undefined. It's possible for force some arbritary order on concurrent writes and discard all writes except the last one. LWW achieves eventual consistency but compromises durability promise. When there're concurrent writes, though all reported successful to the client, only one will survive while others will be silently discarded. In short, the problem with LWW is data loss.

    - Concurrent: if two writes are not aware of each other, they can be considered as concurrent. 

    - For two operations A and B, there're 3 possibilities: A happened before B, B happened before A, or A and B are concurrent. We need an algorithm to **detect if two writes are concurrent**. If we have such an algorithm, we can overwrite earlier writes with later ones; For concurrent writes, we need conflict resolution. We don't lose data.

    - One algorithm for detecting concurrent writes, as described in page 188. 

      ```
      Server maintains a version number for every key, increments the version number for every write, and store the version number with the value;
      When client reads a key, server returns all values that haven't been overwritten, as well as the latest version number; A client must read a key before writing;
      When a client writes a key, it must include the version number from previous read, and must merge together all values that it received in the previous read;
      When the server receives a write with a particular version number, it can overwrite all values with that version number all below, but must keep all values with a higher version number (this are concurrent writes with the incoming write).
      ```

      Python pesudo-code:

      ```python
      # Suppose the client and server pre-agreed on what key to write, i.e., we only have a single key.
      
      class Client:
      	def __init__(self):
          # Client class for writing to one key
      		self.val = None
          self.version = -1
      
        def write():
          val_list, server_assigned_version = server.handle_write(self.val, self.version)
          self.val = merge_vals(val_list)
          self.version = server_assigned_version
      
      
      class Server:
        def __init__(self):
         	self.val_list = [] # Suppose version starts from 0
          
        def handle_write(self, val, overwritten_version):
          if len(self.val_list) == 0: 
            # The first write to the value
            self.val_list.append(val)
            return [val], 0
          else:
            # Later writes to the value
            if len(self.val_list)-1 == overwritten_version:
              # No concurrent writes, overwritten_version is the latest write.
              self.val_list.append(val)
              return [val], overwritten_version+1
            else:
              # Other writes that current write is unaware of
              self.val_list.append(val)
          		concurrent_vals = self.val_list[overwritten_version+1:]
              return concurrent_vals, len(self.val_list)-1
      ```

      This algorithm ensure that no data is silently dropped (unlike LWW), but requires client to merge concurrent writes. Consider this as a shopping cart. If the clients only add items, thenm `merge_vals` can just take the union. However, to support deletion, *tombstones* must be used. 

      This operation is usually called *merging siblings*. Merging siblings in application code can be error-prone and complex. There are approaches to make this easier: Conflict-free replicated datatypes (CRDTs), Mergeable persistent data structures, operational transformations.

    - Version vector

      The above example has only one server/replica, so only one version number. When there're multiple replicas, we need one version number *per replica*. Each replica increments its own version number when processing a write, and also keeps track of the version numbers it has seen. This collection of version numbers from all replicas is called a *version vector*. It's simply to the algorithm above.



### Chapter 6. Partitions

Partitionings = Sharding. Each record belongs to only one partition.

Outline: Partitioning large datasets, data indexing with partitions, rebalancing, database request routing

#### Partition and Replication

Partition and replication are usually combined. Each record belows to only one partiiton, but can be replicated on multiple replicas. A node may store multiple partitions. Each node maybe the the leaders for some partitions and followers for other partitions.

#### Partitoning Key-Value Data

- Potential problem: skewed partitions, hot spot
- Possible key distribution schemes
  - Random distribution. 
    - Pro: Avoid hot spots. 
    - Con: When query a record, you have to query all nodes since there's no order.
  - Partitioning by key range.
    - Mechanism: Key ranges might not be evenly spaced, as the data might not be evenly distributed.The partition boundaries can be manually configured, or automatically chosen by the database. In each partition, keys are sorted.
    - Pro: easy range scans; efficient query within one partition (as data is sorted)
    - Con: certain access patterns lead to hot spots. For example, if we are appending monotonically-increasing keys, the latest partition becomes the hot spot.
  - Partitioning by key hash
    - Mechanism: each partition gets a range of hash values (instead of key values). 
    - Pro: avoid hot spot and skewed data
    - Con: cannot do range query
  - Compromise bewteen key and hashed-key
    - Cassandra provides *compound primary key*, consisting of several columns. One the first column is hashed to determine the partition, while remaining columns are used as a concatenated index for sorting data.
    - A query cannot use the first column for range query. However, if it specify a fixed value for the first column, it can perform efficient range query over the remaining columns.
    - This design can fit into the user case of many applications.
- Skewed workloads and relieving hot spots
  - Hashing doesn't avoid hot spot entirely. In the extreme case, one extremely hot key becomes the hot spot.
  - Solution: for a known **hot** key, add a random suffix/prefix, so that the hashed value is distributed. Different partitions can handle reads/writes in parallel. Two costs: (1) either concurrent writes on different partitions are handled by conflict resolution algorithm; (2) or each read must query all partitions and combine the results.
  - The above technique should be used only for hot keys. For majority of keys with low throughput, this is inefficient.

#### Partitioning and Secondary Index

- document-based partitioning (aka *local index*)
  - Mechanism: each partition maintains its own secondary index, covering only the documents in that partition.
  - Pro: easy to keep the secondary index in sync with the data
  - Con: query using the secondary index requires scatter-and-gather. You need to query all partitions.
- term-based partitioning (aka *global index*)
  - We need a global index that covers all data, but it must also be partitioned: otherwise the index itself can be the bottleneck.
  - Mechanism: the index is partitioned by the *term* (column name in relational model). The index can be distributed by either the term itself, or a hash of the term. Partitioning by term enables range scans, where partitioning on a hash reduces hot spots.
  - Pro: avoids scatter-and-gather in document-based index.
  - Con: writes are more complicated. One write to a single document may affect multiple partitions of the index. This may require distributed transaction or asynchronous updates.

#### Rebalancing Partitions

- Rebalancing: adding/removing hardware. 
- Requirements:
  - Load fairly distributed
  - While rebalancing is happening, the database should continue functioning
  - For efficiency, minimum data should be moved bewteen nodes
- Strategies
  - Hash mod N
    - Problem: when N changes, most of the keys must be moved.
  - Fixed number of partitions: create many more partitions than nodes and assign several partitions to each node. When new nodes join, it steal a few partitions from each existing node. When a node is removed, the reverse happens. 
    - Mechanism: number of partitions is not changedl; the assignment of keys to partitions is not changed; only the assignment of partitions to nodes change.
    - Number of partitions is fixed when the database is created (as splitting/merging partitions is complicated). Choosing the right number of partitions is difficult: it should be large to allow more machines to join; it should be small to avoid the overhead. This is even harder when the dataset size is highly variable.
    - Also, when the dataset grows unexpectedly large, each partition could be very large.
  - Dynamic partitioning (fixed partition size): when a partition grows to exceed the configured size, it's split into two partitions; when a partition shrinks below some theshold, it's merged with an adjacent partition. 
    - Mechanism: the size of each partition is in a configured range, and the number of partitions adapt to the data volume.
  - Partitioning proportionally to nodes (fixed number of partitions per node): make the number of partitions proportional to the number of nodes. 
    - Mechanism: when a new node joins, it randomly chooses a fixed number of existing partitions to split, and takes ownership of one half of each split partitions, leaving the other half in place.

#### Request Routing

- Problem: how to route client request to node?

- An instance of a more general problem called *service discovery*

- Approaches

  - Allow clients to contact any node. The contacted node serves data if it can, otherwise forwards request to the approriate node, receives the reply, and passes reply back to client
  - Send all client requests to a routing tier, which determines the node to be contacted, and forward the request accordingly. This routing tier acts as a partition-aware load balancer.
  - Require that clients be aware of the partitioning and the asignment of partitions.

  The key problem is the same: how does the decision-making component discover changes in partition?

  Typically two ways:

  - A separate coordination service like ZooKeeper
  - *gossip protocol* among nodes to disseminate changes in the cluster state



### Chapter 7. Transaction

> Some authors have claimed that general two-phase commit is too expensive to support, because of the performance or availability problems that it brings. We believe it is better to have application programmers deal with performance problems due to overuse of transactions as bottlenecks arise, rather than always coding around the lack of transactions. (James Corbett et al., Spanner: Google’s Globally-Distributed Database, 2012)

The database can crash at awkward times; concurrent writes can cause problem; transactions provide the abstraction of several read/write operations execute atomically. Transactions also eliminate partial failure, making error handling much easier (just retry).

#### The Slippery Concept of a Transaction

- Atomicity, Consistency, Isolation, Durability (ACID)

  - Atomicity

    In multi-thread programming, atomicity means no threads observe partial result; in ACID, atomicity is NOT about what can be observed in the context of concurrency (which is covered by isolation). ACID atomicity describes what happen if  the database crashed in the middle of a series of writes: the transaction cannot be completed (*committed*) and is *aborted*. The database must discard/undo any writes done.

  - Consistency

    ACID consistency refers to **application-specific** notion of the database being in a "good state" - it depends on the application's notion of invariants. Atomicity, isolation, and durability are properties of the database, whereas ACID consistency is a property of the application. C doesn't really belong to ACID!

  - Isolation

    ACID isolation means concurrently executing transactions are isolated from each other. 

  - Durability

    ACID durability is the promise that if a transaction has committed, the data can be read later even if the database crashes.

- Single-object and multi-object operations

  Requirements: atomicity and isolation

  - Single-object writes

    Atomicity can be implemented using a log for crash-recovery. Isolation can be implemented using per-object lock.

  - Multi-object transaction

    Multi-object transactions make error handling much easier.

#### Weak Isolation Levels

- In theory, isolation should provide *serializability*: the same effect as if all transactions run serially. HOwever, serializable isolation is expensive. It's common practice to trade isolation for performance.

- Definitions

  - Dirty read: read uncommitted data. 

  - Why a database should avoid dirty read? 

    - Dirty read outcome can be confusing for user.
    - Dirty read means changes made by uncommitted transaction, which will be rolled back, can be read.

  - Dirty write: overwrite uncommitted data.

  - Why a database should avoid dirty write?

    Conflicting writes from different transactions can mix up, causing data inconsistency.

- **Read Uncommitted**

  - Property: No dirty read, doesn't prevent dirty write

- **Read Committed**

  - Property: No dirty read, no dirty write
  - Implementation
    - Prevent dirty write: using row-level lock. The lock is held until the transaction commits/aborts.
    - Prevent dirty read: 
      - Option 1: use the same row-level lock. Problem: one long-running write transaction blocks all read-only transactions.
      - Option 2: For every object written, the database maintains both the old committed value and new value written by the transaction currently holding the write lock. While the transaction is going on, any other transaction can only read the committed old value. When the transaction finishes, the new value can be read. 

- **Snapshot Isolation** (a.k.a repeatable read)

  - Read committed doesn't prevent *non-repeatable read*. Non-repeatable read: a row is retrieved twice in one transaction, but the value differs between reads. This can happen when some write transactions happen between the two reads.

  - Why a database should avoid non-repeatable read? Non-repeatable read is unacceptable in some workloads. Example: Backup a database involves long-running read of the entire database. If nonrepeatable read happens, the backup is inconsistent.

  - Snapshot isolation: each transaction reads from a *consistent snapshot* - data committed at the start of the transaction. Even if new transactions commit, the running transaction sees only the old data from the snapshot.

  - Implementation

    - Prevent dirty write: using row-level lock. The lock is held until the transaction commits/aborts.

    - Prevent nonrepeatable read: doesn't require locks. *Readers never block writers, and writers never block readers*. This allows long-running reads of a consistent snapshot, while simultaneously processing writes normally.

      The database uses a generalization of the mechanism for preventing dirty reads in read committed isolation. It keeps different committed versions of an object - the technique is called *multi-version concurrency control* (**MVCC**).

      To provide read committed isolation, but not snapshot isolation, it's sufficient to keep two versions of an object: the committed version, and the overwritten-but-not-yet-committed version.

      When a transaction starts, it's given a unqiue, ever-increasing transaction ID (`txid`). Whenever a transaction writes anything to the database, the written data is tagged with the `txid` of the writer. Each row has a `created_by` field for the transaction that inserts the row, and a initially emptty `deleted_by` field for the transaction that deletes the row. When deletion happens, the row is not actually deleted but marked for deletion by setting the `deleted_by` field. A garbage collection will remove rows marked for deletion and unaccessible by any transaction. An update is internally translated into a delete and a create. Different versions of the same piece of data is stored as different rows/objects.

      When a transaction reads, `txid`s are used to determine which versions are visible/invisible. Visibility rule:

      - At the start of each transaction, the database makes a list of all other in-progress transaction. Any writes made by those transactions are ignored (regardless of whether they commit or abort).
      - Any writes made by transactions with a larger `txid` is ignored (regardless of whether they commit or not).
      - All other writes are visible to the transaction.

      Or put another way, an object is visible if:

      - When the reader's transaction started, the transaction that created the object had already committed, **and**
      - The object is not marked as deleted, or if it is, the deleter's transaction had not committed when the reader's transaction started.

- Lost updates

  Read committed and snapshot isolation is primarily about the guarantees of **what a read-only transaction can see in the presence of concurrent writes**. What's the guarantees for two concurrent writes? 

  One of the best know problem is *lost update*s. Consider an application that reads some value from the database, modifies it, and writes back the modified value (a read-modify-write cycle). If two transactions do this concurrently, one of the transaction can be lost, as the second write doesn't include the first modification. An example is lost updates in concurrent counter increments.

  Solutions:

  - Atomic write operations provided by the database, usually implemented by internal locking.
  - Appilcation can explicitly require locking.
  - Automatically detecting lost updates by transaction manager (for example, using Optimistic Concurrenty Control). 
  - Compare-and-set.

  Note that snapshot isolation doesn't prevent lost updates. Consider two concurrent increments to a counter:

  ```
  A: 1<-read(x)  x = x+1  write(x, 2)  ok<-commit() 
  B: 1<-read(x)  x = x+1                             write(x, 2)  ok<-commit
  ```

  There's no dirty writes or nonrepeatable reads. Adding another read after `write(x, 2)` will return `1`, the old value (this is the guarantee of snapshot isolation). Without other actions, the only isolation level that prevents lost updates is serializability.

- *Write skew*

  Lost update: Two transactions concurrently read an overlapping data set, concurrently make updates to **the same objects**, and concurrently commit, neither seeing the update performed by the other.

  Write skew: Two transactions concurrently read an overlapping data set, concurrently make **disjoint updates**, and concurrently commit, neither seeing the update performed by the other.

  Write skew and lost updates are similar, but differs in what objects the transactions write to. Solutions to prevent write skew:

  - Atomic single-object operations avoid lost updates, but don't help here, as multiple objects are involved.
  - Automatic detection of lost updates doesn't help, also because multiple objects are involved.
  - Ask for serializable isolation.
  - Application can explicitly require locking.

  Patterns of write skews: (1) a read query for some condition (e.g., some row exists; some row has a certain value, some row doesn't exist); (2) Depending on the read, the application decides to do a write and commits. The effect of the write makes the condition in (1) invalid.

  This effect, where a write in one transaction changes the result of a search query in another transaction, is called a *phantom read*. Snapshot isolation avoids phantom reads in read-only queries, but doesn't prevent phantoms in read-write transactions. 

#### Serializability

Serializable isolation is the strongest isolation level. It guarantees that even though transactions can execute in parallel, the end result is the same as if they had executed serially, without concurrency. Serializability protects all race conditions, including lost updates and write skew. There are three main techniques to provide serializability.

- Technique 1: actual serial execution

  Idea: one transaction at a time, on a single thread.

  This is not used until recently, as a single thread is natually slower than multi-threaded concurrency. However, as RAM gets cheaper, it's feasible to keep all data in memory, so multi-threading is not needed to avoid waiting for disk I/O. Also, OLTP transactions are typically short and fast, while long-running OLAP operations can be executed on a consistent snapshot, separate from the serial execution loop.

  Interactive multi-statement (making a query, read the result, and make other queries depending on the previous query) is not allowed in single-threaded serial transaction processing, as it would block all other transactions. The application must submit the entire transaction code to the database ahead of time, as a *stored procedure*. 

  Stored procedure and in-memory data makes single-threaded transaction processing feasible. However, the performance is limited by one CPU. To scale out to more CPUs, you can partition the data and give each CPU one partition. However, cross-partition transactions require locking and are much slower than single-partition ones.

- Technique 2: two-phase locking (2PL)

  Probably the most widely used, the most straight-forward algorithm for serializability, notorious for performance.

  In snapshot isolation, *readers never block writers, and writers never block readers*. In 2PL, readers&writers block readers&writers.

  2PL is implemeted by having a lock on each object. The lock can be in *shared mode* or *exclusive mode*, much like a `RWMutex`.

  Serializable isolation must prevent phantoms. 2PL implements this with the *predicate lock*, which doesn't belong to a particular object/row, but belongs to all objects that match some search conditions (a predicate).

  Predicate locks don't work well practically, as checking for matching locks can be expensive. *Index-range locking* is a simplified approaximation of predicate locking. An approximation of predicate is attached to one of the indexes. If no suitable index to attach the lock, the database can fallback to a shared lock on the entire table.

- Technique 3: optimistic concurrency control

  2PL is a persimistic concurrency control mechanism, and serial execution can be seen as persimistic to the extreme. Persimistic concurrency control: if anything bad can happen (accessing shared data, accessing the same row/object), wait until it's safe (maybe by adding locks).

  By contrast, *Serializable Snapshot Isolation* (SSI) is an optimistic concurrency control technique. Optimistic concurrency control: just perform the operation, but check that nothing bad happens before commit. If so, abort and retry. If there's lower contention, OCC is preferable due to less overhead than PCC; if there's high contention, OCC is slow due to large number of aborts and retries. 

  SSI is based on snapshot isolation, so all reads are made from a consistent snapshots. On top of snapshot isolation, SSI adds an algorithm for detecting serialization conflicts to determine when to abort.

  The idea is to detect decisions made on an outdated premise. When the application makes a query, the database doesn't know how the application logic uses the query result. To be safe, the database must assume that any changes in the query result mean that the writes in that writes may be invalid. There're two cases to consider:

  - Stale MVCC reads (Uncommitted write occurred **before the read**)

    ```
    Transaction 0  							  read(k1)  write(k2, v2)                abort/commit
    Transaction 1  write(k1, v1)                           commit/abort
                     ^-- uncommitted write before read,
                         ignored by MVCC visibility rule
    ```

    In snapshot isolation, a transaction ignores writes by any other uncommitted transactions. However, if the writes are committed, the ignored writes will take effect and invalidate the premise.

    To avoid this anomaly, the database tracks the writes ignored by each transaction. If any of the ignored writes are have been committed when the current transaction wants to commit, this transaction must abort.

    Note that the abort is delayed until making the commit. Why not abort the transaction immediately when detecting writes to objects/rows related to the premise? For read-only transaction, the abort is unnecessary. Moreever, the writes might not commit and might just be aborted. By avoiding unnecessary aborts, SSI preserves snapshot isolation's support for long-running reads from a consistent snapshot.

  - Writes that affect previous reads (The write occurred **after the read**)

    ```
    Transaction 0 read(k)            write(k, v)                commit
    Transaction 1          read(k)                 write(k, v)         abort
    ```

    SSI uses index-range locks, similar to that in 2PL. However, in SSI, the index-range locks doesn't block other transactions, but just serve as a mark for conflict detection, and can be removed after the transaction commits/aborts. 

    When reading data, an index-range lock is attached. When writing to the database, the transaction looks for any other transactions that have read the data to be overwritten. Instead of blocking for the lock, the transaction does the write, and notifies the transaction that have read the overwritten data.

    In the example. transaction 0 first notifies transaction 1 that its previous read is outdated, and then vice versa. Then transaction 0 commits successfully, because transaction 1's write is not committed yet and hasn't taken effect. When transaction 1 tries to commit, the conflicting write has been committed, and must abort.

  Compared to 2PL, SSI has the advantage of one transaction not blocking others. Like in snapshot isolation, writers don't block readers, and vice versa. Compared to serial execution, SSI is not limited to one core. The rate of aborts significantly affects the performance of SSI.

- Summary

  | Isolation level                      | Guarantees                                        |
  | ------------------------------------ | ------------------------------------------------- |
  | Read uncommitted                     | no dirty read;                                    |
  | Read committed                       | no dirty read; no dirty write.                    |
  | Snapshot isolation (repeatable read) | repeatable read; no dirty write.                  |
  | Serializable isolation               | repeatable read; no lost update; no phantom read. |

  Only serializable isolation protects against all problems. 



### Chapter 8. The Trouble with Distributed Systems

### Chapter 9. Consistency and Consensus





## Part III. Derived Data



























