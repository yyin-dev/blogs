.text
.globl main
main:
  # unconditional jump
  li $t0, 0
  # j main

  # conditional jumps
  li $t1, 1
  beq $t0, $t1, main
  bne
  bgtz

  # If there's a previous jal (jump and set $ra), jr has the 
  # effect of function return.
  jr $ra

