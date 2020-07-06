## Transport Layer

- Basic function of transport layer

    Multiplex and demultiplex between applications and the network. At layer 4, for the first time, we have "end-to-end" virtual connectivity between nodes in different networks. We want to provide "process-to-process" delivery.

- Other functions that might be nice to have at the transport layer:

    - Connection control
    - Error detection
    - Flow control between sender and receiver
    - Reliable transmission
    - In order delivery
    - Congestion control

- Multiplex/Demultiplex

    - Multiplex: gather data from appications, encapsulate in a layer 4 header, send to layer 3. 

    - Demultiplex: examine transport layer headers, decapsulate, send packets to the apps. 

    - Port

        The abstraction that enables multiplexing and demultiplexing, which is a 16-bit identifier designated for both sending and receiving applications.

        Well-defined organization of ports:

        - 0 - 1023: well-known ports.
        - 1024 - 49151: registered ports. Associated with commercial applications.
        - 49152 - 65535: ephermal ports. Randomly assigned to client applications by OS.

        When the client sends a request to ther server, its port is in the header. The real problem is for the client to know the port of the server. This can be solved using well-known ports. 


### UDP

User Datagram Protocol (UDP). **Connectionless**, **unreliable**, **unordered** delivery. It may provide simple bit-level error detection using checksum. UDP header format: 

