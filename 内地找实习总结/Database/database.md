### Transaction

A database transaction symbolizes a unit of work performed within a DBMS against a database, and treated in a coherent and reliable way independent of other transactions. A transaction generally represents any change in a database.

A transaction should be ACID.

commit: after all instructions of a transactions are successfully executed, the changes by made transaction are made permanent in the database.

rollback: if a transaction is not able to execute all transactions successfully, all the changes made by transactions are undone.

### ACID

- Atomicity，原子性

    The transaction must be atomic. All operations in a transaction either happen together, or none of them happens. 

- Consistency，一致性

    All data would be valid according to some defined rule, before and after each transaction.

- Isolation，隔离性

    The effect of a transaction is not visible to other transactions until it's completed.'

- Durability，持久性

    Once a transaction is completed(aka commited), it should remain in system even if the system crashes immedidately after the transaction. So the change should go to the disk.

A transaction is correct only if the system is consistent.

Without concurrency, all transactions are executed in sequence and isolation is guaranteed. So if atomicity is ensured, then consistency is ensured.

With concurrency, each transaction must be isolated and atomic, to ensure consistency.

Atomic + isolation = consistency, if we assume durability.

In other words, consistency is the **goal** and atomicity, isolation and durability are the **ways** to achieve the goal.



### Consistency issue under concurrency并发事务的一致性问题

- Lost update，修改丢失: concurrent writes to the same data. 
- dirty read，读脏数据: reads **uncommitted** data.
- Non-repeatable read，不可重复读: two consecutive reads to the same **row** in the same transaction return different values.
- phantom problem，幻影读: two consecutive reads to the same **rows**  in the same transaction return different values.

Note that non-repeatable read focuses on a single row, while phantom problem focuses on a range of rows in a table.

Examples: https://blog.csdn.net/yaotai8135/article/details/79952172



### Isolation level

The isolation levels are listed from lowest to highest.

- Read uncommited

    One transaction can read uncommited changes by other transactions, allowing dirty read.

- Read commited

    Only  commited changes can be read, eliminating dirty read. However, this can still allow non-repeatable read, as two reads can be interleaved by a commit.

- Repeatble read

    Avoid non-repeatable read.

- Serializable

    The execution of operations in which concurrently executed transactions appers to be serially executing. This effectively means that concurrent reads are allowed but no currency between read/write or write/write.

