Pseudo-instruction: implemented using other instruction. Common in MIPS to reduce the number of instructions.

`jal` means jump-and-link. So `jal 0x0` set $ip to `0x0`, and puts the original $ip into $ra. Later when you do `jar`, it would put the value in $ra into $ip.