[<img src="https://book.systemsapproach.org/_images/f05-01-9780123850591.png" alt="../_images/f05-01-9780123850591.png" style="zoom:50%;" />](https://book.systemsapproach.org/_images/f05-01-9780123850591.png)

UDP is used for application that don't reliability, like live streaming. 

UDP packets are "fully defined" when they can be associated with an application unambiguously: they are demultplexed based on `<IP address, port>` pair.

That's everything about UDP, a simple demultiplexer using port.



#### Basics of Reliability

- ACK mechanism

    Goal: reliable transmission. Basic mechanism: ACK, i.e. acknowledgement-based reliability. This implies that the need for a timeout mechanism, called RTO (Retransmit TimeOut). 

    Setting an appropriate RTO is important in this simple mechanism. If the timeout is too long, you can be wasting time waiting; If the timeout is too short, you would be resending data. Let's say the server sends out D1 and the client receives it and sends back ACK1. But since the RTO is too short, RTO fires before ACK1 arrives at the server so server sends D1 again. By the protocol, whenever the server receives an ACK, it sends out the next data packet. So D2 gets sent. If this pattern continues, the server is going to send out twice many packets as needed! Also, the receiver needs to handle duplicates.

    In short, for simple ACK mechanism, setting RTO and handling duplicates are important. Timeout calculation algorithms would be discussed later.

- Problem with *stop-and-wait*

    The simple mechanism where we send one packet and wait for its ACK before sending the next is called stop-and-wait. The problem here is that we may under-utilize the bandwidth in this way.

    To improve performance over stop&wait, we send multiple packets back-to-back that are unacknowledged. We add sequence number to packets, to manage out-of-order packets and lost packets. 

    We need a buffer on the receiver, to store packets when some but not all packets have arrived. The receiver would flush the buffer to the application only when all packets in a sending cycle are received. 

    Having a buffer on a receiver implies that we need to prevent the sender from sending too many back-to-back packets at one time. This is the purpose of *flow control*, where the receiver tells the sender its Receive Window Size (RWS). 

    We also have a buffer on the sender, with size Send Windows Size (SWS).

    Sequence numbers are specified in a field in the transport header and cycle through a sequence number space. At first glance, it seems that it's ok as long as `windowSize <= max sequence number - 1`, but actually we need it to be `2 * windowSize + 1`. Consider the following situation:

    Assume SWS = 7, and sender sends packet of sequence number 0, 1, .., 6. All packets are successfully received, while all ACKs are lost. When RTO fires, the sender retransmits all packets. However, the receiver is not aware of the loss of ACKs and would assume that those are new packets. Thus, we need `2 * windowSize + 1 <= max sequence number`.

- Sliding window enables
    - Better bandwidth utilization
    - reliability
    - in-order delivery
    - flow control



#### Reliable Transmission at Layer 2

Reliable transmission is usually done using *acknowledgement* and *timeouts*. An acknowledgment (ACK) is a control frame, meaning that it's header without payload, although a protocol can *piggyback* an ACK on a data frame it just happens to be sending back. If the sender doesn't receive an ACK after the timeout, it *retransmits* the orignal frame. 

The general strategy of using ACK&Timeout to implement reliable delivery is called *automatic repeat request* (ARQ). We introduce two different ARQ algorithms here.

- Stop-and-Wait

    Main idea: The sender waits for ACK after transmitting each frame.

    Subtlety: duplicated frames. Solution: sequence number.

    Drawback: under-utilization of the bandwidth.

- Sliding Window

    Address the inefficiency of stop-and-wait by making the server sends more frames before waiting for ACKs.

    Each frame is assigned a `SeqNum`. The sender maintains three variables: 

    - *send window size*, `SWS`: upper bound on the number of unacknowledged frames
    - Sequence number of the *last acknowledgment received*, `LAR`
    - Sequence number of the *last frame sent*, `LFS`

    And we have: `SWS >= LFS - LAR`.

    When an ACK arrives, the sender increments `LAR`, thereby allowing a new frame to be sent. The sender associates **a timer with each frame** and retransmits the frame should the timeout fires before the ACK arrives.  

    The receiver maintains three variables:

    - *receive window size*, `RWS`: upper bound on the number of out-of-order frame buffered
    - Sequence number of the *last acceptable frame*, `LAF`
    - Sequence number of the *last frame received*, `LFR` (but might not been acknowledged)

    And we have `RWS >= LAF - LFR`.

    When a frame with `SeqNum` arrives, several cases possible. The pseudo-code as below:

    ```c++
    if SeqNum <= LFR OR SeqNum > LAF:
    	discard // outside the receiver's window
    else: // LFR < SeqNum <= LAF, within receiver's window
    	// Let SeqNumToAck be the max sequence number not yet ACKed,
    	// such that all frames with sequence number <= SeqNumToAck has 
    	// been received.
        Send ACK of SeqNumToAck
        LFR = SeqNumToAck
        LAF = LFR + RWS
    ```

    For example, suppose `LFR = 5` (i.e., the last ACK the receiver sent was for sequence number 5), and `RWS = 4`. This implies that `LAF = 9`. Suppose `SeqNumToAck = 5`. Should frames 7 and 8 arrive, they will be buffered because they are within the receiver’s window. However, the receiver still sends ACK on **5**, since Frame 6 hasn't arrived. Frames 7 and 8 are said to have arrived out of order. Should frame 6 then arrives, `SeqNumToAck` is now `8`. the receiver acknowledges frame 8, bumps `LFR` to 8, and sets `LAF` to 12.




### TCP 

Transmission Control Protocol (TCP), most widely used protocol in the Internet. Features: 

1. Connection-oriented
2. Reliable transmission
3. Full duplex, i.e., bi-directional conversation
4. Flow control
5. Congestion control
6. Byte-oriented

Flow control v.s. congestion control: flow control is to avoid sender from sending too many packets to overload the **receiver**, while congestion control is to avoid sender from sending too many packets to overload the **network**.

- TCP header

    [<img src="https://book.systemsapproach.org/_images/f05-04-9780123850591.png" alt="../_images/f05-04-9780123850591.png" style="zoom:50%;" />](https://book.systemsapproach.org/_images/f05-04-9780123850591.png)

    `SequenceNum`: sequence number for the 1st byte of data carried in the segment. It's used to provide ordered packet deliver.

    `Acknowledgment`: sequence number of the next byte of data expected. It's used to provide reliable delivery.

    `AdvertisedWindow`: sent in every `ACK` packet from the receiver to the sender. The sender can have no more than `AdvertisedWindow` bytes of unacknowledged data at any time.

    Flags indicate TCP packet types, e.g. SYN, FIN, ACK. A packet can be of multiple types.

    

- Sliding window

    Very similar to what's described earlier. Except for flow control:

    - TCP flow control

        Whenever the receiver receives a packet, it sends an `ACK` to the sender, along with the current remaining space in its buffer. The sender can have no more than `AdvertisedWindow` bytes of unacknowledged data at any time. When the sender sees an `AdvertisedWindow` of 0, TCP would block the sending process. 

        Some minor detail of *Zero Window Probes* and `AdvetisedWindow` [here](https://book.systemsapproach.org/e2e/tcp.html#flow-control).

- Byte-oriented

    TCP consideres data as an "ordered byte stream", so `SequenceNum` and `Acknowledgment` reflect the bytes that have been sent/received.

    `SequenceNum` indicates the index of the first byte in a segment/packet. 

    `Acknowledgment` indicates the index of the **next** byte expected in sequence. 

    Consider the situtaion: the sender sends 3 packets with bytes 0-535, 536-800, 801-999, and 536-800 gets lost. The 1st ACK is 536, and the 2nd ACK is also 536! Suppose after RTO the sender resends the packet 536-800 and is received, the 3rd ACK would be 1000. This is called an ***accumulative ACK***. 

    Sequence numbers start at random, for security.

- Connection management

    TCP is a full duplex, process-to-process service. The service is initiated and terminated by following a series of packet determined by a state-transition diagram. 

    Connection establishment is asymmetric (client does an active open and the server does a passive open), connection teardown is symmetric (each side has to close the connection independently). When one side closes the connection, it means that it wouldn't send data to the other side, but it can still receive data sent from the other side.

    - Connection set-up

        The idea is for two parties to agree on the sequence numberes.

        [<img src="https://book.systemsapproach.org/_images/f05-06-9780123850591.png" alt="../_images/f05-06-9780123850591.png" style="zoom:50%;" />](https://book.systemsapproach.org/_images/f05-06-9780123850591.png)

        x is the starting SequenceNum that client would send to server.

        y is the starting SequenceNum that server would send to client.

        The reason why each side acknowledges a sequence number one larger than the one sent is that the `Acknowledgement` field identifies the “next sequence number expected,” thereby implicitly acknowledging all earlier sequence numbers. Although not shown in this timeline, a timer is scheduled for each of the first two segments, and if the expected response is not received the segment is retransmitted.

        The client could send data on the ACK it sends back to the server (called *piggyback*), because its connection to the server is open when SYN+ACK is received. But only when the server receives ACK, the server side connection is open.

    - Connection teardown

        ![11](https://media.geeksforgeeks.org/wp-content/uploads/CN.png)

        Connection is closed independently on each side. Closing one side doesn't require the other side to be closed. Thus, on any one side there're 3 possiblities:

        - This side closes first: ESTABLISHED → FIN_WAIT_1 → FIN_WAIT_2 → TIME_WAIT → CLOSED.

        - The other side closes first: ESTABLISHED → CLOSE_WAIT → LAST_ACK → CLOSED.

        - Both sides close at the same time: ESTABLISHED →  FIN_WAIT_1 → CLOSING → TIME_WAIT → CLOSED.

        A slightly different situation is possible. When the server receives the FIN from client, and knows that the client wants to close the client-side connection, it also chooses to close the server-side connection. So it can send back a FIN+ACK packet, and in this way only 3 packets are exchanged.

        The problem is if the ACK from client to server, to acknowledge the close of server-side connection, is not received, the server would keeps resending FIN. So the solution is for client to wait for another 2 minutes to be able to resend ACK to server. This is called the TIME_WAIT state.

        > The main thing to recognize about connection teardown is that a connection in the TIME_WAIT state cannot move to the CLOSED state until it has waited for two times the maximum amount of time an IP datagram might live in the Internet (i.e., 120 seconds). The reason for this is that, while the local side of the connection has sent an ACK in response to the other side’s FIN segment, it does not know that the ACK was successfully delivered. As a consequence, the other side might retransmit its FIN segment, and this second FIN segment might be delayed in the network. If the connection were allowed to move directly to the CLOSED state, then another pair of application processes might come along and open the same connection (i.e., use the same pair of port numbers), and the delayed FIN segment from the earlier incarnation of the connection would immediately initiate the termination of the later incarnation of that connection.

        To repeat, a connection in TIME_WAIT cannot move to CLOSED until it has waited for two minutes (two times the maximum amount of time an IP packet can live in the Internet). The reason is that the ACK sent from this side might not be successfully delivered. If it's lost, the other side might retransmit the FIN and the FIN can get delayed. If the connection directly moves to CLOSED, then another `<application on this side, application on the other side>` pair might use the same connection (`<srcIP, srcPort, dstIP, dstPort>`) and the delayed FIN from earlier invocation of the connection might cause the unintended termination of the current connection.

    - TCP state visited on client side

        ![img](https://media.geeksforgeeks.org/wp-content/uploads/CN-1.png)

    - TCP state visited on server side

        ![img](https://media.geeksforgeeks.org/wp-content/uploads/CN-2.png)

    - State-transition diagram

        [<img src="https://book.systemsapproach.org/_images/f05-07-9780123850591.png" alt="../_images/f05-07-9780123850591.png" style="zoom: 50%;" />](https://book.systemsapproach.org/_images/f05-07-9780123850591.png)

        Three things to notice:

        - During three-way handshake to establish the connection, if the client's ACK to the server is lost, the connection still functions correctly. The reason is that once receiving ACK from server, the client is in ESTABLISHED state and the local application can send data to the server. Each of the **data segments** would have `ACK` flag set and the correct value in `Acknowledgement` field, indicating the next sequence number it's expecting. Thus, when the server receives the first data segment, it also moves to ESTABLISHED state. (RFC specifies that once a connection is established, the `ACK` flag is alwasy set. [Reference](https://networkengineering.stackexchange.com/questions/29823/why-is-tcp-acknowledging-all-the-time?newreg=94bd067900dc46808b5e906f9aa225bd).)
        - There's a transition from LISTEN to SYN_SENT with a *send* action. This's to allow passively waiting node to change to an active one.
        - Timeout and retransmissions are not shown in the diagram.

- Making TCP efficient

    - Delayed ACKs. E.g., every 200ms possibly send ACKs. 

        Instead of sening an ACK for every data packet received, send one ACK for all packets received within the time period.

    - Timeout calculation

        The timeout should adapt to network conditions, like RTTs.

        - Exponentially Weighted Moving Average (EWMA)

            Simple means for estimating network/RTT conditions. Assumes that we measure RTT for every data/ack pair (sample RTT). Whenever a data/ack pair transmission completes without exceeding RTO, we get a sample. The formula is: 
            $$
            EstimatedRTT = \alpha EstimatedRTT + (1 - \alpha) SampleRTT
            $$
            a typical value for α is 0.9. And
            $$
            RTO = 2 \times EstimatedRTT
            $$

        - Karn-Patridge Algorithm
          
            The problem is that ACK really acknowledges *receipt*, instead of *transmission*. Whenver a packet is retransmitted and an ACK arrives at the sender, we can't tell whether it should be associated with the 1st or 2nd transmission. However, we need to know this to calculate `SampleRTT`. 
            
            The solution is to meansure `SampleRTT` only for segments that're sent exactly once. Also, if time TCP retransmits, set `RTO` to be twice as original `RTO`, instead of twice of `EstRTT`, **for that packet**. In short:
            
            - Do not sample RTT for data packets that have been resent due to time out.
            - Exponentially increase RTO value for **resent** packet.
