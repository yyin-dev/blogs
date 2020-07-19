****

**Source:** [Commonly Asked C++ Interview Questions | Set 1](https://www.geeksforgeeks.org/commonly-asked-c-interview-questions-set-1/)
**Difference and similarities between pointers reference and pointer?**  

Similarity: 

1. Both can be used to change local variables of a function inside another function.
2. Both can be used to reduce copying when passed as argument.

Difference: 

1. Reference is less powerful than pointers.
    - Once created, a reference can no longer be made to reference another object. Reference cannot be *reseated*. In contrast, a pointer can be made to point to other objects. This is by design. A reference is an alias of the existing variable, so the variable name and the reference name refers to the same memory location.
    - A reference cannot be 0/null. A pointer can be null.
    -  A reference must be initialized at declaration. A pointer does not have to.

2. References are safer.
    - Since references are always initialized, wild references like wild pointers does not exist.



**What are virtual functions? Write an example.**  

Virtual functions are used with inheritance. They are called according to the **type of object** pointed or referred, not according to the **type of pointer or reference**. In other words, virtual functions are resolved late, at **runtime** (**late binding**). The `virtual` keyword is used to make a function virtual.
A minimum runtime polymorphism example. Necessary components:

- A base case + a derived class;
- A function with same name in base class and derived class;
- A pointer/reference of base class type, but pointing/referencing to a derived type.

```c++
class Base { 
    public:	
    virtual void show() { cout << “From Base” << endl; }
};

class Derived: public Base {
    public:	
    void show() { cout << “From Derived” << endl; }
};

int main() {	
    Base *bp = new Derived();	
    bp->show(); // Runtime-polymorphism, “From Derived”	
    return 0;
}
```

Note that we are only making the member function of the Base class `virtual`. So when you call `show()` using a `Base*`, you get late-binding.

A destructor can be a virtual function but a constructor cannot be a virtual function.



**What is `this` pointer?** 

The *this* pointer is passed as a hidden argument to all non-static member function calls and is available as a local variable within the body of all non-static functions. *this* pointer is a **constant** pointer holding the memory address of the current object. It is not available in static functions.

**What happens if calling** `delete this` **?**

`delete` only works if the object is allocated using `new`, otherwise the behavior is undefined.

Once `delete` is called, any memeber of the deleted object should not be accessed.

The good practice is not to do `delete this`.

**What are VTABLE and VPTR?**

https://www.learncpp.com/cpp-tutorial/125-the-virtual-table/

Used to implement virtual functions and late binding.

`vtable`: a table of function pointers to virtual functions. Maintained for every **class** if it uses virtual fuction. Created at compile time.

`vptr`: a pointer to `vtable`, added by the compiler to the *most base* class that uses virtual functions. Maintained per **object**. 

Compiler adds code to maintain and use `vtable` and `vptr`:

1. In every constructor, sets `vptr` of every object to point to `vtable` of the class.
2. Code with polymorphic function class. When a polymorphic function call is made, compiler inserts code to look for `vptr` using base class pointer/reference. Once `vptr` is fetched, `vtable` on derived class can be accessed. 

Reconsider the example of `virtual` function above.

```c++
int main() {	
    Base *bp = new Derived();	
    bp->show(); // Runtime-polymorphism, “From Derived”	
    return 0;
}
```

`bp` is a of type `Base*`, so it only points to the `Base` portion of the class. However, since `__vptr` is in the base class, `bp` has access to `__vptr`. Since `__vptr->show` points to the vtable of class `Derived`, the correct `show` would be called though `bp` is of type `Base*`.  

Lastly, calling a virtual function can be more expensive then calling a non-virtual function.







**Source: **https://www.geeksforgeeks.org/commonly-asked-c-interview-questions-set-2/?ref=rp

**Access specifier in C++?**

`private`, `protected`, `public`.



**`struct` vs `class` in C++?**

`struct` and `class` are the same except:

- Members are by default public for struct, and private for class.

- When deriving a `struct` from a class/struct, default access-specifier for the base is `public`. When deriving a `class`, it is `private`. Example:

    ```c++
    class Base { 
    public: 
        int x; 
    }; 
      
    class Derived : Base { }; // equilalent to class Derived : private Base {} 
      
    int main() { 
      Derived d; 
      d.x = 20; // compiler error: inheritance is private 
      return 0; 
    } 
       
    // Program 4 
    class Base { 
    public: 
        int x; 
    }; 
      
    struct Derived : Base { }; // equilalent to struct Derived : public Base {} 
      
    int main() { 
      Derived d; 
      d.x = 20; // works fine: inheritance is public 
      return 0; 
    } 
    ```

    https://stackoverflow.com/a/860353/9057530

    https://stackoverflow.com/a/1372858/9057530



**`malloc` vs `new`/ `free` vs `delete`**

`malloc` and `free` is in C, while `new` and `delete` are in C++.

- `new` is an opeartor, while `malloc` is a function

- `new` returns exact data type, while `mallco` returns `void*`

- `new` calls constructors of the class, while `malloc` does not.

- Syntax

    ```c++
    int *n = new int(10);
    int *m = (int *) malloc(sizeof(int));
    ```

- `free` is used on resources allocated by `malloc`/`calloc`, `delete` is used on `new`.



**Difference between macro and `const` in C++?**

`const` is best thought of as a unmodifiable **variable**. It has all the properties of a variable, like type, scope, address, etc, except modifiability. 

A macro expands to tokens at pre-processing time. There's no type checking on macro. There's no scope for macro. Difficult to debug.



**Inline function**

A function could be made `inline` to reduce calling overhead. An inline function is expanded in line when gets called. The substitution is done at compile time. However, `inline` is only a request to the compiler instead of a command. The compiler could ignore the request.



**Difference between macro and inline function?**

Macros are **pre-processed before compilation** by the preprocessor. Inline functions are type-checked and substituted as the body of the function at **compilation time** by the compiler.



**Friend class and function in C++?**

A friend class can access private and protected members of other classes which declaring it as friend. Similarity, a friend function can access private members of the class. A friend functino can be: (1) a method of another class; (2) a global function.

Excessive use of friends lessens the value of encapsulation.

Friendship is not mutual. Class A being a friend of class B doesn't make B a friend of A.

Friendship is not inheriented. 

Example:

```c++

class Node { 
private: 
    int key; 
    Node* next; 
  
    friend class LinkedList; // LinkedList can access private members of Node 
    friend int LinkedList::search();
    friend int mySearch();
}; 

int mySearch() {
    // ... 
}

```



**Function overloading vs Operator overloading**

Function overloading: multiple functions can have same name, if they have different type of parameters or different number of parameters. **However**, functions that only differ in return type is not allowed.

```c++
int addOne(int x) { return 1 + x; }
string addOne(string s) { return s + "1"; }
```

Memeber function overloading is not allowed if one of them is a static function.

Operating overloading: make operators work for user-defined class. Example:

```c++
class Complex { 
private: 
    int real, imag; 
public: 
    Complex(int r = 0, int i = 0): real(r), imag(i) {} 
      
    // Automatically called when '+' is used between two Complex objects 
    Complex operator + (Complex const &obj) {
         return Complex(real + obj.real, imag + obj.imag); 
    } 
}; 
```

Operator function is the same as normal functions. The only difference is that there's always the `operator` keyword. The above example can be rewritten using global function:

```c++
class Complex { 
private: 
    int real, imag; 
public: 
    Complex(int r = 0, int i =0)  {real = r;   imag = i;} 
	friend Complex operator + (Complex const &, Complex const &); 
}; 
  
Complex operator + (Complex const &c1, Complex const &c2) { 
     return Complex(c1.real + c2.real, c1.imag + c2.imag); 
} 
```



**Copy constructor**

A copy constructor is a constructor that initializes an object using another object of the same class. A copy constructor has type: `ClassName (const ClassName &old_obj);`.

```c++
Point {
private:
    int x, y;
public:
    // Normal constructor
    Point(int _x, int _y): x(_x), y(_y) {}
	
    // Copy constructor
    Point(const Point &p) { x = p.x; y = p.y; }
}
```

In C++, the copy constructor is called in following cases:

1. When an object of the class is **returned by reference**;
2. When an object of the class is **passed by value** as argument;
3. When an object is construted based on another object of the same class;
4. When compiler generates a temporary object.

However, there's no guarantee that the copy constructor would be called in those cases.

A copy constructor can be made private.



**When is user-defined copy constructor needed?**

If no user-defined copy construtor is provided, a default copy constructor is provided which uses a member-wise copy. We need to define our own copy constructor only if an object has pointers or runtime allocation of the resources. 

Default constructor only does shallow copy (copying the pointer). Deep copy is possible only with user-defined copy constructor.

```c++
MyClass t1, t2; 
MyClass t3 = t1;  // Copy constructor called
t2 = t1;          // Assignment operator called
```

More example: https://www.geeksforgeeks.org/copy-constructor-in-cpp/



**Copy constructor vs Assignment operator**

Syntax for copy constructor:

```C++
ClassName (const ClassName& obj);
```

Syntax for (copy) assignment operator:

```C++
ClassName& operator = (ClassName obj);        // when copy-and-swap can be used
ClassName& operator = (const ClassName& obj); // when copy-and-swap cannot be used
```

PS1: for details of implementing copy assignment operator correctly, refere to `./move_copy/copyAssignmentOperator.cpp`.  

PS2: for move semantics (move constructor and move assignment operator), refer to `./move_copy/moveSemantics.cpp`.





**`static` keyword in C++**

Three usages: inside a function, inside a class definition, in front of a global variable.

- Inside a function

    Used on variable. Once the static variable is initialized, it remains in memory until the end of the program. 

    ```c++
    int keepCallingCount() {
    	static int count = 0; 
    	
    	count++;
    	// ...
    }
    ```

    The line `static int count = 0;` would be executed only once.

- Inside class definition

    A static member variable has the same value in any instance of the class and does not require an instance of the class to exist. 

    Note that you cannot initialize a static class member inside the class. Moreever, the static class member must be initialized otherwise it would not be in scope.

    You can also have static member functions. A static member function does not require to be called on an instance of the class. **A static function can only operate on static members.**

    A static member function cannot be virtual.

    ```c++
    int IndexedObjects::objIndex = 0; // Correct initialization
    
    class IndexedObjects {
    private:
        static int objIndex;
        int index;
    public:
        IndexedObject() { index = objIndex++; }
        static void resetIndex() { IndexedObjects::objIndex = 0; }
    };
    ```

- As a global variable

    The use of `static` indicates that source code in other files of the project cannot access the variable. In other words, the scope is limited to the file.


**Source: ** https://www.tutorialspoint.com/cplusplus/cpp_interview_questions.htm

**keyword `volatile` **

https://stackoverflow.com/a/4437555/9057530

https://stackoverflow.com/a/72617/9057530



**What are abstract class and pure virtual function?**

An *abstract* class is a class designed to be used as a base class and won't be instantiated. An abstrat class contains at least one *pure virtual function*. 

A pure virtual function is a virtual function without implementation, but only declaration. It is declared by assigning 0 to it.

```c++
class AbstractBase {
public:
	virtual void show() = 0; // pure virtual function makes an abstract class
}
```

You cannot use an abstract class an a parameter type or return type, nor declare an object of an abstract class. However, you can declare pointers and references to an abstract class.

Virtual member functions are inherited. A class derived from an abstract base class is also abstract unless each pure virtual function is overidden in the derived class.

Note that you can derive an abstract class from a non-abstract class, and you can override a non-pure virtual function with a pure virtual function.



**When a class member function is defined outside the class, which operator can be used to associate the function definition to that class**?

Scope resolution operator, `::`.



**Destructor**

A destructor is called automatically when:

- for a function parameter and the function ends;
- the program ends;
- a block containing locla variable ends;
- a delete opeartor is called.

A destrutor has the same name as the class, but preceeded by a tilde `~`. Destructor has no argument and does not have return type: `~ClassName() {...}`.

A destructor cannot be overloaded.

The default constructor works fine unless the class members have dynamically allocated memory or pointer. When the class contains a pointer to memory allocated, you should write a user-defined constructor to release memory before the class instance is destroyed. Otherwise, we have memory leak.

It's good practice for the destructor in base class to be virtual.



**C++ vector**

vector is implemented as dynamically allocated array with resizability. Vector elements are placed in **contiguous** storage and can be accessed using index directly. Inserting at end takes constant time, if no resizing happens. Insertion/Erasure at head takes linear time because shifting is required.



