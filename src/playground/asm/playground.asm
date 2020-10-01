DEFAULT REL		; defines relative addresses

; import the required external symbols / system calls
extern _read
extern _write
extern _exit

; export entry point
global _start		; exports public label _start

    LENGTH EQU 64      ; definition constant LENGTH (buffer length)

section .bss		; defines start of section of unitialized data
    alignb 8            ; align to 8 bytes (for 64-bit machine)
    BUFFER resb LENGTH  ; buffer (128 bytes)
    NUMBER resb LENGTH  ; buffer (128 bytes)

section .text		; defines start of code section

_start:			; start label
   ;mov r15, 48
   ;mov r10, r15
   jmp pow


_simple_multiplication:
    mov rax, 2
    mov rbx, 4
    imul rax, rbx

    add rax, '0'
    mov [BUFFER], rax

    mov rdi, BUFFER     ; copy address of variable BUFFER into register rdi
    mov rsi, rax        ; register rax contians the number of typed char, copy value of rax into register rsi
    call _write         ; now calls function _write to write to console (stdin)

    jmp _end


pow:
    mov r8, 6
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
    cmp r9, r8
    jl pow_loop

number_to_string:
    mov r9, 0 ;inde for number_to_string_div_loop
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


;test:
;    add r13, '0'
;    mov [BUFFER], r13

    mov rdi, BUFFER     ; copy address of variable BUFFER into register rdi
    mov rsi, r11        ; register rax contians the number of typed char, copy value of rax into register rsi
    call _write         ; now calls function _write to write to console (stdin)

_end:
    mov   rdi, 0        ; terminate program with exit 0
    call  _exit




    ;DEBUGER
        mov r14, 0; WERT
        add r14, '0'
        mov [BUFFER], r14
        mov rdi, BUFFER     ; copy address of variable BUFFER into register rdi
        mov rsi, 1        ; register rax contians the number of typed char, copy value of rax into register rsi
        call _write