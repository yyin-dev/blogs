来源：https://www.nowcoder.com/discuss/377389?type=2&order=0&pos=4&page=1

**虚函数表中放什么，虚函数表是子类和父类共用一份吗？**
vptr每个object都有，vtable每个class都有一份。
constructor不可以是虚函数，destructor可以。



**new operator, operator new，以及placement new**
https://www.geeksforgeeks.org/new-vs-operator-new-in-cpp/
https://stackoverflow.com/q/1885849/9057530

TLDR:

- `new` operator` calls `operator new` to allocate memory, then uses placement new to construct the object.  
- placement new calls the constructor. Placement new can be used for both heap and stack.
- Use `delete` for `new`. Use explicit destructor call for placement new.  
- Placement new is used for (1) performance; (2) when you want object to be constructed to be at a specific memory location.   


Detail

(1) new operator指的是最常见的用法：Foo foo = new Foo();。其实new是一个keyword而不是operator。
(2) operator new是一个member function。可以在class中override opeartor new来改变heap
allocation的方式。该函数的signature: `void* operator new(size_t size)`;

```c++
1. class Foo {
     Foo() {}

public:
    void* operator new(size_t s) {
        cout << "operator new called!" << endl;
        void *p = malloc(s);
        return p;
    }
}；
```


new keyword会调用operator new函数。When you override `operator new`, it is good practice to also override `operator delete`.

(3) placement new

https://www.geeksforgeeks.org/placement-new-operator-cpp/

https://stackoverflow.com/a/8918942/9057530

In placement new, we can pass preallocated memory and construct an object in the passed memory.

* Normal new allocates memory on heap. For placement new, the object construction can be done at **known address** (both heap and stack!)
* The deallocation is done using `delete` for normal new, but theres's no placement delete. If needed, write it in the destructor.

Syntax:

```c++
new (address) type(initializer)
```

When is placement new better? Answer: placement new allows constructing an object on memory that's already allocated. So this reduces deallocation and reallocation.

Normal `new` keyword does two things: allocate memory on heap using `operator new` + call the constructor. So

```c++
Foo *f = new Foo();
// is equivalent to
void* raw = Foo::operator new(sizeof(Foo)); // operator new would call malloc()
Foo* f = new(raw) Foo(); // Placement new!
```

And similarity, `delete` keyword would call the destructor of the class first, and then deallocate the memory.

```c++
//normal version                   calls these two functions
MyClass* pMemory = new MyClass;    void* pMemory = operator new(sizeof(MyClass));
                                   MyClass* pMyClass = new(pMemory) MyClass();

//normal version                   calls these two functions
delete pMemory;                    pMyClass->~MyClass();
                                   operator delete(pMemory);
```

How to delete memory allocated by placement new? Explicit Destructor call.

```c++
// Allocate memory
char* pMemory = new char[sizeof(MyClass)];

// Construct the object ourself
MyClass* pMyClass = new(pMemory) MyClass();

// The destruction of object is our duty.
pMyClass->~MyClass();
```



智能指针 (TODO: 关于unique_ptr, shared_ptr, weak_ptr的具体用法原理还没搞明白)

详见`../CPP/smartPointers.cpp`.