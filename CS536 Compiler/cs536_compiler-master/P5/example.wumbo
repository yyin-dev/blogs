bool bbb;
struct S { int a; };
struct S t;

void f(int p1, bool p2) {
}

void main() {
    int aaa;

    // some errors involving illegal operations on functions
    aaa = f + f;
    bbb = f == 3;
    bbb = f > main;
    bbb = f == f;

    // some errors involving bad function calls 
    aaa();
    f();
    f(5);
    f(aaa, aaa);
    f(t, t.a);
    S();

    // some errors involving structs 
    t = 5;
    t = t;
    cin >> S;
    t = true || 3;
    bbb = S && t.a;

    // error involving return
    return 10;

    // other type errors 
    aaa = aaa + (!false);
    aaa = aaa + "foo";
    bbb = aaa;

    // don't give too many or too few error messages here 
    bbb = (aaa + true) && (bbb < true);
}
