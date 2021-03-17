# About PingCAP

For around a year, I have been very passionate about PingCAP - probably because it's one of the most admired, and widely-discussed company in the study group of MIT6.824, CMU15445, etc. I heard about the names of TiDB, TiKV, Raft, distributed storage, but I always believe that I am not good enough to apply for an internship.

So I spent around a year studying. I studied MIT6.828 for OS, MIT6.824 for distributed system, CMU15445 for database - and finally I believe that I acquired all the fundamentals to build a distributed database. Also, I received the MSCS offer from CMU, which is a great approval for my knowledge and skills. Thus, I decide to apply for an internship at PingCAP.

Rather than blindly prepare for algorithm problems, OS, network, compiler, distributed system, I decide to take a look at what's TiDB. I spent several days, reading this [blog series](https://pingcap.com/blog-cn/#TiDB-%E6%BA%90%E7%A0%81%E9%98%85%E8%AF%BB) and working on the [talent plan](https://university.pingcap.com/talent-plan/implement-a-mini-distributed-relational-database). The code is not easy, neither the talent plan project. Usually I know what the database should do, but mapping the knowledge to actual source code can be painful and tedious.

While working on these, I constantly compare TiDB with *bustub*, the project I worked on for CMU 15445. Surprisingly, I find that the parsing-validating-planning-optimizing-execution workflow is exactly the same as what I learned in CMU15445, where I built several important components of a single-node database. 

Then I come across this series explaining the overall architecture of TiDB.

- TiKV: https://pingcap.com/blog-cn/tidb-internal-1/

- TiDB: https://pingcap.com/blog-cn/tidb-internal-2/

- PD: https://pingcap.com/blog-cn/tidb-internal-3/

The blogs are not hard to read, as I have acquired all necessary concepts in MIT6.824, CMU15445, and reading DDIA. Here's my thought. As a MySQL-compatible fault-tolerant distributed database, TiDB mainly relies on the underlying TiKV, the distributed key-value storage, to provide fault-tolerance. What TiDB provides a MySQL-interface compatible abstraction over the underlying storage engine (and create query plans aware of the underlying storage engine). PD works on a even higher level, orchistrating different TiDB clusters. In simplest word, TiKV provides storage ability, while TiDB provides the MySQL interface over the storage engine. If you use `UniStore` in TiDB, it's the same as MySQL.

As for TiDB layer (parsing/planning/optimization), it's similar to any other relation database (MySQL, PostgreSQL). However, I am more interested in distributed storage, which is mainly provided by TiKV. Thus, I decide not to spend any more time on TiDB - if I want to see how planning/optimization is done, I can read PostgreSQL, which has much higher code quality. 

Whether to learn Rust and explore TiKV? Let's see.