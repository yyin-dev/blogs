// lvalue and rvalue
// https://www.internalpointers.com/post/understanding-meaning-lvalues-and-rvalues-c
// Informal definition:
// lvalue: something pointing to a specific memory location
// rvalue: something that doesn't point anywhere.
// In general, rvalues are temporary and lvaues have longer life as they are
// variables. Without being assigned to a lvalue, the rvalue expires.

// TLDR:
// 1. An lvalue is something with specific memory locaiton, while an rvalue
// is only a temporary value.
// 2. Legal: Assign rvalue to lvalue;
//           Convert lvalue to rvalue;
//           Create reference to (const/non-const) lvalue;
//      ---> Create const reference to rvalue.
//    Illegal: Assign to rvalue;
//             Convert rvalue to lvalue
//             Create non-const reference to rvalue;
// 3. function call
// Function call of a function returning rvalue is an rvalue;
// ..................................... lvalue ..... lvalue.


// 1. Legal: assign ravlue to lvalue
int x = 66;
int* y = &x;
// x is lvalue, 66 is rvalue. Legal.
// & takes an lvalue and returns an rvalue.
// So y is lvalue and &x is an rvalue. Legal.

// 2. Illegal: assign to rvalue
// 666 = y;
// 666 is rvalue and y is lvalue.

// 3. Functions returning lvalues and rvalues
// Function call of a function returning rvalue is an rvalue;
// ..................................... lvalue ..... lvalue.

int returnRvalue() {
    return 42;
}
// returnRvalue() = 2;
// returnRvalue() is an rvalue. Cannot assign to rvalue.
int global = 42;
int& returnLvalue() {
    return global;
}
// returnLvalue() = 100;
// A reference is an lvalue, so returnLvalue() is an lvalue. Can assign to lvalue.

// 4. Legal: lvalue -> rvalue
// The conversion from lvalue to rvalue is legal and common.
// opeartor + takes two rvalues and return an rvalue.
int conversionDemo() {
    int x = 1;
    int y = x + 1;
    // x is an lvalue, but converted to an rvalue.
}

// 5. lvalue reference
int& xRef = x;
// int& refToTen = 10; // illegal
// Two possible ways that we guess C++ can do:
// Create a reference to an rvalue:
// A reference must refer to a lvalue. If reference to rvalue is
// allowed, the value of a literal can be changed, which makes
// no sense!
// Convert an rvalue to an lvalue:
// This is forbidden in C++.
//
// Neither way works. So this is illegal.
int lvalueReference() {
    int x = 42;
    int& xRef = x;  // Legal, lvalue reference
    // int& r = 42; // illegals
}

// So the following also fails.
int refArg(int& x) { return 42; }
// int y = refArg(10); // Fails on int literal input

// So if you make the argument of reference, the function
// does not accept literal input. This is inconvenient!!!

// 6. Solution: const lvalue reference
// It's legal to bind a const lvalue to an rvalue.
// The *const* eliminates the problem of modifying a rvalue.
int constRefArg(const int& x) { return 42; }
int y = constRefArg(0);

// Under the hood, the compiler creates an hidden variable for you.
int __temp = 10;
int z = constRefArg(__temp);

int main() {
    const int & consRefToRvalue = 10;

    // int& nonConsRefToRvalue = 10; 
    // G++ error: initial value of reference to non-const must be an lvalue
    return 0;
}