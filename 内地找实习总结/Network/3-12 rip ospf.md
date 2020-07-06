This note is written based mainly on 3.4 of the textbook.

## Routing

- Forwarding and routing

    Forwarding is the process of receiving a packet, looking up the destination address in the forwarding table, and send the packet. Routing is the process by which the forwarding table is built. Forwarding is a simple and well-defined, so it's often referred to as the network's *data plane*. Routing depends on complex distributed algorithm, so it's often referred to as the network's *control plane*.

- Forwarding table and routing table

    The two terms are sometimes used interchangeably, but we make a distinction. The forwarding table must contain enough information to accomplish forwarding. A row in the forwarding table should contain mapping from network prefix to an outgoing interface and some MAC information. On the other hand, a routing table is the built up to facilitate building the forwarding table. It generally contains mappings from network prefix to the IP address of next hop.

    Routing table entry:

    | Prefix/length | Next Hop      |
    | ------------- | ------------- |
    | 18/8          | 171.69.245.10 |

    Forwarding table entry:

    | Prefix/length | Interface | MAC address     |
    | ------------- | --------- | --------------- |
    | 18/8          | intf0     | 8:0:2b:e4:5:1:2 |

    Note that the MAC address above is provided by the ARP (Address Resolution Protocol).

- Intra-domain routing protocols

    Aka *interior gateway protocols*. A *domain* is an internetwork in which all routers are under the same administrative control. Intra-domain routing protocols are designed for small to medium-sized networks.

    Assumption: cost of each link is known.

### Distance-Vector (RIP)

RIP: Routing Information Protocol, based on distance-vector.

Each node constructs a one-dimensional array (a vector) containing the “distances” (costs) to all other nodes and distributes that vector to its immediate neighbors. The assumption for RIP is that each node knows the cost to its directly connected neighbors.

<img src="distance-vector-example.jpg" style="zoom: 50%;" />

Each node executes the "receive - compute - send" cycle. The [example](https://book.systemsapproach.org/internetworking/routing.html#distance-vector-rip) in the textbook is very clear.

- Distance vector are generated at two situations:

    - Periodically.
    - Triggered. When (1) detects a link failure; (2) receives a message causing a change to its routing table. 

- Detect link failure

    There are different ways to detect link failures. One approach is for the node to continually tests the link by sending a control packet and seeing if it receives an acknowledgement. Another approach is to consider a link as failed if the node hasn't receive any periodic routing message for some time.

- Handle link failure and the problem

    Suppose edge FG fails. F sets its distance to G to infinity and passes the information to A. Because in A's routing table, the 2-hop path to G is through F, A set its distance to G to be infinity. However, with the next update from B, A would learn that there's a 2-hop path from C to G. Thus, A would know that it can reach G through C in 3 hops, which is less than infinity. A would update its routing table. The system becomes stable again.

    However, in another case, the system would not be able to stabilize. Suppose edge AE goes down. A would advertises a distance of infinity to E, while at the same time B, C still advertises an outdated distance of 2 to E. Depending on the timing, this could happen: A's new distance vector reaches B and B updates its distance to E to be infinity; C's outdated distance vector reaches B and B updates its distance to E to be 3; A's new distance vector reaches C and C updates its distance to E to be infinity; A receives B's distance vector and updates its distance to E to be 4; ... This cycle continues until the distance reach some extremely large number and only then the system would recognize that E is not reachable. This takes a long time and the situation is known as the *count to infinity* problem.

    The essence of this problem is that the distance-vector doesn't have the notion of a path, but only have a notion of neighbors. 

- Possible solutions to the count to infinity problem

    - Use relatively small number as an approximation of infinity
    - *Split horizon*. When a node sends a routing update to its neighbors, it doesn't send those routes it learned back to that same neighbor. For example, if B has the route (E, 2, A) in its table, then it knows it must have learned this route from A, and so whenever B sends a routing update to A, it does not include the route (E, 2) in that update. The problem with this approach is that it only works for routing loops involving two nodes. This also delays the convergence.

- RIP detail

    RIP differ slightly from distance-vector routing described above:

    - The advertised distance is not distance to router, but distance to networks
    - The period for generating distance-vector message is 30 seconds
    - 16 represents infinity, so RIP doesn't support large network with paths longer than 15 hops
    - packets are sent via UDP
    
    RIP doesn't work well for large networks.



### Link-State (OSPF)

The most widely used intra-domain routing protocol nowadays.

OSPF: Open Shortest Path First, based on Dijkstra's algorithm, but also relies on "reliable flooding" of link state packets to all nodes.

- Main idea

    In link-state routing, all nodes compute shortest paths independently. In distance-vector routing, each node depends on the local computations from its neighbors, while in link-state routing, nodes only rely on information in link state packets.

    The idea is that each node knows how to reach its directly connected neighbors, and we try to disseminate this knowledge to all nodes in the network. This is a sufficient (but not necessary) condition for finding the shortest path to any node in the network. So link-state protocols replies on two mechanism: reliable dissemination of the link-state information, and the calculation of routes from the collection of the link-state information. (The link-state information is the information in the link-state packet, described below.)

- Assumptions

    The same as for distance-vector routing: Each individual node knows all other nodes in the network, and it knows the cost of the links directly connected with itself.

- Reliable flooding

    The process to make sure all nodes get a copy of the information from other nodes. Each link-state packet (LSP) contains: (1) ID of the node that created the LSP, (2) a list of directly connected neighbors of that node, with the cost of the link to each one, (3) a sequence number, and (4) a TTL (time to leave) for the packet. 

    Reliable LSP transmission between adjacent routers is made using acknowledgements and retransmissions. When a node X receives a copy of an LSP **originated** (but might be forwarded by some other node Z) from node Y, X checks if it has already stored a copy of an LSP from Y. If no, it stores the LSP. Otherwise, it compares the sequence numbers; If the new LSP has a larger sequence number, i.e., more recent, X replaces the LSP. If the new LSP has a smaller sequence number, it gets discarded. If the received LSP is the newer one, X sends a copy of the LSP to all its neighbors except the neighbor from which the LSP was received, this helps to bring an end to the flooding of LSP. Eventually, the most recent copy of the LSP would reaches all nodes in the network. 

    Each node generates LSPs either **periodically**, or if one of its **immediate neighbors goes down**.

    The goal of a link-state protocol is to flood the newest information to all nodes **as quickly as possible**, with **minimum cost**.

    From this point, we assume that reliable flooding of link-state information is achieved.

- Route calculation

    Based on Dijkstra's algorithm, almost the same as presented in CS577.

    ```python
    N = the set of nodes in the graph
    M = set of nodes incorporated
    l(i, j) = cost with edge between nodes i, j, and l(i, j) = infinity if no edge
    s = source node
    C(n) = cost of path from s to n
    
    Algorithm: 
        M = {s}
        for each n in N - {s}
            C(n) = l(s,n)
    
        while (N != M)
            M = M + {w} where C(w) is the min for all w in (N-M)
            for each n in (N-M):
            	C(n) = MIN(C(n), C(w)+l(w,n))
    ```

In distributed/practical manner, each switch maintains two lists `Tentative` and `Confirmed`. Each list contains entries of the form `(Destination, Cost, NextHop)`. Once a given node has a copy of the LSP **from every other node**, it's able to compute the distance to every other node (This doesn't involve too much overhead with reliable flooding). The algorithm in action:
1. Initialize the `Confirmed` list with an entry for myself; this entry has a cost of 0.
   
