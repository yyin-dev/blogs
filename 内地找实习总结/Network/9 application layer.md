## Application Layer (layer 5)

- Application types: (1) client-server; (2) peer-to-peer.

    Client-server is the most popular type. Servers open passively and service incoming client requests. Examples: the web, email, DNS.

    Peer-to-peer applications are those where a host performs both client and server functions. No hosts may always be on, which means that finding hosts to communicate with can be a challenge. Examples: BitTorrent, BitCoin.

    Applications define message types, formats and exchange protocols. Considerations include scale, reliability, performance, timing, ...

- World Wide Web

    Invented to serve "rich media" (text, images, sound, videos, etc). 

    Structural components: browsers, servers, caching infrastructures, data centers.

    Semantic oomponents: HTTP, HTML/XML, URI/URL.

- Hyper Text Transfer Protocol, HTTP

    A simple, stateless, request-reponse protocol.

    Eight commands. There're a series of response codes from servers.

    - HTTP 1.0

        The same as HTTP 0.9, except for the version number.

        The packet exchange protocol in HTTP 1.0 uses "stop-and-wait". Namely, for each TCP connection from client to server, a file would be requested, served and then the next TCP connection would be generated. The browser requests a base page (like `index.html`), read and render that page by requesting all embedded contents. Key aspect of HTTP 1.0 is that one TCP connection is created for each file.

    - HTTP 1.1

        HTTP 1.1 made the observation that there were performance inefficiencies in 1.0. The main improvement include:

        - Persistent TCP connections, i.e. keep TCP connections open. So one TCP connection is used for multiple files/objects. The question is when should the server close the connection.
        - Pipelining. Pack as many data objects into packets as possible (since many objects are small in size). This requires a way to identify and separate objects in the packet.

