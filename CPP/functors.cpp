// 1. Without functor
// A function that increments an int by 1
int addOne(int x) {
    return x + 1;
}

// A function that increments an int by 2
int addTwo(int x) {
    return x + 2;
}

// Note that function template does not help here as it only helps 
// to generalize to different types.

// 2. With functor
// A functor is a class which defines operator()
struct addX {
    private:
    int x;

    public:
    addX(int _x): x(_x) {}
    int operator()(int y) { return x + y; }
};

int main() {
    addX add1(1), add2(2), add42(42);
}