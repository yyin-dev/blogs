#include <algorithm>
#include <iostream>
#include <memory>
#include <string>
#include <vector>

using namespace std;

// The control block of shared_ptr keeps reference count. However, the problem
// is circular references.
// Source:
// https://stackoverflow.com/a/106614/9057530
// https://www.learncpp.com/cpp-tutorial/15-7-circular-dependency-issues-with-stdshared_ptr-and-stdweak_ptr/
struct Owner {
    std::shared_ptr<Owner> other;

    ~Owner() { cout << "~Owner() called" << endl; }
};

void circularRef() {
    cout << "In circularRef: " << endl;
    std::shared_ptr<Owner> p1(new Owner());
    std::shared_ptr<Owner> p2(new Owner());
    p1->other = p2;  // p1 references p2
    p2->other = p1;  // p2 references p1

    // Oops, the reference count of of p1 and p2 never goes to zero!
    // The objects are never destroyed!
    cout << "Destructor is not called!" << endl;
}

// Solution: weak_ptr
// weak_ptr is designed to solve the cyclic reference problem.
// A weak_ptr is an observer. It can access the same object as a shared_ptr but
// is not considered an owner. In other words, it is not considered for
// refernce count.
struct WeakOwner {
    std::weak_ptr<WeakOwner> other;

    ~WeakOwner() { cout << "~WeakOwner() called" << endl; }
};

void useWeakPtr() {
    cout << "In useWeakPtr: " << endl;

    shared_ptr<WeakOwner> wo1(new WeakOwner());
    shared_ptr<WeakOwner> wo2(new WeakOwner());
    wo1->other = wo2;
    wo2->other = wo1;
}

// https://docs.microsoft.com/en-us/cpp/cpp/how-to-create-and-use-weak-ptr-instances?view=vs-2019
// You can use a weak_ptr to try to obtain a new copy of the shared_ptr with
// which is was initialized. If the memory has already been deleted, the
// weak_ptr's bool operator returns false. If the memory is still valid,
// the new shared_ptr can be created.

// Note that weak_ptr cannot be initialized with a raw pointer. It can only be
// initialized with a shared_ptr or another weak_ptr.
int main() {
    circularRef();
    useWeakPtr();
    return 0;
}