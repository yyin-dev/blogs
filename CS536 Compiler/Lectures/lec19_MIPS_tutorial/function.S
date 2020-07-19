.text
.globl main
main:
	# store caller's $ip and control link
	sw $ra 0($sp)
	subu $sp $sp 4
	sw $fp 0($sp)
	subu $sp $sp 4

	# adjust $sp
	addu $fp $sp 8

	# low 
	# +-------------------+ <- $sp
	# |					  |
	# +-------------------+
	# |	 control link     |
	# +-------------------+ <- $fp
	# |  caller's $ip     |
	# +-------------------+
	# | 				  |
	#
	# high

	# reserver space for locals
	# subu $sp $sp 12

.data
main_L0: .asciiz "hello AR!\n"

.text
	la $t0 main_L0

	move $a0 $t0
	li $v0 4
	syscall

	# tear down the function
	lw $ra, 0($fp)
	move $t0 $fp
	lw $fp, -4($fp)
	move $sp $t0

	jr $ra


	