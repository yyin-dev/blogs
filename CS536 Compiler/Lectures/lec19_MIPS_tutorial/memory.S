.data
.align 4 # Not needed if you use .word, .byte, etc.
myLabel: .word 5 # a word is 32 bit
myString: .asciiz "hello world\n" # automatically terminated
myInt: .word 42
.text
.globl main
main:
  		lw $t0 myLabel

  		la $a0 myString # load an address, a0 for system call arg
  		li $v0 4
  		syscall # print string

  		lw $a0 myInt
  		li $v0 1
  		syscall # print int

  		jr $ra
