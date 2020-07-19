![constraints](constraint.jpg)

Goal: maximuze **throughput** and **peak memory utilization**

Internal fragmentation and external fragmentation.

![design choices](design_choices.jpg)



#### Implicit list

![implicit list](implicit.jpg)

Note that the **payload** is aligned, instead of the header.

Fit policy: first fit, next fit, best fit, worst fit.

Boundary tag introduced to help coalescing.

![allocator policies](allocator_policies.jpg)

 ![implicit list summary](implicit_summary.jpg)



#### Explicit list

![explicit list](explicit.jpg)

![explicit free](explicit_free.jpg)

![explicit list summary](explicit_summary.jpg)



#### Segregated list

![segregate](segregate.jpg)

![](seglist_allocate.jpg)

![](seglist_pro.jpg)