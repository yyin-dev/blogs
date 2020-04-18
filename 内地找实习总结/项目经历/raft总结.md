#### CAP Theorem

CAP theorem: Consistency, Availability, Partition tolerance.

- Consistency: every read sees previous write
- Availability: the system is functioning all the time
- Partition: any number of packets can be dropped

The theorem states that a distributed cannot achieve all three at the same time. 



# Raft

- Replicated state machine: usually implemented with a replicated log
- Job of consensus algorithm: **keep the replicated logs consistent**
- Properties of typical consensus algorithm:
    - Safety: never return incorrect result
    - Availability: the system is fully functioning as long as a majority are operational
    - The correctness of the system doesn't depend on timing
    - A command can complete as long as a majority of the cluster has responded to a single round of RPCs. A minority of slow/unreliable servers doesn't affect the performance of the system.
- Problem with Paxos: poor understandability, hard to build practical implementation.
- Raft is **a consensus algorithm for managing a replicated log**.

#### Raft basics

- Three states: follower, candidate, leader
- Followers are passive and issue no requests on their own, but simply respond to request from candidate/leader.
- The leader handles all client requests (recall Lab3).
- Raft divides time into *term*s, which act as the logical time units. Each term begins with an election (recall that `currTerm` is only advanced when the follower-candidate transition happens). Term is exchanged in **every RPC** between servers and is used to detect obsolete information. If one server's `currTerm` is smaller than the term in the RPC request/response, it updates its `currTerm`. If a candidate/leader is out of term, it reverts to follower immediately. If a server receives a request with a stale term, it rejects it.
- Three RPC for communication between servers: `RequestVote`, `AppendEntries`,  `InstallSnapshot`.



#### Leader Election

- A leader is given complete responsibility for managing replicated log.

- A server starts out as a follower. A server remains a follower until election timeout happens. The election timeout is reset every time a follower receives RPCs from a **leader/candidate**.

- When election timeout happens, follower transits to candidate, and begins a new round of election. It increments `currTerm`, change state to candidate, vote for itself, and issues `RequestVote` to other servers. It remains in this state until:

    - It wins the election by receiving vote from a majority of servers. Then it transits to leader and immediately issues heartbeat to all servers to prevent new elections.
    - It doesn't receive enough votes and election timeout happens again. Then it restarts another round of election. This happens with split vote happens. 
    - It receives`AppendEntries` from another leader with term **greater or equal to** its `currTerm`. Then the candidate reverts to follower. If the `AppendEntries`'s term is smaller than `currTerm`, it rejects the request.

    ![state_transition](state_transition.jpg)

- Raft, essentially overlapping majority, guarantees that at most one leader can be elected in any term.

#### Log Replication

Leader replicates log entries on other servers.

- When receiving request, leader appends command to log as new entry, and issues `AppendEntries` in parallel to other servers. When the entry is safely replicated (committed), the leader applies the entry to its state machine and respond to the client. 
- A leader retries indefinitely until all followers' log is consistent with (the same as) its log. Note that in actual implementation, the time interval between two consecutive tries must be considered.
- Each log entry contains a command and `receivedTerm`. It also has an associated `logIndex`.
- **Raft guarantees that committed entries are durable and will eventually be applied to all available state machines**. An entry is committed when being replicated on a majority of servers in the cluster. The leader keeps track of the `commitIndex` and include it in `AppendEntries` to inform other servers (the decision whether an entry is committed is made by the leader and otherwise unknown to other servers). Once a follower knows an entry is committed, it applies it to its state machine. 
- *Log Matching Property*
    - If 2 entries in 2 logs have the same log index and term, they store the same command.
    - If 2 entries in 2 logs have the same log index and term, the 2 logs are identical up to that point.
- Crashes lead to log inconsistency. A follower may be missing terms from leader, or have extra terms than leader, or both.
- Raft leader forces follower's log to replicate its own. In other words, the leader always have the correct log. This is the *Leader Append-Only Property*.
- To bring a follower's log into consistency, the leader finds the latest log entry where the 2 logs agree, delete any entries in the follower's log after that point, and send the follower all leader's entries after that point. The leader maintains a `nextIndex` for each follower. The `AppendEntries` succeeds only when the follower's log is consistent with the leaders.



#### Safety

- Election restriction

    Guarantee: **Every elected leader's log contains all committed entries in previous terms**. This relies on *overlapping majority* and *log up-to-date check*.

    1. When an entry is committed, a majority of servers have that entry in the log.
    2. The candidate must receive votes from a majority of servers to become a leader. 
    3. The above two mentioned majorities overlap, so there exists on server S who has the all committed entry and votes for the new leader.
    4. By the log up-to-date check, the leader's log is at least as up-to-date as S. So the leader also contains all committed entries. 

    Log up-to-date rules checks the term and index of the last log entry.

- Committing entries from previous term

    Figure 8 shows that

    - If an entry from **current term** is replicated on a majority of servers, it's committed.
    - If an entry from **previous terms** is replicated on a majority of servers, it's not guaranteed to be committed.

    So Raft never commits log entries from previous term by counting replicas. Only log entries from leader's current term are committed by counting replicas. By the log matching property, once an entry from current term is committed, all prior entries are committed indirectly.



#### Log Compaction (Snapshotting)



#### Client Interaction

This is to ensure linearizability. 

- Only leader responds to client request.

- Problem: Raft can execute one command multiple times. Consider if a leader crashes after committing the command but before sending the request back to client. 

    Solution: assign a serial number for each command. 

- Creating a log entry for read-only request can be inefficient. Read-only request can be handled without writing to the log. However, this could return stale data (consider figure 8). Two actions needed:

    - No-op entry

        A newly elected leader needs the latest information about committed entries. It contains all committed entries, but it doesn't know the `commitIndex`. 

        Solution: commit a no-op entry **at the start of each term**, to get correct `commitIndex`.

    - Leadership check

        The leader needs to ensure that it's still the actual leader in the cluster. So **before responding to each read-only request**, it needs to receive heartbeat reply from a majority of servers.

    The leader would not respond to read-only request before both are done.



Raft中文术语：

https://blog.csdn.net/daaikuaichuan/article/details/98627822

