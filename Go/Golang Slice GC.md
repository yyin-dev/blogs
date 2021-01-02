# Golang Slicing GC

I come into the problem of Golang's GC when implementing a priority queue for production. This post tries to clarify basic concepts in Golang's GC.



## No reference

There's no reference in Golang - everything is a value (for pointers, they are memory address values). Some types are implemented to *behave like reference*: maps, slices, channels, references, strings. They are descriptors of the underlying values and the internal implementation must have used pointers. For example, the strings are internally pointers to underlying bytes. Thus, when passing a map/slice to a function, you never need to pass the pointer.



## GC principle

A value can be GCed and removed from the memory if there's no pointer/variable that refer to it.



## GC in slicing

There're two ways to remove elements from a slice:

```go
// Method 1
s = append(a[:i], a[j:]...)

// Method 2
copy(a[i:], a[j:]) // copy(dst, src)
for k, n := len(a)-j+i, len(a); k < n; k++ {
    a[k] = nil
}
a = a[:len(a)-j+i]
```

If the slice contains pointers (or reference types, or structs with reference types as fields), Method 2 allows memory to be GCed earlier than Method 1. 



### Non-pointers

Suppose the slice contains `int`. 

```
{ 2, 1, 0}          backing array
  ^
  |
{        }          slice
```

Suppose you do `s = append(a[:1], [2:]...)`. Even though no one can reference 1 now, the backing array is not GCed. Reference: https://stackoverflow.com/a/57617695/9057530



### Pointers

Suppose the slice contains `*int`. Then the memory can be visualized as:

```
  +-----> 2
  |     +----> 1
  |     |    +---> 0
  |     |    |
{ ptr, ptr, ptr}          backing array
  ^
  |
{              }          slice
```

Suppose you do `s = append(a[:1], [2:]...)`. Though the slice doesn't hold any pointer to 1, the backing array does. Thus, 1 cannot be GCed.

Suppose you do as Method 2, making the pointer `nil` (note that assigning to slice is the same as assigning to the backing array). Then, there would be no pointer to the underlying value. Thus, the underlying value would be GCed. However, the memory allocated for the array itself `[]*int` is not GCed (the reason is similar as non-pointer case). Note the difference between GCing the backing array and GCing the   value pointed by the pointer.



### Struct with "reference types"

```go
type Book struct {
    title  string
    author string
}

book1 := {"foo", "bar"}
book2 := {"FOO", "BAR"}

var bkSlice = []Books{book1, book2}
```

If you do

```go
bkSlice = bkSlice[:1]
```

`book2` will be inaccessible, but cannot be GCed as part of the backing array, the the underlying `string` cannot be GCed.

If you do

```go
bkSlice[1] = Book{}
bkSlice = bkSlice[:1]
```

the `Book` struct will still be in memory, being the 2nd element of the backing array, but the struct would be a zero value and wouldn't hold any string reference. Thus, the original `title` and `author` can be GCed. 











