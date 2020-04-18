#include <iostream>
#include <memory>
#include <vector>
using namespace std;

// Source: https://docs.microsoft.com/en-us/cpp/cpp/how-to-create-and-use-unique-ptr-instances?view=vs-2019

struct Song {
    string artist, title;
    Song() : artist("foo"), title("bar") {}
    Song(const string& a, const string& t) : artist(a), title(t) {}

    void info() {
        cout << artist << ", " << title << endl;
    }
};

// unique-ptr instances cannot be copied, to prevent the pointer from being
// deleted multiple times. It can only be moved.
// So you cannot do anything with it that involves copying:
//   copy it to another unique_ptr;
//   pass by value to a function;
//   use in C++ standant library algorithm that requires copies.
// But you can move it using std::move. The move constructor would be called.

// It is recommended to use make_unique to construct it, introduced in C++14.

// Example1: unique_ptr return type
unique_ptr<Song> SongFactory(const string& artist, const string& title) {
    return make_unique<Song>(artist, title);
    // By returning unique_ptr, implicit move operation is performed!
}

void MakeSongs() {
    auto song1 = make_unique<Song>("Baby", "Justin Biber");

    unique_ptr<Song> song2 = std::move(song1);
    if (song1.get() == NULL) {
        cout << "unique_ptr is nulled after std::move!" << endl;
    }

    auto song3 = SongFactory("22", "Taylor Swift");
}

// Example 2: pass by reference
void SongVector() {
    vector<unique_ptr<Song>> songVec;

    songVec.push_back(make_unique<Song>("22", "Taylor"));
    songVec.push_back(make_unique<Song>("Baby", "Justin Biber"));

    for (const auto& song : songVec) {  // Pass by reference!!
        // If you pass by value, the compiler would complain:
        //
        // uniquePointer.cpp: In function 'void SongVector()':
        // uniquePointer.cpp:45:25: error: use of deleted function 'std::unique_ptr<_Tp, _Dp>::unique_ptr(const std::unique_ptr<_Tp, _Dp>&) [with _Tp = Song; _Dp = std::default_delete<Song>]'
        //   for (const auto song : songVec) { // Pass by reference!!
        //                          ^~~~~~~
        // In file included from c:\mingw\lib\gcc\mingw32\8.2.0\include\c++\memory:80,
        //                  from uniquePointer.cpp:2:
        // c:\mingw\lib\gcc\mingw32\8.2.0\include\c++\bits\unique_ptr.h:394:7: note: declared here
        //        unique_ptr(const unique_ptr&) = delete;
        //        ^~~~~~~~~~
        //
        // This says that the copy constructor is a deleted function.
        cout << "Artist: " << song->artist << ", title: " << song->title << endl;
    }
}

// Example 3: initialize unique_ptr as a class member
class MyClass {
   private:
    unique_ptr<Song> favSong;

   public:
    // Initialize by using make_unique with Song default constructor.
    MyClass() : favSong(make_unique<Song>()) {}

    void call() {
        favSong->info();
    }
};

// Example 4: unique_ptr with array
// You can use make_unique to create a unique_ptr to an array, but you cannot
// use make_unique to initialize the array elements.
void withArray() {
    // Create a unique_ptr to an array of 5 integers.
    auto p = make_unique<int[]>(5);

    // Initialize the array.
    for (int i = 0; i < 5; ++i) {
        p[i] = i;
        cout << p[i] << endl;
    }
}

int main() {
    MakeSongs();
    SongVector();
	withArray();
    return 0;
}