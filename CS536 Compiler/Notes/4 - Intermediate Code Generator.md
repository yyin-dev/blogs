# Intermediate Code Generator

With annotated AST and symbol table, we entered the backend of compiler. Two possible designs: (1) Intermediate Representation -> Optimized IR -> Machine Code; (2) Directly to machine code. 

- Intermediate Representation pros and cons:
  - Pro: easier to optimize, more flexible output options (e.g. Java), make code generation easier. 
  - Con: the compilation takes longer, requires more engineering.

  Our language skips IR for easier implementation. 