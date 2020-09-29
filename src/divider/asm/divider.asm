; import the required external symbols for the system call functions
extern _read
extern _write
extern _exit

; export entry point
global _start

section .data
    LENGTH equ 64                                       ; constant (buffer length in bytes) 64 bytes

    TEXTHW db 'Bitte geben Sie eine Zahl ein:', 13, 10	; defines the string constant TEXTHW with 13=cr and 10=lf
    TXTLEN equ $-TEXTHW		                            ; Reads the length of TEXTHW ino TXTLN

    OUTPUTTEXT db 'Das Resultat ist: '
    OUTPUTLEN equ $-OUTPUTTEXT

    CRLF        db  10, 13  ; carriage return (CR) / line feed (LF)
    CRLF_LEN    equ $-CRLF  ; current position ($) minus address of CRLF => 2 bytes

section .bss
    alignb 8
    BUFFER: resb LENGTH
    NUMBER: resb LENGTH
    ;ISNEGATIVE: resb 1
    POW_PARAM: resb 8

section .text
_start:
push  rbp               ; store pointer to previous frame, and additionally
                        ; ensure that the stack is aligned for the subsequent
                        ; function calls. Required for Windows and MacOS.


mov rdi, TEXTHW     	; adresse von konstante TEXTHW in das register rdi laden
mov rsi, TXTLEN         ; länge von konstante TEXTHW in das register rsi laden
call _write         	; now calls function _write to write to console (stdout)

mov rdi, BUFFER         ; copy pointer to BUFFER into rdi
mov rsi, LENGTH         ; copy length of byte array into rsi
call _read              ; execute system call --> BUFFER contains the input, rax contains the input length

sub rax, 1  ; Enter = \r\n -> subtract 2

;----------Eingabe zu zahl umwandeln-----------
mov rcx, 0              ; rcx = index von loop
jmp convert_string_to_number_cond

;----------BUFFER zeichen für zeichen iterieren und der string wird durch subtratkion von 48 (ascii code für '0') in eine zahl umgewandelt und in NUMBER gespeichert---------
convert_string_to_number_loop:
    ;cmp [BUFFER], '-'
    ;je set_negative_flag

    xor rdx, rdx
    mov dl, [BUFFER + rcx]
    mov r11, rdx

    cmp r11, 48
    jge greater_than_48     ;wenn wert >= 48 ist, handelt es sich möglicherweise um eine Zahl
    jl combine_numbers      ;wenn wert < 48 ist, handelt es sich um keine Zahl mehr

    greater_than_48:
    cmp r11, 58
    jg  combine_numbers     ;wenn wert > 58 ist, handelt es sich um keine Zahl mehr

    sub r11, 48
    mov [NUMBER + rcx], r11

    ;is_negative_return:
    inc rcx

convert_string_to_number_cond:
    cmp rcx, rax        ;check if max length is reached
    jl convert_string_to_number_loop

;subtract index -1 if number is negative
;mov rax, [ISNEGATIVE]
;add rax, 1
combine_numbers:
    sub rcx, 1
    mov r8, rcx
    ;-----reset register-----
    mov rcx, 0              ;rcx = Potenz, von 0-n
    mov rax, 0              ;1. Faktor
    mov rbx, 0              ;2. Faktor
    mov r11, 0              ;r11 = resultat
    jmp combine_numbers_cond

combine_numbers_loop:
    mov [POW_PARAM], rcx        ;parameter für pow-func 10^x, wobei x = rcx
    call pow

    mov rbx, r10
    xor rdx, rdx
    mov dl, [NUMBER + r8]
    mov rax, rdx

    imul rax, rbx
    add r11, rax

    dec r8
    inc rcx

combine_numbers_cond:
    cmp r8, 0
    jge combine_numbers_loop
;--------division--------
mov rdx, 0
mov rax, r11
mov r8, 2
idiv r8
mov r11, rax
;------division finished-----

;-------number_to_string------
mov rcx, 0              ;index for number_to_string_div_loop
mov r8, 10              ;move 10 (dividend) to r12
mov rax, r11            ;r11 auszugebender wert
mov rdx, 0

number_to_string_div_loop:
    idiv r8                      ;rdx:rax / r8 -> rax=Resultat, rdx=rest
    mov [NUMBER + rcx], rdx      ;move rdx (rest) to NUMBER
    mov rdx, 0
    inc rcx

number_to_string_div_cond:
    cmp rax, 0
    jne number_to_string_div_loop
    sub rcx, 1

mov r11, 0              ;r11 = index number_to_string_convert_loop
jmp number_to_string_convert_cond

number_to_string_convert_loop:
    mov rax, [NUMBER + rcx]
    add rax, '0'
    mov [BUFFER + r11], rax
    inc r11
    dec rcx

number_to_string_convert_cond:
    cmp rcx, 0
    jge number_to_string_convert_loop

mov rdi, OUTPUTTEXT     ;copy pointer to OUTPUTTEXT into rdi
mov rsi, OUTPUTLEN      ; copy length of byte array into rsi
call _write             ; execute system call

mov rdi, BUFFER         ; copy pointer to BUFFER into rdi
mov rsi, r11            ; copy length of byte array into rsi
call _write             ; execute system call

mov rdi, CRLF           ; copy address of variable CRLF into register rdi
mov rsi, CRLF_LEN       ; length of output: 3 (number + CRLF)
call _write             ; now calls function _write to write to console (stdin)


exit:                    ; exit program with exit code 0
    mov   rdi, 0         ; first parameter: set exit code
    call  _exit          ; call function


;-------pow: rechnet 10^x-------
;r8 = potenz (x von 10^x)
;r9 = index of loop
;r10 = start und resultat
pow:
    mov r9, 0
    mov r10, 1
    jmp pow_cond

pow_loop:
    mov rax, 10
    mov rbx, r10
    imul rax, rbx               ; the product is in rax
    mov r10, rax
    inc r9

pow_cond:
    cmp r9, qword [POW_PARAM]   ;r9 ist die höchste potenz bzw. anzahl stellen-1
    jl pow_loop
    ret

;set_negative_flag:
;    mov [ISNEGATIVE], 1
;    jmp is_negative_return