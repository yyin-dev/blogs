#include <iostream>
using namespace std;

// Before talking about move semantics, review copy semnatics.
// Copy semantics: copy constructor + (copy) assignment operator
//
// Copy constructor syntax:
// ClassName(const ClassName& obj);
//
// Assignment operator syntax:
// ClassName& operator = (const ClassName& obj);
//
// Copy constructor is called when:
// 1. Return by referece;
// 2. Pass by value;
// 3. Initialize new object from existing one;
// 4. Compiler constructs temporary object.
// Though there's no guarantee.

// Rule for three:
// Whenever you define one of the following three, you may need all three.
// 1. destructor
// 2. copy constructor
// 3. copy assignment operator

// Move semantcics
// Source: https://stackoverflow.com/a/3109981/9057530

// Consider a simple wrapper around heap-allocated char array.
class myString {
private:
	char* data;

public:
	// Constructor
	myString (const char* p) {
		size_t size = strlen(p) + 1;
		data = new char[size];
		memcpy(data, p, size);
	}

	/* As the class has pointer member, need copy constructor. */
	// Copy constructor
	myString (const myString& that) {
		// Make a deep copy
		size_t size = strlen(that.data) + 1;
		data = new char[size];
		memcpy(data, p, size);
	}

	// assignment operator (defined later)
	myString& operator = (myString that);

	void swap(myString& first, myString& second) {
		using std::swap; // necessary
		swap(first.data, second.data);
	}

	// Move constructor (defined later)
	myString (myString&& that);

	// Destructor
	~myString() {
		delete[] data;
	}
};

// Now consider the following use cases.
void example() {
	myString x("Hello world!");
	myString y("FooBar");

	myString a(x);     // (1)
	myString b(x + y); // (2)
}

// The copy constructor is called at both (1) and (2), as we are 
// initializeing a new object from an existing one.
// Our copy constructor does deep copying. However, deep 
// copy is only needed for (1), but not (2). Why?
// As x is a lvalue, the user could refer to it later. So
// we must do a deep copy and leave x untouched. However,
// x + y is a rvalue, which is temporary and the code
// cannot refer to it later. So during the initialization of
// b, we can do whatever we want with the source string!

// C++0x allows "rvalue reference", which allows us to detect
// rvalue arguments via function overloading and the rvalue
// reference parameter. Inside the funtion, we can do anything
// we want with the source.

// Move constructor
// Instead of making a deep copy, we just "moved"/"stolen" the data
// by modifying the pointer.
// This is the basics of move semantics!
myString::myString(&&myString that) {
	data = that.data;
	that.data = NULL;
}


// copy assignment operator
// copy-and-swap idiom, refer to copyAssignmentOperator.cpp.
myString& myString::myString(myString that) { // Pass by value
	// that would be constructed by:
	// 1. copy constructor, if it is a lvalue;
	// 2. move constructor, if it is a rvalue.
	swap(*this, that);
	return *this;
}