## Overview

- Circuit-switched vs Packet-switched

- Difference between switch and router
    - Switch: within a Local Area Network (LAN), only in layer 2 protocols.
    - Router: between different LANs or even larger networks, only in layer 3 protocols. 

- Forwarding and Routing

    - Packet: data chunk + header
    - Forwarding: transmit a packet towards the destination using forwarding table
    - Routing: the process of establishing forwarding table

- Multiplexing: 

    - time division multiplexing (TDM): allocating time slices
    - frequency division multiplexing (FDM): allocating frequency
    - statistical multiplexing: queueing packets. Possible problem: congestion, packet loss.

- Protocol

    - Definition: specification for interface between modules on different machines
    - Characteristics: data format, rules for information exchange, service implemented

- Internet layered architecture

    ```
    L5: Application -> define interactions with users
    L4: Transport   -> define logical channels between apps and the network
    L3: Network     -> define how packets move (routing + forwarding)
    L2: Link        -> define how hosts access physical layer
    L1: Physical    -> cabal and bit representations
    
                    Data unit    Protocols
    +------------+
    | Application|  message      HTTP, FTP, Email, ...
    +------------+
    | Transport  |  message          TCP, UDP
    +------------+      
    | Network    |  packet               IP
    +------------+
    | Link       |  frame        Ethernet, ...
    +------------+
    | Physical   |  bit          Cabel
    +------------+
    ```

    - Routers use up to network layer, while switches use only up to link layer.
    - Packet and frame are just different names for the same thing.
    - Encapsulation & Decapsulation
      - Encapsulation: Adding headers when data moving down the stack
      - Decapsulation: Removing headers when data moving up the stack