- Domain Name System (DNS)

    Terminologies: First, a *name space* defines the set of possible names. A name space can be either *flat* (names are not divisible into components) or *hierarchical* (Unix file names are an obvious example). Second, the naming system maintains a collection of *bindings* of names to values. The value can be anything we want the naming system to return when presented with a name; in many cases, it is an address. Finally, a *resolution mechanism* is a procedure that, when invoked with a name, returns the corresponding value. A *name server* is a specific implementation of a resolution mechanism that can be queried by sending it a message.

    - Domain Hierarchy

        A translation service between alpha-numeric strings and IP addresses. The name space is reflected in a global server infrastructure that provides the translation service.

        DNS names are processed from right to left and use periods as the separator. DNS hierarchy can be visualized as a tree, where each node in the tree corresponds to a domain, and the leaves in the tree correspond to the hosts being named.

        ![../_images/f09-15-9780123850591.png](https://book.systemsapproach.org/_images/f09-15-9780123850591.png)

    - Name servers

        How the hierarchy is implemented?

        The first step is to partition the hierarchy into subtrees called *zones*. Each zone can be thought of as corresponding to some administrative authority that is responsible for that portion of the hierarchy. For example, the top level of the hierarchy forms a zone that is managed by the Internet Corporation for Assigned Names and Numbers (ICANN). Below this is a zone that corresponds to Princeton University. Within this zone, some departments do not want the responsibility of managing the hierarchy (and so they remain in the university-level zone), while others, like the Department of Computer Science, manage their own department-level zone.

        ![../_images/f09-16-9780123850591.png](https://book.systemsapproach.org/_images/f09-16-9780123850591.png)

        A zone corresponds to the fundamental unit of implementation in DNS—the name server. Clients send queries to name servers, and name servers respond with the requested information. Sometimes the response contains the final answer that the client wants, and sometimes the response contains a pointer to another server that the client should query next. Thus, from an implementation perspective, it is more accurate to think of DNS as being represented by a hierarchy of name servers rather than by a hierarchy of domains.

        <img src="https://book.systemsapproach.org/_images/f09-17-9780123850591.png" alt="../_images/f09-17-9780123850591.png" style="zoom: 33%;" />

        Each zone is implemented in multiple name servers for redundancy. Each name server implements the zone information as a collection of *resource records*, 5-tuples:

        ```
        (Name, Value, Type, Class, TTL)
        ```

        The `Name` and `Value` fields are exactly what you would expect, while the `Type` field specifies how the `Value` should be interpreted. For example, `Value` of `A` indicates that the `Value` is an IPv4 address. Other record types include:

        - `NS`—The `Value` field is the domain name for a name server that knows how to resolve names within the specified domain.
        - `AAAA` - the `Value` is an IPv6 address.

    - Assume that we want to resolve `cs.wisc.edu`. Ignore `Class` and `TTL`.

        First, a root name server contains an `NS` record for each top-level domain (**TLD**) name server. This identifies a server that can resolve queries for this part of the DNS hierarchy (`.edu` and `.com`in this example). It also has `A` records that translates these names into the corresponding IP addresses. Taken together, these two records effectively implement a pointer from the root name server to one of the TLD servers.

        ```
        (edu, a3.nstld.com, NS)
        (a3.nstld.com, 192.5.6.32, A)
        (com, a.gtld-servers.net, NS)
        (a.gtld-servers.net, 192.5.6.30, A)
        ...
        ```

        The TLD servers have:

        ```
        (princeton.edu, dns.princeton.edu, NS, IN)
        (dns.princeton.edu, 128.112.129.15, A, IN)
        ...
        ```

        Down one level:

        ```
        (email.princeton.edu, 128.112.198.35, A, IN)
        (penguins.cs.princeton.edu, dns1.cs.princeton.edu, NS, IN)
        (dns1.cs.princeton.edu, 128.112.136.10, A, IN)
        ...
        ```

        Finally, a third-level name server, such as the one managed by domain `cs.princeton.edu`, contains `A` records for all of its hosts.

        ```
        (penguins.cs.princeton.edu, 128.112.155.166, A, IN)
        (cs.princeton.edu, mail.cs.princeton.edu, MX, IN)
        (mail.cs.princeton.edu, 128.112.136.72, A, IN)
        ...
        ```

        

        Suppose the client wants to resolve the name `penguins.cs.princeton.edu`. The client could first send a query containing this name to one of the root servers. The root server, unable to match the entire name, returns the best match it has—the `NS` record for `edu` which points to the TLD server `a3.nstld.com`. The server also returns all records that are related to this record, in this case, the `A` record for `a3.nstld.com`. The client, having not received the answer it was after, next sends the same query to the name server at IP host `192.5.6.32`. This server also cannot match the whole name and so returns the `NS` and corresponding `A` records for the `princeton.edu` domain. Once again, the client sends the same query as before to the server at IP host `128.112.129.15`, and this time gets back the `NS` record and corresponding `A` record for the `cs.princeton.edu` domain. This time, the server that can fully resolve the query has been reached. A final query to the server at `128.112.136.10` yields the `A` record for `penguins.cs.princeton.edu`, and the client learns that the corresponding IP address is `128.112.155.166`.

        For `cs.wisc.edu`. Client sends a request via UDP to local name server. The local name server would send the domain name in a request to root DNS server. Root's response would an IP address of a **TLD server** that can resolve `edu`. The local name server send another request of `cs.wisc.edu` to the TLD `edu` server, which would respond an IP address of an **authoritative server** that can resolve `wisc.edu`. The local name server send another request to the authoritative `wisc.edu` server, which would respond the IP address of `cs.wisc.edu`.

- Internet caching

    Main idea: move content closer to clients to reduce latency. Caching also improves robustness. Content Delivery Network (CDN).

    The problem is that DNS would resolve the domain name to the IP address of the data center, instead of the local cache infrastructure. 

    Two ways to get data from an Internet cache:

    - Transparant, inline cache

        Requires inspection of application layer data of all packets. Directly serves those that it recognizes. However, this is very costly and not widely used.

    - DNS-based CDN

        The CDN takes over operation of the authorative server of a domain.