![img](https://media.geeksforgeeks.org/wp-content/cdn-uploads/transactnLevel.png)



### Schedule

Source: https://www.geeksforgeeks.org/types-of-schedules-in-dbms/

![img](https://media.geeksforgeeks.org/wp-content/cdn-uploads/20190813142109/Types-of-schedules-in-DBMS-1.jpg)

Serial schedule: a transaction cannot start until the previous is completed. Low throughput.

Non-serial schedule: aka concurrent schedule. Operations of a transaction are interleaved with opeartions of other transactions. High throughput but consistency issue.

Non-serial schedule is categorized into:

- Serializable

    **A serializable schedule guarantees consistency.** A non-serial schedule is serializable iff it's **equivalent** to the serial schedules. A subset is called conflict serializable.

    - Conflict serializable (commonly implemented)

        A schedule is conflict serializable if it can be transformed into a serial schedule by swapping non-conflicting operations. 

        Two operations are conflicting if all conditions below hold:

        1. They belong to different transactions
        2. They operate on the same data item
        3. At least one of them is a write

        To determined whether a non-serial schedule is conflict seriazable, see if you can find a serial schedule why swapping.

    - View serializable

        Two schedules are view-equal if the follow holds:

        - Initial read
        - updated read
        - final write

        A schedule is view serializable if it is view-equal to a serial schedule.

        To determined whether a non-serial schedule is view seriazable, see if you can find a serial schedule satisfying the 3 constraints above.

        Source: https://www.geeksforgeeks.org/view-serializability-in-dbms-transactions/

- Non-serializable

    Divided into two types.

    - Recoverable schedule

        A schedule is recoverable if each transaction T commits only after transactions whose changes T read from are committed. 

        If `Ta` reads value written by  `Tb`, then `Ta` commits after the commit of `Tb`. Example:

        ```
        Ta	     Tb
        -----    -----
        R(X)	
        W(X)	
                 R(X)
                 W(X)
                 R(X)
        commit	
                 commit
        ```

        This schedule is recoverable since `Tb`, which reads value written by `Ta`, commits later than `Ta`.

        Three types of recoverable schedules.

        - Cascading schedule

            A failture in one transaction leads to the rollbacks/abortions other dependent transactions. This can waste CPU time.

            ```
            Ta	     Tb       Tc
            -----    -----    ------
            R(X)	
            W(X)	
                     R(X)
                     W(X)
                              R(x)
                              W(x)
            failure
            ```

            In the example above, `Tb` depends on `Ta` and `Tc` depends on `Tb`. The failure of `Ta` cause `Tb` and `Tc` to rollback.

            Note that `Tb` and `Tc` cannot commit before `Ta`, otherwise the schedule is not recoverable.

        - Cascadeless schedule

            A transaction reads values only after all transactions whose changes it'll read are committed. This avoids the situation where failure in one transaction leads to a series of transaction rollbacks. 

            So `Ta` can **read** value written by `Tb` only after `Tb` commits.

            This is like an enforcement on the original restriction of recoverable schedule. 

        - Strict schedule

            For any two transactions `Ta` and `Tb`, if a write operation of `Ta` precedes a conflicting operation of `Tb`, then the commit/abort of `Ta` also precedes that conflicting operation of `Tb`. 

            So `Ta` can **read/write** values written by `Tb` only after `Tb` commits.

            This's like a further enforcement on cascadeless.

        So we can see 

        1. Cascading is just a rule for recoverable schedules to recover on failure. So every recoverable schedule is already a cascading schedule. 
        2. Cascadeless schedules are stricter than cascading schedule, and strict schedules are stricter than cascadeless schedules. 
        3. Additionally, serial schdules satisfy all constraints of all recoverable, cascadless and strict.

        4. Comparison

            - Recoverable

                If `Ta` reads value written by  `Tb`, then `Ta` commits after the commit of `Tb`.

            - Cascadeless

                `Ta` can **read** value written by `Tb` only after `Tb` commits.

            - Strict

                `Ta` can **read/write** values written by `Tb` only after `Tb` commits.

        So we have:

        ![img](https://media.geeksforgeeks.org/wp-content/uploads/Types-of-schedules.png)

    - Non-recoverable schedule



Note that any combination of serializability and recoverability is possible:

- Serializable and recoverable
- Serializable and not recoverable
- Not serializable and recoverable
- Not serializable and not recoverable



### Concurrency control protocol

Goal: non-serial, conflict/view serializable, recoverable (preferably cascadeless) schedule.

Concurrency control protocols tries to achieve the goal. Categories:

- Lock based protocol
    - Basic 2-PL (Two phase locking)
    - Conservative 2-PL
    - Strict 2-PL
    - Rigorous 2-PL
- Graph based protocol
- Timestamp ordering protocol
- Multiple granularity protocol
- Multi-version protocol



## Lock Based Protocol

Solution to consistency issue under consistency.

Two locking granularity （粒度）: on row, on table.

A balance between the level of concurrency and the cost of locking. 

- Exclusive/Shared Lock

    - Exclusive Lock (Read-only)

        Aka X lock. Exclusive lock needs to be acquired to write. Only one party can hold a exclusive lock to a data.

    - Shared lock (Read/Write)

        Aka S lock. Shared lock needs to be acquired for reading. Multiple parties can hold a S lock to a data at the same time, as they are not modifying the data.
    
- Lock upgrade/downgrade

    - S(A) can be upgraded to X(A) if Ti is the only transaction holding S lock on A.
    - X(A) can be downgraded to S(A) at any time.

But simply applying locks isn't enough. New problems: 

1. starvation; 
2. deadlock; 
3. no guarantee of serializability.



#### Basic 2-PL

- Locking and unlocking is done in two phases

    - Growing phase

        Can only acquire/upgrade lock, cannot downgrade/relaese lock

    - Shrinking phase

        Can only release/downgrade lock, cannot acquire/upgrade lock.

    The phase between growing phase and shrinking phase is called Lock Point. Note that there's **no restrictions on the time of commit** in basic 2-PL. In other words, the shrinking phase could start before/after the commit.

- **2-PL ensures serializability**

    It can be proved that that transactions can be serialized in the order of lock point.

- Drawbacks of 2-PL

    - Cascading rollback possible

        ```
        T1			T2
        ------- 	--------
        XLock(A)
        XLock(B)
        A = A + B
        Unlock(A)   XLock(A)   
        ...			Read(A) <--- dirty read: read uncommitted data
        ...         ...
        failure
        ...
        commit
        ```

        The dirty read causes the cascading rollback problem.

    - Deadlock possible

        ```
        T1			T2
        ------- 	--------
        XLock(A)
        			XLock(B)
        XLock(B)
        			XLock(A)
        ```

- In fact, basic 2-PL does not ensure recoverability.

    ```
    T1			T2
    ------- 	--------
    XLock(A)
    Write(A)
    XLock(B)
    Unlock(A)   XLock(A)   
    ...			Read(A) <--- dirty read: read uncommitted data
    			Write(A)
    			commit  <--- committed!
    			Unlock(A)
    ...         ...
    failure    
    ...
    commit
    ```



**strict schedule**: if a write of `Ti` preceeds a conflicting operation of `Tj`, `Ti` **commits/aborts** before the **conflicting opeartion** of `Tj`.



#### Strict 2-PL

All **exclusive(X)** locks held by the transaction are released **after** the transaction is committed. 

Strict 2-PL ensures **cascadeless recoverability** and **strict schedule**.

Deadlock possible.



#### Rigous 2-PL

All **exclusive(X) and shared(X)** locks held by the transaction are released after the transaction is committed.

Strict 2-PL ensures **recoverable and cascadeless.**

Deadlock possible.



#### Conservative 2-PL

Aka **static 2-PL**. The transaction must lock all items it may access before the transaction begins, by predeclaring the read-set and write-set. If any of the items cannot be locked, the transaction doesn't lock any and wait.

**Deadlock free**, as there's no *hold-and-wait* phase, which is one of the four necessary conditions for deadlock.

There's no restriction on commit time. So **cascading callback** possible and no strict schedule.

But not very practical as it requires predeclaring read-set and write-set.



#### Summary

Major problem of lock based problem: avoid deadlock + ensure strict schedule. Strict/Rigorous 2-PL ensure strict schedule but deadlock is possible, while conservative 2-PL avoid deadlock but cannot guarantee strict schedule.



## Timestamp Ordering Protocol

#### Transaction Timestamp (TS(`Ti`))

A timestamp is a unique identifier created by the DBMS to identify a transaction. They are usually assigned in the order in which they are submitted to the system, so a timestamp may be thought of as the transaction start time. If `T1` starts before `T2`, then TS(`T1`) will be less than TS(`T2`).

#### Deadlock Prevention using Timestamp

Two schemes: *wound-wait* and *wait-die*.

Say there are two transactions `Ti` and `Tj`, now say `Ti` tries to lock an item X but item X is already locked by some `Tj`,

- wait-die

    **Non-preemptive**. An older transaction is allowed to wait for a younger trasnaction, whereas a younger transaction requesting an item held by an older transaction is aborted and restarted. In this example, if TS(`Ti`) > TS(`Tj`), it would be **aborted and restarted after an random delay with the same timestamp**; otherwise, `Ti` waits.

- wound-wait

    **Preemptive**. The opposite to wait-die. A younger can wait for a older, but an older requesting an item held by a younger would make the younger abort and restart later with the same timestamp.

Both schemes prefer older transactions, with the assumption that aborting the younger transaction would induce less cost. The two schemes eliminate deadlock: no cycle is possible as we are waiting in a determined order.

TODO: Multiple Granularity Locking

TODO: Indexing