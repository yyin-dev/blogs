# Overview

## Phases of a compiler

```
  source program
		| sequence of tokens
	   	V
1. lexical analyzer (scanner)
		| sequence of tokens
	   	V
2. syntax analyzer (parser)
		| abstract syntax tree (AST)
	   	V
3. semantic analyzer
		| Augmented, annotated AST
	   	V
4. intermediate code generator
		| Intermediate code
		|								frontend
--------------------------------------------------------
		|								backend
		V
5. optimizer							
		| Optimized Intermediate code
		V
6. code generator
		| Assembly/machine code
		V
object program
```



## Scanner

Input: Characters from source program 
Output: Sequence of tokens 
Actions: Group characters into tokens. Identify and ignore comments, whitespaces, etc   

## Parser
Input: Tokens from scanner 
Output: AST 
Actions: Group tokens into sentences  

## Semantic analyzer

Input: AST
Output: Annotated AST 
Actions: Static semantic checks, like *name analysis*, *type checking*.

## Intermediate code generation

Input: Annotated AST 
Output: Intermediate Representation (IR)

## Optimizer
Input: IR 
Output: Optimized IR

## Code generator
Input: Optimized IR
Output: Target code