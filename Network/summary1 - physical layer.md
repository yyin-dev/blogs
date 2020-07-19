## Physical Layer

- Performance
  - Bandwidth: amount of bits **transmitted** (by the transmitter) per unit time. The rate at which bit encoding is generated.
  - Latency: time needed to transfer one bit between two nodes. Three parts: propagation delay, queueing delay, transmit delay.
- Error detection & handling

  - Strategy: redundancy
  - Goal: all packets with errors are detected and dropped (recovering is too expensive). The procedure should detect common case errors, while being computationally efficient and minimizing redundant bits needed.
- Different physical media with different signal-carrying properties. 
- Bit encodings for copper wire. Non-return to Zero (NRZ), Non-return to Zero Inverted (NRZI), Manchester. 
- Framing: detecting the start/end of packet. Sentinel method. Byte-couting. Clock-based.