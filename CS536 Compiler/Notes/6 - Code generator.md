# Code Generator



## Runtime Environment

Two main issues: variable storage/access; function call entry/execution/return.

- Stack and Heap

  We can put static data (like strings) in the global region to reduce stack size. Local variables (in function calls) are in the function frame.  

  Static memory allocation (each function has a fixed memory range) doesn't allow recursive calls. Dynamic allocation of function frame (aka "activation record") on the stack. The stack handles local variables whose size are unknown at compile-time by allocating stack space dynamically, so stack size cannot be determined at compile-time! 

  The activation record stores: local variables, information about caller: data context + control context. 

  The stack grows from high address to low address, while the heap grows from low address to high address.

  ```
  +-----------+ high
  |	stack	| 	|
  |	 		|	V
  +-----------+
  |			|
  |			|
  +-----------+
  |			|   ^
  |	heap	|	|
  +-----------+ low
  ```

- Function call implementation

  $ip, the *instruction pointer* stores the line of code currently being executed. 

  Frame pointer (\$fp) tracks the base of the AR.

  Stack pointer (\$sp) tracks the top of the stack.

  - Function entry: caller responsibility

    Store caller-saved registers in its own AR;

    Reserve space for return value and push arguments onto stack; [*]

    Copy return address out of $ip; [*]

    Jump to callee's first instruction. 

  - Function entry: callee responsiblity

    Push caller's $fp onto stack (control link); [*]

    Update $fp to point to the base of the new AR;

    Save callee-saved registers;

    Make space for local variables.

    Note: actions marked with [*] pushes data onto stack. The size of pushes data can be determined at compile-time, using information from symbol table. 

  - Function exit: callee responsibility

    Set the return value;

    Restore callee-saved registers;

    Copy return address (stored on stack) into $ra;

    Restore old $sp: fixed offset from current value of \$fp. 

    Restore old $fp from stack.;

    Jump to address stored in $ra.

  - Function exit: caller responsiblity

    Get return value;

    Restore caller-saved registers.

- Parameter Passing Implementation

  When pass by value, value of argument is on the stack;

  When pass by reference, reference (pointer to argument) is on the stack. 

- Runtime Variable Allocation and Access

  Three kinds of scope: local, global, non-local. 

  - Local variable

    Calculate variable's address on stack (negative offset from $fp), then use `lw` and `st` to access stack memory. Add an `offset` field to symbol table entry, and calculate the `offset` during name analysis. 

  - Global variable

    Allocated in static data during compile time. Referred directly by name, instead of address. 

  - Non-local variable (static scope)

    Option 1

    Add an additional field, *access link*, to the AR, pointing to local variable area in the outer function. We can follow the access link to access variable defined in outer scope. 

    Why we need access link when we already have control link? Answer: control link points to the caller function, while access link points to the outer function/scope. **The outer function is not necessarily the caller function!** Consider function f1 has a nested function f2 which refers to local variable in f1. Now f1 calls f2, which makes recursive call to f2 itself.

    Option 2

    Instead of store an access link in each AR, build a table that stores pointer to the local variable area of all functions. 



## Code Generation

- DeclNode

  - StructDeclNode
  - FnDeclNode
  - VarDeclNode

- FnDeclNode (funtion declaration&definition)

  - Preamble
  - Prologue
  - Body
  - Epilogue

- CallExpNode (function call)

  Reserve return value slot;

  Push arguments onto stack;

  Jump to callee's lable;

  Retrieve result value.

- StmtNode

  Expression evaluation. Post-order traversal of the AST.

  Normal operation: At operand: push value onto stack. At operator: pop operand values from stack, compute result, and push onto stack.

  Assignment: (1) compute RHS expression on stack; (2) compute LHS location on stack; (3) pop LHS into \$t1; (4) pop RHS into ​\$t0; (5) store ​\$t0 at address $t1.

  Dot access: use offset from the base of the struct.

- Control flow