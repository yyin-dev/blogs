.text
.globl main
main:
  li $t0 1
  li $t1 41
  li $t2 -1
  add $t2 $t1 $t0

  move $a0 $t2 # move between registers
  li $v0 1
  syscall

  jr $ra