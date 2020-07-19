## Network Security

- Issues
    - Network security - address threads via counter-meansures deployed in the network
    - Privary - preventing unauthorized release of info/data
    - Authentication - verifying he identity of a remote party
    - Integrity - ensuring that messages are not altered

- Threat

    The first step in developing effective counter-measures is to understand the adversaries' objective:

     - Making money
     - Vandalism
     - Gathering intelligence/counter intelligence
     - Marketing/influence

    The adversaries can take advantage of the following issues to achieve their objectives:

    - open internet - anyone can contact anyone else
    - anonymity
    - complexity/dymanically evolving
    - humans in the loop

    - inversion of work - something easy for one party is difficult for another party

- Examples of evolution of threats:

    - 1988, Morris Worm - software that exploited vulnerabilities in some popular Unix apps.
    - 1990s, computer virus - Progagated via email. Outcome was local to the host.
    - 2001, Internet worms - Code Red I, II.
    - 2003-2004, botnets. More sophisticated software that could be controlled by a remote entity after install.
    - Today, diverse service offering. Exploits, command + control, DoS, data gathering.

- Defending against threats

    Assumes there's a network infrastructure perimeter. Keep the bad stuff out. Assumes knowledge of ingress/egress points, like border routers. We want to block unwanted traffic.

    Typical mechanisms:

    1. Firewalls

        Blocking traffic using IP layer information. Define rules that admit/deny traffic. Coarse-grained blocking.

    2. Intrusion detection/prevention systems (IDS/IPS)

        Inspect details of data payloads to identify exploits. 

        IDS reports exploits, while IPS blocks exploits.

