#include <memory>
using namespace std;

// https://docs.microsoft.com/en-us/cpp/cpp/smart-pointers-modern-cpp?view=vs-2019

// Gaol of smart pointer: RAII. Resource Acquisition Is Initialization.
//
// Principle: give ownership of any heap-allocated resource to a
// stack allocated object whose destructor contains the code
// to delete/free the resource and any cleanup code.

// In modern C++, raw pointer is used at very limited scope.
// Compare a raw pointer and a smart pointer declaration.

struct Foo {
    int bar;

    Foo(int x) : bar(x) {}
};

void useRawPointer() {
    // Use raw pointer
    Foo* fooPtr = new Foo(42);

    // ...

    // Must manually delete it
    delete fooPtr;
}

void UseSmartPointer() {
    // Declare a smart pointer on stack and pass it the raw pointer
    unique_ptr<Foo> fooUniqPtr(new Foo(42));

    // ...

    // fooUniqPtr is automatically deleted
}

// The destructor of smart pointers contains the call to delete
// the pointer it owns, at correct time.
// Always create smart pointers on a separate line, but not in
// a parameter list. To avoid subtle bugs.

// You can use -> and * on smart pointers as if on raw pointers.
// Example:
class LargeObject {
   public:
    void DoSomething() {}
};

void ProcessLargeObject(const LargeObject& lo) {}

void SmartPointerDemo() {
    // Create the object and pass it to a smart pointer
    std::unique_ptr<LargeObject> pLarge(new LargeObject());

    //Call a method on the object
    pLarge->DoSomething();

    // Pass a reference to a method.
    ProcessLargeObject(*pLarge);

}  //pLarge is deleted automatically when goes out of scope.

// Essential rules for using smart pointers:
// 1. Declare as local variable on the stack.
// 2. In the type parameter, specify the type of the encapsulated pointer.
// 3. Pass a raw pointer to a dynamically allocated to the smart pointer constructor.
// 4. Use -> and * to access the object.
// 5. Let the smart pointer delete the object.

// Member funtions of smart pointers
void SmartPointerDemo2() {
    // Create the object and pass it to a smart pointer
    std::unique_ptr<LargeObject> pLarge(new LargeObject());

    //Call a method on the object
    pLarge->DoSomething();

    // Free the memory BEFORE we exit function block.
    pLarge.reset();

    // Do some other work...
}


// Access raw pointer
void LegacyLargeObjectFunction(LargeObject* p) {}
void SmartPointerDemo4() {
    // Create the object and pass it to a smart pointer
    std::unique_ptr<LargeObject> pLarge(new LargeObject());

    //Call a method on the object
    pLarge->DoSomething();

    // Pass raw pointer to a legacy API
    LegacyLargeObjectFunction(pLarge.get());
}


// Common smart pointers
// unique_ptr
// shared_ptr
// weak_ptr 