// Source: https://stackoverflow.com/a/3279550/9057530

// Among the big three (copy constructor, copy-assignment operator, destructor),
// the copy-assignment operator is the most diffcult one.
//
// Copy-and-swap idiom is the solution.

// The copy-assignment operator should make a copy of the argument
// before destroying the original data. Then it should assign the
// copied data into its own.

// The copy-assignment needs a working copy constructor and a 
// swap function definition. So the big-three should really be
// called the big-three-and-half.

// The copy constructor is implictly called during argument construction.
// So once the function is entered, the copying process cannot throw
// any exception. This also avoid duplicating code written in copy
// constructor.

// Actually, since move opeartor is added in C++11, the rule of
// big-three-and-half should be big-four-and-half.