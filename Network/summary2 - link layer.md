## Link Layer

- Concerned with data transfer in the same Local Area Network (LAN). 

- Challenge: Multiple hosts communicate simultaneously using the same channel, i.e., Multiple-access network.

- Solution: **Media Access Control** (MAC). Ethernet, WiFi, etc.

  

### Ethernet

- Physical properties

  - Ethernet segment: a cable with length up to 500m

  - Transceiver: each host has a transceiver, which can transmit and receive bits

  - Repeater: blindly forwards bit between Ethernet segments. Operates at the physical layer

  - Hub: a multi-way repeater. Repeats what it hears on one port to all other ports.

    In a Ethernet, regardless of the number of segments and network topology, data transmitted by any one host reaches all the other hosts, so all these hosts are competing for access to the same link. This is way they are said to be in the same *collision domain*.

    Ethernet segments connected using repeaters/hubs remain in the same collision domain.

  - Switch: connecting multiple collision domains, operating at Link Layer.  When a switch receives a packet, it examines its destination MAC address: if the packet is for another LAN, it forwards it on the corresponding port; otherwise, the packet is dropped. A switch fully implements the Ethernet's collision detection and media access control protocol.

    Ethernet segments connected using switches remain separate collision domains. More on this later.

    A learning switch: maintains a forwarding table between LANs. The forwarding table begins emtpy. When receiving a packet, the switch examines the source MAC address and make entries in the forwarding table. If no entry exists for destination, boardcast on all other ports. 

    The learning switches work unless there's a loop in the network. Spanning Tree Protocol (STP) solves the problem, by finding a spanning tree in the network and remove unused edges. Each switch decides the ports that are used/unused. STP is dynamic, ready to readapt to changed network.

    Historically, nodes form Ethernet segments, and switches connect Ethernet segments. Nowadays, nodes are directly connected to switches and switches are inter-connected with each other: switched Ethernet.

- Frame format

  ![Ethernet frame format](ethernet-frame-format.jpg)
  
  MAC address is assigned by hardware manifacture of the network chips.
  
- MAC for Ethernet

  Carrier-sense, multiple-access, with collision detection (CSMA/CD).

  Transmitter algorithm  

  1. When the adaptor has a frame to send and the line is idle, it transmits the frame immediately. If the sender has multiple packets to send, wait for an "interframe gap" (96 bit-time, 9.6 microseconds) in before packets.

  2. When an adaptor has a frame to send and the line is busy, it waits for the line to go idle. When the line gets idle, all waiting adaptors wait for an interframe gap and then transmits. Collision can occur when more than one adaptors thinks the line is idle and transmits at the same time.

  3. At the moment a sender detects a collision (receives packet during transmitting), it transmits a 32-bit jamming sequence and stops transmission.  

     The collision detection mechanism requires each frame to be >= 64 bytes on a 10-Mbps Ethernet (14-byte headers + 46-byte payload + 4-byte CRC). Why 64 bytes? Consider the worst case when hosts A and B located at opposite ends of the network. Suppose host A begins transmitting a frame at time t. It takes one link latency (let's denote the latency as d) for the frame to reach host B. Thus, the first bit of A's frame arrives at B at time t+d. Suppose an instant before host A's frame arrives (i.e., B still sees an idle line), host B begins to transmit its own frame. B's frame will immediately collide with A's frame, and this collision will be detected by host B. Host B will send the 32-bit jamming sequence. Unfortunately, host A will not know that the collision occurred until B's frame reaches it, which will happen one link latency later, at time t+2×d. In other words, host A must transmit for 2×d to be sure that it detects all possible collisions. Considering that a maximally configured Ethernet is 2500 m long, and that there may be up to four repeaters between any two hosts, the round-trip delay has been determined to be 51.2 μs, which on a 10-Mbps Ethernet corresponds to 512 bits = 64 bytes.  

  4. Once an adaptor detects a collision and stops its transmission, it waits a certain amount of time and tries again. Each time it tries transmitting and fails, the adaptor doubles the amount of time it waits before next try. This strategy is called *exponential backoff*.




### Wireless LAN (WLAN) and WiFi

Skipped for now. Doesn't seem like a popular topic for interview.
