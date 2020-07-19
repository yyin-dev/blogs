## Part II - Concurrency

- Thread: 
    - **Separate**: registers (virtualized by context switch), stack, PC, TCB. 
    - **Shared**: address space.  

### 1. Lock

- Lock evaluation metrics: 
    (1) mutual exclusion service
    (2) fairness
    (3) performance  
- Spin lock with hardware support: `TestAndSet`, `CompareAndSwap` 
    Evaluation:
    (1) Provided.
    (2) No fairness guarantee. Starvation. 
    (3) Inefficient.  
- `LoadLinked` and `StoreConditional`
- Spinning Problem: Inefficiency  
    Solutions:  
    (1) `yield` system call to relinquish CPU. Running -> Ready.  
        Con: Still costly when number of threads are large. Starvation still exists.   
    (2) OS support: `park` & `unpark`, to replace spinning with sleeping, and an explicit queue to tackle starvation.  
- Hybrid: Two-phase lock.
- Lock-based concurrent data structures: sloppy counter, concurrent list, concurrent queue, concurrent hashtable.  

### 2. Conditional Variables

- Definition: A conditional variable is *an explicit queue* that threads can put themselves on when some condition is not satisfied; some other threads, after changing the said condition, can wake one (or more) of the waiting thread(s) on the queue.  
- Two functions  
    `pthread_cond_wait(pthread_cond_t *c, pthread_mutex_t *m);`, a.k.a `wait()`  
    `pthread_cond_signal(*pthread_cond_t *c);`, a.k.a `signal()`  
    Note:   
    1. `wait()` assumes that the lock is held when being called. It releases the lock when going to sleep and must re-acquire the lock before waking up.  
    2. A `while` loop is used for deciding whether to `wait()`, instead of a `if`. `while` always works and is the good practice.  
    3. Another good practice is to hold the lock when calling `signal`, which guarantees to work. Holding the lock when calling `wait` is a requirement by the function itself. 

- Producer/Consumer problem (Read the code)  
    Two important points:  
    (1) Use `while` instead of `if`;  
    (2) Use two conditional variables instead of one. 

### 3. Semaphores

- Definition: a semaphore is an object with an integer value which can be manipulated with 2 routines: `sem_wait()` and `sem_post()`.  
    - `sem_wait()`: decrement the semaphore by 1. If the semaphore value after decrement >= 0, then return from call. Otherwise, the thread would wait for `sem_post()` to be woken up.
    - `sem_post()`: increment the semaphore value by 1. If there are *any* waiting threads, pick up and wake it up.

- Producer/Consumer problem (read the code)
- Reader/Writer problem (read the code)
    - unfair solution
    - fair solution
- The Dining Philosopher Problem 
- It is managable to implement semaphore with lock and condition variables, but very hard the other way round. 

### 4. Common Concurrency Problems

- Non-deadlock problem
    - Atomicity-violation problem  
        Solution: impose mutual exclusion.
    - Order-violation problem  
        Solution: use conditional variables (or semaphores).

- Deadlock problem
    - Mutual Exclusion
    - Hold-and-wait
    - Non-preemption
    - Circular wait

### 5. Event-driven Concurrency: One single thread

- API: `select()` or `poll()`
- Problem 1: blocking system call.   
    Solution: Asynchronous I/O
- Problem 2: state management.  
    Solution: manual stack management.
- Other problems: multicore system, implicit blocking system call.

### 6. Advice on writing correct concurrent code

1. Keep it simple.  
    Avoid complicated interaction and use common paradigns like reader/writer, producer/consumer. 
2. Use concurrency only when absolutely needed.
3. Seek simplified form when parallism is needed.   
    Like map-reduce by Google.
