// Source: https://docs.microsoft.com/en-us/cpp/cpp/how-to-create-and-use-shared-ptr-instances?view=vs-2019#example-5
// Example 5 says the general rule of using shared_ptr.

// Designed usage of shared_ptr:
// More than one owner have to manage the lifetime of the object in memory.
// You can copy, pass by value, assign to other shared_ptr. All the shared_ptr
// instances point to the same object, and share access to a "control block",
// which contains a reference count. When the reference count reaches 0,
// the memory resouce is deallocated by the control block.

#include <algorithm>
#include <iostream>
#include <memory>
#include <string>
#include <vector>

using namespace std;

struct MediaAsset {
    virtual ~MediaAsset() = default;  // make it polymorphic
};

struct Song : public MediaAsset {
    string artist, title;
    Song(const string& a, const string& t) : artist(a), title(t) {}
};

struct Photo : public MediaAsset {
    string date, location, subject;
    Photo(const string& d, const string& l, const string& s) : date(d), location(l), subject(s) {}
};

int main() {
    // Example 1
    // Use make_shared function when possible.
    auto sp1 = make_shared<Song>("The Beatles", "Im Happy Just to Dance With You");

    // Ok, but slightly less efficient.
    // Note: Using new expression as constructor argument
    // creates no named variable for other code to access.
    shared_ptr<Song> sp2(new Song("Lady Gaga", "Just Dance"));

    // When initialization must be separate from declaration, e.g. class members,
    // initialize with nullptr to make your programming intent explicit.
    shared_ptr<Song> sp5(nullptr);
    //Equivalent to: shared_ptr<Song> sp5;
    //...
    sp5 = make_shared<Song>("Elton John", "I'm Still Standing");

    // Example 2
    //Initialize with copy constructor. Increments ref count.
    auto sp3(sp2);

    //Initialize via assignment. Increments ref count.
    auto sp4 = sp2;

    //Initialize with nullptr. sp7 is empty.
    shared_ptr<Song> sp7(nullptr);

    // Initialize with another shared_ptr. sp1 and sp2
    // swap pointers as well as ref counts.
    sp1.swap(sp2);
}