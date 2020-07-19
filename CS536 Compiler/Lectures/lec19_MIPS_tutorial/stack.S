.text
.globl main
main:
  	li $t0 4

  	# push
  	sw $t0 0($sp)  # put value
  	subu $sp $sp 4 # adjust stack pointer

  	# pop
  	addu $sp $sp 4
  	lw $t2 0($sp)

  	jr $ra