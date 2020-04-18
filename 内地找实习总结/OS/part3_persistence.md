## Part III - Consistency

### Chapter 36 - IO device overview

- System architecture

    CPU is connectd with other system components with buses:

    - Main memory: memory bus
    - IO devices: IO bus
    - slowest IO devices: peripheral bus

    Faster devices are placed closer to CPU while slower devices are further. 

- Device = Interface to OS + Internal structure

- I/O Mode
    - Programmed IO(PIO)

        CPU waits for I/O device. Inefficient.

    - Interrupt-driven IO

    - Direct Memory Access(DMA).   

        Say a process wants to write some data to the disk. Even in interrupt-driven IO, the process must explicitly moves these data from memory to the device through the interface exposed by the disk. Only after this, the CPU can be scheduled for other threads/processes. 

        DMA allows data transfer between devices and memory without the intervention of CPU.

    Interrupt-driven IO and DMA are techniques to reduce overhead in PIO. Note that interrupt-driven IO is not always the best option. It depends on how fast the I/O device is. A hybrid can be an option. 

- Device driver

    We want a device-neural OS. For example, we want the file system to work on HDD, SSD, RAID, etc. 

    Solution: abstraction via device driver. 

### Chapter 37 - HDD (Hard Disk Drive) 

#### Basic geomotry

Platter, surface, spindler, RPM, track, disk head, disk arm.

#### Latencies

Rotational latency, seek time, transfer time.  
IO time = seek time + rotation time + transfer time.  
Random workload v.s. sequential workload.  

#### Other details

Track skew, track buffer, write back/through

#### Disk scheduling

Job length can be estimated well, so *disk scheduler* uses SJF.  
Policies: 

- SSTF(Shortest-Seek-Time-First)
- Elevator (a.k.a SCAN)
- SPTF(Shortest-Positioning-Time-First)  

Rotational delay v.s. seek time.

#### Other scheduling issues

OS scheduling v.s. disk scheduling.  
I/O Merging.  

### Chapter 38 - RAID (Redundant Arrays of Inexpensive Disks)

RAID **transparently** transforms a number of indepent disks into a *large*, *fast*, *reliable* disk system.  
Metrics for evaluating RAID: capacity, reliability, performance.  

- RAID Level 0 (striping)
- RAID Level 1 (mirrowing)
- RAID Level 4 (parity disk)
- RAID Level 5 (rotating parity)  

Understand how Figure 38.8 is derived.  

### Chapter 39 - Files and Directories

**File system is another abstraction provided by the OS** for managing external storage. This is not provided by the external storage, but by the OS!

- A file is a linear array of bytes, with a lowe-level name given by the OS's file system called **inode number**. The OS doesn't know about the structure of the file. A directory, like a file, has a inode number but contains specific content: mappings between human-readable name to inode number. 

- Create a file

    `int fd = open("foo", O_CREAT | O_WRONLY  | OTRUNC);`

    The return value is a **file descriptor**, a private, per-process integer referring to a file.

    Three file descriptors are initialized for each process at creation: stdin, stdout, stderr. 

- Read and write files

    ```
    prompt> echo hello > foo
    prompt> cat foo
    hello
    prompt> strace cat foo
    ...
    open("foo", O_RDONLY | O_LARGEFILE) = 3
    read(3, "hello\n", 4096) = 6
    write(1, "hello\n", 6) = 6
    hello
    read(3, "", 4096) = 0
    close(3) = 0
    ...
    prompt>
    ```

- Read and write file at an offset

    `off_t lseek(int fd, off_t offset, int whence)`

    `whence` determines how the seek is performed. 

    Each open file has a current offset tracked by the OS, which can be changed only by 2 operations: 1) read or write; 2) explicityly calling `lseek()`. 
    Note: calling `lseek()` does not perform a disk seek! It just changes a variable in the data structure used by the OS.  

- File system, for performance reason, would buffer writes to disk. To force immediate writing, use `fsync()`. Note that to force writing to a file, you need to call `fsync()` on both the file itself and the directory containing it.  

- Renaming a file uses system call `rename()` and is usually implemented as an atomic operation.  

- File system keeps metadata about files in a structure called **inode**.  

- Removing a file uses system call `unlink()`.  But what's the meaning of `unlink`? Explained below. 

