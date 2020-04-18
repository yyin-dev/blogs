#include <iostream>
#include <string>

using namespace std;

// http://duramecho.com/ComputerInformation/WhyHowCppConst.html

const string* returnFixedStr() {
    // There's nothing on the left of `const`, so it works on the right:
    // the string is constant.
    static string unmodifiable("Cannot be modified");
    return &unmodifiable;
}

void passConstReference(string const& constStr) {
    //                           ^-- `const` works on the left:
    //                               the string is constant
    cout << constStr << endl;
}

class MyClass {
    int x;

    void cannotModifyObject() const {
        //                      ^--- Cannot change member variable
        cout << x << endl;
    }

    // http://c-faq.com/decl/spiral.anderson.html
    const int* const complicated(const int* const&) const {
        // 1      2                3          4        5
        // 1: a pointer to a constant int
        // 2: constant pointer
        // 3: a pointer to a constant int
        // 4: constant pointer
        // 3 + 4 : a reference of a constant pointer to a constant int
        // 5: Cannot modify the intance.
    };
};

int main() {
    // 1. most basic
    const int i = 1;

    // 2. pointer declaration

    // A pointer to a constant int
    const int* ptrToConstantInt;

    // A constant pointer to a changable int
    int j = 2;
    int* const constantPtrToInt = &j;  // Must be initialized

    // A constant pointer to a constant integer
    const int* const constantPtrToConstantInt = &i;  // Must be initialized

    // The rule is that `const` applies to the thing immediately to its left.
    // Only if there's nothing on the left, then it applies to the thing
    // immediately to the right.

    // 3. Use `const` in function return value
    // Check returnFixedStr
    const string* ptrToConstString = returnFixedStr();
    // string* ptrToNonconstString = returnFixedStr(); // Not allowed

    // 4. Parameter passing
    // Check passConstReference()

    // 5. In class methods
    // Check MyClass::canChangeMemberVar

    return 0;
}
