#include <iostream>

using namespace std;

// 1. Basics
// Class templates create class that can use template parameter as types.
template <class T>
class myPair {
    T values[2];

   public:
    myPair(T first, T second) {
        values[0] = first;
        values[1] = second;
    }

    // member function declartion
    T getMax();
};

// member function definition
// Must preceed definition with template <...> prefix
template <class T>
T myPair<T>::getMax() {
    //   ^------ required to specify that the function's template parameter
    //           is also the class template parameter
    return max(values[0], values[1]);
}



// 2. Template specialization
// Define a different implementation for a template when a specific
// type is passed as template parameter.

template <class T>
class mycontainer {
    // holds only one element
    T element;

   public:
    mycontainer(T arg) { element = arg; }
    T increase() { return ++element; }
};

// class template specialization for char type
template <>  // To explicitly declare a template specification
class mycontainer<char> {
    //             ^---- specific type
    char element;

   public:
    mycontainer(char arg) { element = arg; }
    char uppercase() {
        if ((element >= 'a') && (element <= 'z'))
            element += 'A' - 'a';
        return element;
    }
};

// Compare generic class template and the specialization

/* template <class T> class mycontainer {...}; */
/* template <> class mycontainer <char> {...}; */

// When declaring specializations for a template class, we must also define
// all its members, even those exactly equal to the generic template class,
// because there is no "inheritance" of members from the generic template
// to the specialization.



// 3. Non-type parameters
// Class templates can also have regular typed parameters.
template <class T, int N>
class mysequence {
    T memblock[N];

   public:
    void setmember(int x, T value);
    T getmember(int x);
};

template <class T, int N>
void mysequence<T, N>::setmember(int x, T value) {
    //          ^-^----- Required for definition out of class
    memblock[x] = value;
}

template <class T, int N>
T mysequence<T, N>::getmember(int x) {
    //       ^-^----- Required for definition out of class
    return memblock[x];
}

// Functions/classes are generated using templates on demand at instantiation.

int main() {
    myPair<int> intPair(1, 2);
    myPair<double> doublePair(1.1, 2.2);

    // template specification
    mycontainer<int> myint(7);
    mycontainer<char> mychar('j');
    cout << myint.increase() << endl;
    cout << mychar.uppercase() << endl;

    // non-type parameters for template
    mysequence<int, 5> myints;
    mysequence<double, 5> myfloats;
    myints.setmember(0, 100);
    myfloats.setmember(3, 3.1416);
    cout << myints.getmember(0) << '\n';
    cout << myfloats.getmember(3) << '\n';

    return 0;
}