2. For the node just added to the `Confirmed` list, call it node `Next` and select its LSP.
   
3. For each neighbor (`Neighbor`) of `Next`, calculate the cost (`Cost`) to reach this `Neighbor` as the sum of the cost from myself to `Next` and from `Next` to `Neighbor`.
    1. If `Neighbor` is currently on neither the `Confirmed` nor the `Tentative` list, then add `(Neighbor, Cost, NextHop)` to the `Tentative` list, where `NextHop` is the direction I go to reach `Next`.
    2. If `Neighbor` is currently on the `Tentative` list, and the `Cost` is less than the currently listed cost for `Neighbor`, then replace the current entry with `(Neighbor, Cost, NextHop)`, where `NextHop` is the direction I go to reach `Next`.
4. If the `Tentative` List is empty, stop. Otherwise, move the entry with the minimum cost in `Tentative` to `Confirmed`, and go to step 2.

Link-state routing algorithm stabilizes quickly, generates little traffic, has **no problem of count to infinity** and responds rapidly to topology changes. The con is that the amount of information stored at each node can be quite large. This is the fundamental problem of scalability in routing. 

Why OSPF is preferred over RIP? **Faster, loop-free convergence**.



### Metrics for Link Costs

Links with lower cost are more likyly to be in the paths selected by the protocol.

Methods for assigning link costs: 

1. Static assignment. E.g., link cost = 1 (i.e, a hop), propagation delay, bandwidth.

    Pro: simple, low overhead.

    Con: not flexible.

2. Dynamic assignment. E.g., latency (which includes queuing delay), packet volume.

    Pro: adaptability.

    Con: hard to control.

Historically, cost is proportional to queue size. The problem is that the path follows short queues, but not necessarily destinations. Seemingly a good idea but actually is not.

Then cost becomes proportional to average delays over some period of time. This achieves good network utilization, but oscilation is a problem under heavy traffic.