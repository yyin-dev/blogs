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























