#include <iostream>

using namespace std;

// http://www.cplusplus.com/doc/oldtutorial/templates/

// Function template
// Create functions that adapt to different types without repeating.
//
// Like regular functions parameters can be used to pass values to
// a function, template parameters allow to pass types to functions. 
// The function templates can use the type as if they are regular types.
//
// Format for declaring function templates with type parameters

/* template <class identifier> function_declaration; */
/* template <typename identifier> function_declaration; */

// The two declarations are exactly the same except for the syntax.

// Example:
template <class myType>
myType getMax(myType a, myType b) {
    return a > b ? a : b;
}

// The template parameter represents a type that has not yet been specified, 
// but can be used in the template function as if it were a regular type. 
//
// To use the function template, use the following format for function call:

/* function_name <type> (parameters); */

// The compiler would use the template to generate functions to replace each
// appearance of myType by the type passed as the actual template parameter.

template <class T>
T getMin(T a, T b) {
    T res;
    res = a < b ? a : b;
    return res;
}

// We can define function templates with more type parameters
template <class T, class U>
T returnFirst(T a, U b) {
    return a;
}

int main() {
    int x = 1, y = 2;
    cout << getMax<int>(x, y) << endl;
    cout << getMin<int>(x, y) << endl;
    cout << returnFirst<double, int>(1.11, 2) << endl;
    return 0;
}
