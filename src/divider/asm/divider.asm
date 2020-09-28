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
    BUFFER: resb LENGTH
    NUMBER: resb LENGTH
    NUMBER_2: resb LENGTH
    ;ISNEGATIVE: resb 1
    POW_PARAM: resb 8

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
mov rcx, 0  ; r12 = index von loop
jmp convert_string_to_number_cond

; BUFFER zeichen für zeichen iterieren und der string wird durch subtratkion von 48 (ascii code für '0') in eine zahl umgewandelt und in NUMBER gespeichert
convert_string_to_number_loop:
    ;cmp [BUFFER], '-'
    ;je set_negative_flag
    mov r11, [BUFFER + rcx]
    sub r11, 48 ; '0'
    mov [NUMBER + rcx], r11

    ;is_negative_return:
    add rcx, 1

convert_string_to_number_cond:
    cmp rcx, rax    ; check if max length is reached
    jl convert_string_to_number_loop
;--------------------WORKING ABOVE-------------------- Yes, indeed

;subtract index -1 if number is negative
;mov rax, [ISNEGATIVE]
;add rax, 1
sub rcx, 1;rax
mov r8, rcx

;reset register
mov rcx, 0  ;rcx = Potenz, von 0-n
mov rax, 0
mov rbx, 0
mov r11, 0  ;r11 = resultat

jmp combine_numbers_cond

combine_numbers_loop:
    mov [POW_PARAM], rcx ; 10^x, wobei x = r13
    call pow
    mov rbx, r10

     xor rdx, rdx
     mov dl, [NUMBER + r8]
     mov rax, rdx

    imul rax, rbx
    add r11, rax

        ;cmp rcx, 2
       ;je debug


    sub r8, 1
    add rcx, 1

combine_numbers_cond:
    cmp r8, 0
    jge combine_numbers_loop

 ;debug:
  ;      mov rdi, r11
  ;      call _exit

;division

;division finished
;debug

;debug

number_to_string:
    mov rcx, 0 ;index for number_to_string_div_loop
    mov r8, 10                 ;move 10 (dividend) to r12
    mov rax, r11 ;r11 auszugebender wert
    mov rdx, 0

    jmp number_to_string_div_cond

    number_to_string_div_loop:
        idiv r8                    ;rdx:rax / r12 -> rax=Resultat, rdx=rest
        mov [NUMBER_2 + rcx], rdx      ;move rdx(rest) to NUMBER
        mov rdx, 0
        add rcx, 1

    number_to_string_div_cond:
        cmp rax, 0
        jne number_to_string_div_loop

    sub rcx, 1
    mov r11, 0  ;r11 = index number_to_string_convert_loop
    jmp number_to_string_convert_cond

    number_to_string_convert_loop:
        mov rax, [NUMBER_2 + rcx]
        add rax, '0'
        mov [BUFFER + r11], rax
        add r11, 1
        sub rcx, 1

    number_to_string_convert_cond:
        cmp rcx, 0
        jge number_to_string_convert_loop

mov rdi, BUFFER; copy pointer to BUFFER into rdi
mov rsi, r11; copy length of byte array into rsi
call _write; execute system call

; exit program with exit code 0
exit:       mov   rdi, 0                ; first parameter: set exit code
            call  _exit                 ; call function


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
    cmp r9, qword [POW_PARAM] ;r8 ist die höchste potenz bzw. anzahl stellen-1
    jl pow_loop
    ret

;set_negative_flag:
;    mov [ISNEGATIVE], 1
;    jmp is_negative_return