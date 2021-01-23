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



## Part III. Derived Data





