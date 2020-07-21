# Semantic Analysis

Semantics: the meaning of a program. The parser can guarantee that the program is constructually correct, but may not be meaningful. Possible problem: undeclared variables, redeclared variables, type mismatch, etc. 

Two phases: name analysis, type analysis. 



## Name analysis

- Associating identifiers with their uses, major activities: 
  - build symbol table
  - find name problems
  - add `IdNode` links to symbol table entries

- Symbol table entry information: kind (struct, variable, function), type (int, int x string -> bool), scope, etc.
- Symbol table operations: insert entry, lookup, add new table, remove a table.
- Many design choices: static/dynamic scoping, overloading, variable shadowing, foward references...
- Our language: static scope, variable shadowing allowed
- Implementation choice: a list of hashmaps
- Build symbol table: 
  - recursively walk the AST, augmenting AST nodes (where names are used) with link to relevant symbol table entry
  - Put new entries into the symbol table when encoutering declaration



## Type analysis

- Type system component

  - Primitive types + operator for building more complex types
  - Means of determining if types are compatible
  - Type inferring rules

- Implementation: similar to name anslysis, recursively walk the AST checking types. Examples:

  - Binary operator

    Get type of left child; get type of right child; check that types are compatible with operator; Set the type of the node.

  - Literal

    Cannot be wrong. Send up the tree. 

  - IdNode

    Look up the name in the symbol table and send up the tree.

  - Function calls

    Get type of each arguments; match against parameters; send return type up the tree. 

  - Statement

    No type.

- Error reporting

  Goal: don't give up at the first error; don't report the same error multiple times.

  Strategy: when type incompatibility is discovered at curret node, report error, and pass it up the tree. Don't report error if you get error type as operand, but just pass it up the tree. 