- You cannot write to a directory directly, since the format of the directory is considered as file system metadata. You can update a directory only be creating/deleting files/directories. 

- Read directory

    `opendir()`, `readdir()`, `closedir()`. 

- Delete directory

    `rmdir()`. Only allowed if the directory is empty.

- Hard link

    `int link(const char *path1, const char *path2)` create another name to `path1` as `path2`. `link` simply creates another name in the directory with `path2`, but refers it to the *same* inode number of `path1`. The flie is not copied in any way. 

    This also answers why `unlink` system call is used to remove a file. When creating a file, you're doing two things: 1. making an inode; 2. linking a human-readable name to that inode number. `unlink` just removes the human-readable name, but the underlying inode is not touched. 

    The reason this works is that whenever an unlink is done, the file system checks a reference count of the inode number (called *link count*). When the reference count reaches 0, the inode is freed. 

    Hard link cannot be created on directory, for fear of cycle in the directory tree. 

- Symbol/Soft link

    A symbol link is a file itself. In other words, the file system knows 3 types: file, directory, symbolick link. 

    ```
    prompt> echo hello > file
    prompt> ln -s file file2
    prompt> cat file2
    hello
    prompt> ls -al
    drwxr-x--- 2 remzi remzi    29 May 3 19:10 ./
    drwxr-x--- 27 remzi remzi 4096 May 3 15:14 ../
    -rw-r----- 1 remzi remzi     6 May 3 19:10 file
    lrwxrwxrwx 1 remzi remzi     4 May 3 19:10 file2 -> file
    ```

    The reason that `file2` is 4 byte is that a symbolic link holds **the pathname of the linked-to file** as data. As `file` has 4 characters, the size of `file2` is 4 bytes.

    Possibility of dangling reference:

    ```
    prompt> rm file
    prompt> cat file2
    cat: file2: No such file or directory
    ```



### Chapter 40 - File System Implementation  

**The file system is pure software** by the OS.

#### Overall Organization

Check 40.2 figures.

- Superblock
- Inode bitmap, data bitmap
- Inode table, data region

#### Inode

- Inode address computation
- Referring data blocks
    - direct pointers (cannot handle large files)
    - indirect pointers
    - double/triple indirect pointers

#### Access path

- Read a file `/foo/bar`  
- Write to a file
- Create a new file

#### Caching and Buffering

### Chapter 41 - Locality and The Fast File System

Poor performace. FFS Solution: disk awareness.  

#### Cylinder group/Block group

- Motivation: accessing files within the same group would have short seek time. Accessing inode and then the data blocks is a common operation.  
- Include superblock, inode bitmap, data bitmap, inode table, data region within each group.  

#### Policies: allocating files and directories  

Locality.  

- Directory  
    Find a group with low number of allocated directories and high number of free inodes.  
- File  
    1. Try to allocate the inode and data blocks of a file within the same group. 
    2. Place all files in the same directory within the same group.  

#### The large file exception in FFS  

A large file could fill the current group and prevents other files from enjoying the locality.  
Solution: store chunks of the large file in different groups. Make chunk size larger to amortize the seek time.

### Chapter 42 - Crash Consistency: FSCK and Journaling  

#### FSCK: File system checker  

Problem: too slow for large disk.  

#### Data Journaling

- Journal write: write TxB and data blocks to log
- Journal commit: write TxE
- Checkpoint: write to final location in disk
- Free: update journal superblock

#### Meta journaling (Ordered journaling)  

Only metadata is written to the journal, while user data is directly written to the final location, to reduce overhead.  

- Data write
- Journal metadata write
- Journal commit (after previous 2 steps are both finished)
- Checkpoint metadata
- Free

### Chapter 43 - Log-structured File System (LFS)

#### Motivations

- Memory grows. More disk data is cached in main memory and disk traffic mainly consists of writes.  
- Larger performance gap between sequential IO and random IO
- Existing file systems perform poorly on common workload

#### Mechanism

When writing to disk, LFS buffers all updates (including metadata!) in a *segment* and write to disk when the segment is full. LFS never overwrites existing data, but always writes to free location.  

#### Segment size: how much to buffer?

#### Finding inodes: inode map

#### Checkpoint Region

#### Garbage Collection





















