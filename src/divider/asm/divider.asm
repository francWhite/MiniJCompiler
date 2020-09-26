; import the required external symbols for the system call functions
extern _read
extern _write
extern _exit

; export entry point
global _start

section .data
    TEXTHW db 'Bitte geben Sie eine Zahl ein:', 13, 10	; defines the string constant TEXTHW with 13=cr and 10=lf
    TXTLEN equ $-TEXTHW		                        ; Reads the length of TEXTHW ino TXTLN
    LENGTH equ 64                                      ; constant (buffer length in bytes)

section .bss
    alignb 8
    BUFFER resb LENGTH
    NUMBER resb LENGTH

section .text

_start:
    push  rbp   ; store pointer to previous frame, and additionally
                ; ensure that the stack is aligned for the subsequent
                ; function calls. Required for Windows and MacOS.

; implement divider (milestone 1)
mov rdi, TEXTHW     	; adresse von konstante TEXTHW in das register rdi laden
mov rsi, TXTLEN         ; länge von konstante TEXTHW in das register rsi laden
call _write         	; now calls function _write to write to console (stdout)

mov rdi, BUFFER ; copy pointer to BUFFER into rdi
mov rsi, LENGTH ; copy length of byte array into rsi
call _read      ; execute system call --> BUFFER contains the input, rax contains the input length

sub rax, 2  ; Enter = \r\n -> subtract 2

; eingabe zu zahl umwandeln
mov r12, 0  ; r12 = index von loop
jmp convert_string_to_number_cond

; BUFFER zeichen für zeichen iterieren und der string wird durch subtratkion von 48 (ascii code für '0') in eine zahl umgewandelt und in NUMBER gespeichert
convert_string_to_number_loop:
    mov r13, [BUFFER + r12]
    sub r13, 48 ; '0'
    mov [NUMBER + r12], r13
    add r12, 1

convert_string_to_number_cond:
    cmp r12, rax    ; check if max length is reached
    jl convert_string_to_number_loop
;--------------------WORKING ABOVE--------------------
sub r12, 1
mov r13, 0  ;r13 = Potenz, von 0-n
mov r15, 0  ;r15 = resultat
jmp combine_numbers_cond

combine_numbers_loop:
    mov r8, r13 ; 10^x, wobei x = r13
    jmp pow
pow_return:
    mov rax, [NUMBER + r12]
    mov rbx, r10
    imul rax, rbx
    add r15, rax

    sub r12, 1
    add r13, 1

combine_numbers_cond:
    cmp r12, 0
    jge combine_numbers_loop


;division

;division finished

mov r10, r15
jmp number_to_string
jmp _exit

pow:
    mov r9, 0
    mov r10, 1
    jmp pow_cond
;r8 = potenz (x von 10^x)
;r9 = index of loop
;r10 = start und resultat
pow_loop:
    mov rax, 10
    mov rbx, r10
    imul rax, rbx  ; the product is in rax
    mov r10, rax
    add r9, 1

pow_cond:
    cmp r9, r8 ;r8 ist die höchste potenz bzw. anzahl stellen-1
    jl pow_loop
    jmp pow_return

number_to_string:
    mov r9, 0 ;index for number_to_string_div_loop
    mov r12, 10                 ;move 10 (dividend) to r12
    mov rax, r10 ;r10 auszugebender wert
    mov rdx, 0
    jmp number_to_string_div_cond

    number_to_string_div_loop:
        idiv r12                    ;rdx:rax / r12 -> rax=Resultat, rdx=rest
        mov [NUMBER + r9], rdx      ;move rdx(rest) to NUMBER
        mov rdx, 0
        add r9, 1

    number_to_string_div_cond:
        cmp rax, 0
        jne number_to_string_div_loop

    sub r9, 1
    mov r11, 0  ;r11 = index number_to_string_convert_loop
    jmp number_to_string_convert_cond

    number_to_string_convert_loop:
        mov rax, [NUMBER + r9]
        add rax, '0'
        mov [BUFFER + r11], rax
        add r11, 1
        sub r9, 1

    number_to_string_convert_cond:
        cmp r9, 0
        jge number_to_string_convert_loop

mov rdi, BUFFER; copy pointer to BUFFER into rdi
mov rsi, r11; copy length of byte array into rsi
call _write; execute system call

; exit program with exit code 0
exit:       mov   rdi, 0                ; first parameter: set exit code
            call  _exit                 ; call function
