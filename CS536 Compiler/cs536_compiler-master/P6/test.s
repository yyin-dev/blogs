		.text
		.globl main
main:
__start:
		sw    $ra, 0($sp)	# PUSH
		subu  $sp, $sp, 4
		sw    $fp, 0($sp)	# PUSH
		subu  $sp, $sp, 4
		addu  $fp, $sp, 8	# Set FP
		subu  $sp, $sp, 0	# Reserve space for locals
		.data
.L0:	.asciiz "hello world!"
		.text
		la    $t0, .L0
		sw    $t0, 0($sp)	# PUSH
		subu  $sp, $sp, 4
		lw    $a0, 4($sp)	# POP
		addu  $sp, $sp, 4
		li    $v0, 4
		syscall
_main_exit:			# function epilogue
		lw    $ra, 0($fp)
		move  $t0, $fp
		lw    $fp, -4($fp)
		move  $sp, $t0
		li    $v0, 10
		syscall
