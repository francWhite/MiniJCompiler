; import the required external symbols for the system call functions
extern _read
extern _write
extern _exit

; export entry point
global _start

section .data
    TEXTHW: db 'Bitte geben Sie eine Zahl ein:', 13, 10	; defines the string constant TEXTHW with ‘hello world’&cr/lf)
                                    ; don’t add CR (10) in Linux
    TXTLEN: equ $-TEXTHW		    ; Reads the lenght of TEXTHW ino TXTLN

    LENGTH: equ 64                  ; constant (buffer length in bytes)

section .bss
    alignb 8
    BUFFER: resb LENGTH
    NUMBER: resb LENGTH

section .text

_start:
            push  rbp                   ; store pointer to previous frame, and additionally
                                        ; ensure that the stack is aligned for the subsequent
                                        ; function calls. Required for Windows and MacOS.

; implement divider (milestone 1)
mov rdi, TEXTHW     	; copy address of constant TEXTHW into register rdi
mov rsi, TXTLEN       ; writes the length of TEXTHW into register rsi
call _write         	; now calls function _write to write to console (stdout)

mov rdi, BUFFER; copy pointer to BUFFER into rdi
mov rsi, LENGTH; copy length of byte array into rsi
call _read; execute system call


; loop init
mov r12, 0  ;r12 = index
jmp convert_string_to_number_cond

convert_string_to_number_loop:
    mov r13, [BUFFER + r12]
    sub r13, 48 ;'0'
    mov [NUMBER + r12], r13
    add r12, 1

convert_string_to_number_cond:
    cmp r12, rax    ;check if max length is reached
    jl convert_string_to_number_loop

    mov rdi, NUMBER; copy pointer to BUFFER into rdi
    mov rsi, rax; copy number of bytes to output to rsi
    call _write; execute system call

; loop init
mov r12, rax  ;r12 = index
jmp addition_cond

addition_loop:


addition_cond:
    cmp r12, 0
    jg addition_loop

; loop init
mov r10, 1
mov r8, 5
;r8 = potenz (x von 10^x)
;r9 = index of loop
;r10 = start und resultat
pow_loop:
    mov al, 10
    mov bl, 10
    mul bl  ; the product is in ax
    mov r10, rax
    add r9, 1

pow_cond:
    cmp r9, r8
    jl pow_loop

mov rdi, r10; copy pointer to BUFFER into rdi
mov rsi, LENGTH; copy length of byte array into rsi
call _read; execute system call

; exit program with exit code 0
exit:       mov   rdi, 0                ; first parameter: set exit code
            call  _exit                 ; call function
