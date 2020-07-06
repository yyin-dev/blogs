#### TCP Congestion Control (textbook)

- Jacobson's algorithm

    Timeout is closely related to congestion. If timeout is too short, you're adding unnecessary load to the network. Jacobson's algorithm provides better RTO estimation.

    The main problem with the original computation is that it does not take the **variance** of the sample RTTs into account. Intuitively, if the variation among samples is small, then the `EstimatedRTT` can be better trusted and there is no reason for `RTO = 2 * EstimatedRTT`. On the other hand, a large variance in the samples suggests that the timeout value should not be too tightly coupled to the `EstimatedRTT`.

    In the new approach, the sender measures a new `SampleRTT` as before, but folds this new sample into `RTO` differently:

    ```
    Difference = SampleRTT - EstimatedRTT
    EstimatedRTT = EstimatedRTT + (delta x Difference), where 0 < delta < 1
    Deviation = Deviation + delta (|Difference| - Deviation)
    Timeout = mu * EstRTT + phi * Deviation, where mu = 1 and phi = 4
    ```

    With this, when variance is small, `Timeout` is close to `EstimiatedRTT`; when variance is large, `Deviation` dominates the computation of `Timeout`.

- TCP Congestion Control

    - Additive Increase/Multiplicative Decrease (AIMD)

        TCP maintains a new state variable for each connection, `CongestionWindow`, limiting how much data is allowed to be in transit at a given time. It's the congestion control's couterpart of flow controls' `AdvertisedWindow`. Now, TCP's max unacknowledged data = `min(CongestionWindow, AdvertisedWindow)`. In other words, TCP is allowed to send no faster than the slowest component - the network or the destination host.

        Unlike `AdvertisedWindow` sent from the receiver, the sender needs to learn and adjust `CongestionWindow` by itself. 

        - How does the sender determine if the network is congested and it should decrease `CongestionWindow`? The observation is that the main reason packets aren't acknowledged and the timeout fires is that a packet is dropped due to congestion, since it's rare to have transmission error. Thus, TCP interprets timeouts as a sign of congestion and halves `CongestionWindow` (Multiplicative Decrease) for each timeout.

            Though `CongestionWindow` is defined in bytes, we can think about it as in terms of whole packets. `CongestionWindow` wouldn't fall below the size of a single packet, called the *maximum segment size* (MSS).

        - How does the sender determine if the network is less congested and it should increase `CongestionWindow`? Every time the sender sends a `CongestionWindow`'s  worth of packets (each packet sent during the latest capacity of `CongestionWindow`, or during the last RTT as we know sliding window would fill the pipe, is ACKed), it adds the equivalent of 1 packet to `CongestionWindow`. In practice, TCP doesn't wait for an entire window's worth of ACKs to add one MSS to `CongestionWindow`, but increments `CongestionWindow` for each ACK received, like:

            ```
            Increment = MSS x (MSS/CongestionWindow)
            CongestionWindow += Increment
            ```

            The phase of using AIMD is also called ***congestion avoidance*** mode.

            So the `CongestionWindow`'s value changes like in the figure:

            ![../_images/f06-09-9780123850591.png](https://book.systemsapproach.org/_images/f06-09-9780123850591.png)

            In AIMD, the sender reduce `CongestionWindow` much faster than increasing it. This reason for the aggressive decrease is that having too large a window is too expensive.

            Note that since timeout is a curcial indication of congestion, we need accurate timeout mechanism. Jacobson's algorithm is used.

    - Slow Start

        The problem with AIMD is its slowness to increase `CongestionWindow`. Slow start, an ironic name, is a mechanism to increases `CongestionWindow` exponentially, rather than linearly. 

        <img src="https://book.systemsapproach.org/_images/f06-10-9780123850591.png" alt="../_images/f06-10-9780123850591.png" style="zoom:50%;" />

        The reason why this's called *slow* start is that it's slow compared to original TCP, where the sender try to fill up the `AdvertisedWindow` at the start.

        When the `CongestionWindow` is relatively large, slow start is too aggressive. So we have another variable `SlowStartThreshold`, SST. When `CongestionWindow` exceeds SST, we use AI; when `CongestionWindow` is below SST, we use slow start.

        The sender starts out with `CongestionWindow` = 1, SST = infinity, and does slow start. When `CongestionWindow` exceeds SST, converts from slow start to Additive Increase. Whenever a timeout fires, set SST = `0.5 * CongestionWindowSize`, set `CongestionWindow` = 1, and restart slow start.

        ![img](https://i.stack.imgur.com/S9uiN.png)

        In summary, three cases slow start is terminated:

        - Timeout
        - Exceed SST (enters congestion avoidance mode)
        - Receives 3 duplicate ACKs (enters fast recovery mode)

    - Fast Retransmit and Fast Recovery

        In practice, it's hard to keep a timer on each packet being sent. So a timer is used for a group of packets. However, waiting for timeout to fire before retransmitting can be slow, fast retransmit triggers the retransmission of a lost packet sonner than regular timeout. 

        Timeout can be considered as a coarse-grained signal of congestion (as we do timing in a coarse-grained way), while duplicate ACK is a fine-grained signal of congestion.

        Every time a data packet arrives at the receiving side, the receiver responds with an ACK, even if this sequence number has already been ACKed. Thus, when a packet arrives out of order, the receiver sends back an ACK for a packet that has been ACKed before, called *duplicate ACK*. When the sender sees a duplicate ACK, it knows that the other side must have received a packet out of order. This can be because an earlier packet was lost, **or** the earlier packet has been delayed. As we have two possibilities, sender waits until it sees some number of duplicate ACKs and then retransmits the missing packet. In practice, TCP waits for 3 duplicate ACKs before retransmitting.

        <img src="https://book.systemsapproach.org/_images/f06-12-9780123850591.png" alt="../_images/f06-12-9780123850591.png" style="zoom:50%;" />

        In this example, the destination receives packets 1 and 2, but packet 3 is lost in the network. Thus, the destination will send a duplicate ACK for packet 2 when packet 4 arrives, again when packet 5 arrives, and so on. When the sender sees the 3rd duplicate ACK for packet 2, it retransmits packet 3. Note that when the retransmitted copy of packet 3 arrives at the destination, the receiver then sends a cumulative ACK for everything up to and including packet 6 back to the sender.

        Receiving 3 duplicate ACKs is a mild sign of reaching the limit of network capacity, as compared to a timeout. So we take less dramatic action when doing slow start. Recap that when doing start, if encoutering timeout, we set SST = `0.5 * CongestionWindow` and set `CongestionWindow` = 1. When encountering 3 duplicate ACKs, we set SST = `0.5 * CongestionWindow`, but setting `CongestionWindow = SST + 3 * MSS`. This behavior is called ***Fast Recovery***.

    - In summary

        ![enter image description here](https://i.stack.imgur.com/cJDMC.png)

        ​	Points to note in the graph

         - When timeout happens, convert to slow start;
         - When receiving 3 duplicate ACKs, convert to fast recovery;
         - `CongestionWindow` increment when new ACK arrives
             - At slow start, `cwnd = cwnd + MSS`
             - At cogestion avoidance, `cwnd = cwnd + MSS * (MSS / cwnd)`
        - In fast recovery
            - When receiving new ACK (the **accumulative ACK**), convert to congestion avoidance. Reason: previously we entered fast recovery as we thought we are approaching the capacity limit. Thus, we make increments to `CongestionWindow` cautiously.
            - When receiving duplicate ACK, `cwnd = cwnd + MSS`
        - The net effect of "slow start -> fast recovery -> congestion avoidance" is that, on receiving 3 duplicate ACKs, retransmit the packet. Then, on receiving a non-duplicate ACK, i.e. the cumulative ACK, set SSH = `cwnd / 2`, set `cwnd = SSH`. 
        - With fast recovery, slow start only happens:
            1. at the start of the connection,
            2. when timeout happens.

        - TCP Tahoe doesn't have fast retransmit & fast recovery, TCP Reno, an extension to TCP Tahoe, has both.



#### TCP Congestion Control (CS640)

TCP used to be implemented to send a full, flow-controlled window of packets, to maximize the performace of the connection. This could lead to congestion.

A network will stay in "equilibrium" if it observes "conservation of packets" - don't put a packet into the network unless a packet has left the network. This only fails when: 

- A connection doesn't get the equilibrium. Solution: Slow Start.

    Objective: determine the link/path capacity on start up.

    - Set CWIND = 1
    - New variable: slow start threshold = SSThrash = undefined (>= 2)
    - Increase CWIND by 1 for **each ACK**
    - Transition from Slow Start to AIMD when CWIND > SSThrash
    - On packet loss, set SSThrash = 0.5 * Send Window Size, set CWIND = 1, redo slow start

- A new packet enters before an old packet leaves. Solution: improved RTO calculation.

    Jacobson's RTO calculation considers variance in EstRTT measurements.

    ```
    diff = sampleRTT - EstRTT
    EstRTT = EstRTT + d * diff, where  0 < d < 1
    Dev = Dev + d * (|diff| - Dev)
    RTO = µ * EstRTT + v * Dev, where µ = 1 and v = 4
    plus Karn-Patridge
    ```

- Equilibrium cannot be reached due to limited resources on a path. Solution: AIMD.

    The objective of Additive Increase Multiplicative Decrease (AIMD) is to scale the number of unacknowledged packets in the network to available resources. This is congestion control.

    So far we only have flow control through receive window. We add a *congestion window* (CWIND). Congestion window and receive window limits the number of unacknowledged packets in flight in the network.

    sendWind = min(CWIND, RWIND) <- packets

    Scale CWIND based on available resources on the path. How to get the information about the resources on the path? A timeout is a strong signal that the resources on the path is limited. The only way that a sender can know if there's additional capacity is to increase send rate. So AIMD would:

    - Increase send rate, i.e. CWIND, by 1 every RTT
    - Decrease send rate by half whenever a timeout happens.

    The congestion control scheme only has to be implemented on servers, which send the majority of data in the network.

- TCP Reno

    The observation of Tahoe is that timeouts cause significant drop in performance. Reno extends Tahoe by adding Fast Retransmit and Fast Recovery.

    - Fast Retransmit is another way of identifying packet loss. ACKs indicate the next byte expected and is sent by receiver for every packet received. When 3 duplicate ACKs are received by the sender, the sender retransmit the packet in order.
    - Fast recovery says, after receipt of a non-duplicate/new ACK, set SST = `cwnd/2` and set `cwnd = SST`. So with fast recovery, slow start only happens (1) at the start of the connection and (2) when timeout happens.

- Congestion Avoidance

    TCP Tahoe and Reno deals with congestion after it happens. Here the idea is try to sense when the network is nearing congestion and do something proactive to avoid it.

    - Host-based approach: **TCP Vegas**

        Watch for "signs" of congestion and modify send rate to avoid cogestion. The observation is that queues will grow as demand exceeds capacity of the network. The consequence is that `SampleRTT` will grow. This results in a slowing down in the growth of send rate (if RTT gets longer, the time interval between ACKs gets longer, and the sender increment `CongestionWindow` less often). Vegas tries to detect this and to set a send rate just below a level that will cause congestion/loss.

        Algorithm

        ```
        ExpectedRate = CongestionWindow / BaseRTT
        ActualRate = CongestionWindow / SampleRTT
        Diff = ExpectedRate - ActualRate
        if Diff < alpha, increase CongestionWindow by 1, where alpha often = 1
        if Diff > beta, decrease CongestionWindow by 1, where beta often = 3
        else, CongestionWindow is not changed.
        Follow TCP Reno on loss.
        ```

        where `BaseRTT` is the RTT of a packet when there's no congestion. In practice, TCP Vegas sets `BaseRTT` to the minimum of all measured round-trip times. So `ExpectedRate` is the expected send rate if there's no congestion. Note that `Diff` is positive by definition, since if `Diff < 0`, it means we should update `BaseRTT`. Note that

        ```
        Diff = CongestionWindow * (1/BaseRTT - 1/SampleRTT)
        ```

        So for the same `(BaseRTT, SampleRTT)` pair, increase `CongestionWindow` would increase `Diff`; decrease `CongestionWindow` would decrease `Diff`. In other words, we want to keep `ActuralRate` close to `ExpectedRate`.

        The problem with this approach is that in practice, `BaseRTT` and `SampleRTT` are very close and hard to measure.

    - Router-based approach: **Random Early Detection** (RED)

        Routers know the state of their queues. The basic idea of RED is to send an explicit (Explicit Congestion Notification) signal of congestion to senders when queues begin to build. Two types of signals, ECN (marking) or proactively dropping the packet to force senders to take actions (Jacobson's TCP would take this as a sign of congestion). RED makes decisions on which packets to mark/drop based on queue size:

        ```c
        AvgLen = (1 - Weight) x AvgLen + Weight x SampleLen
        if AvgLen <= MinThreshold
            queue the packet
        if MinThreshold < AvgLen < MaxThreshold
            calculate probability P
            mark/drop the arriving packet with probability P
        if MaxThreshold <= AvgLen
            drop the arriving packet
        ```

        where 0 < `Weight` < 1 and `SampleLen` is the length of the queue when a sample measurement is made. The probability `p` is proportional to queue size.

        RED is not widely used, as it makes it more likely for service provider to violate the promised drop rate.







