; Externe Bibliotheken importieren
extern _read
extern _write
extern _exit

; Startpunkt definieren
global _start

section .data
    TEXTHW db 'Bitte geben Sie eine Zahl ein:', 13, 10	; Stringkonstante mit 13=CR and 10=LF
    TXTLEN equ $-TEXTHW		                            ; Schreibt die Länge von TEXTHW in TXTLEN
    LENGTH equ 64                                       ; Konstante (Bufferlänge in Bytes)

section .bss
    alignb 8
    BUFFER resb LENGTH
    NUMBER resb LENGTH

section .text

_start:
push  rbp                           ; store pointer to previous frame, and additionally
                                    ; ensure that the stack is aligned for the subsequent
                                    ; function calls. Required for Windows and MacOS.

; Die Stringkonstante TEXTHW ausgeben
mov rdi, TEXTHW                     ; Die Adresse der Konstante TEXTHW in das Register rdi laden
mov rsi, TXTLEN                     ; Die Länge der Konstante TEXTHW in das Register rsi laden
call _write         	            ; Schreibt n Zeichen beginnend von der Adresse, welche in rdi gespeichert ist, wobei n TXTLEN

; Benutzereingabe lesen und in BUFFER speichern
mov rdi, BUFFER                     ; copy pointer to BUFFER into rdi
mov rsi, LENGTH                     ; copy length of byte array into rsi
call _read                          ; execute system call --> BUFFER contains the input, rax contains the input length
; Ausgabe: BUFFER enthält den Text und rax enthält die Textlänge

sub rax, 2                          ; 2 von der Länge subtrahieren, da die Eingabetaste 2 weitere Zeichen darstellt (\r\n)

; Die einzelnen Zeichen der Eingabe in einzelne Zahlen umwandeln
mov r8, 0  ; r8 = Index von convert_string_to_number_loop
jmp convert_string_to_number_cond

; BUFFER Zeichen für Zeichen iterieren und der Char wird durch Subtraktion von 48 (ASCII Code für '0') in eine Zahl umgewandelt und in NUMBER gespeichert
; r8 = Index von convert_string_to_number_loop
; r9 = Temporäre Variable zum Zwischenspeichern des Wertes
convert_string_to_number_loop:
    mov r9, [BUFFER + r8]           ; Inhalt an der Adresse von BUFFER+r8 nach r9 kopieren
    sub r9, 48 ; '0'                ; 48 vom Eingabewert subtrahieren, um den tatsächlichen Wert als Zahl zu erhalten
    mov [NUMBER + r8], r9           ; Die Zahl von r9 an die Adresse [NUMBER+r8] kopieren
    add r8, 1                       ; Index inkrementieren

; r8 = Index von convert_string_to_number_loop
; rax = Anzahl Zeichen von der Eingabe
convert_string_to_number_cond:
    cmp r8, rax                     ; Solange wiederholen, bis sämtliche Zeichen bearbeitet wurden
    jl convert_string_to_number_loop
    sub r8, 1                       ; Die letzte unnötige Inkrementierung rückgangig machen
; Ausgabe: An den Adressen [NUMBER+n] sind die jeweiligen Zahlenwerte gespeichert, in r8 steht die Anzahl Zahlen-1 (Wird benötigt für Rückwärtsiteration)

;--------------------WORKING ABOVE
                                    ; r8 = Rückwärtsiterator und gleichzeitig Potenz
mov r9, 0                           ; r9 = Index von combine_numbers_loop
jmp combine_numbers_cond

; Durch jede Zahl in NUMBER iterieren und mit der entsprechenden Potenz multiplizieren
combine_numbers_loop:
    mov r15, r8; WERT
    ;add r15, '0'
    ;mov [BUFFER], r15
    ;mov rdi, BUFFER     ; copy address of variable BUFFER into register rdi
    ;mov rsi, 1        ; register rax contians the number of typed char, copy value of rax into register rsi
    ;call _write
    ;mov r15, [NUMBER + r9]; WERT
    ;add r15, '0'
    ;mov [BUFFER], r15
    ;mov rdi, BUFFER     ; copy address of variable BUFFER into register rdi
    ;mov rsi, 1        ; register rax contians the number of typed char, copy value of rax into register rsi
    ;call _write
    jmp pow_init
pow_return:
        mov r15, r12; WERT
        add r15, '0'
        mov [BUFFER], r15
        mov rdi, BUFFER     ; copy address of variable BUFFER into register rdi
        mov rsi, r8        ; register rax contians the number of typed char, copy value of rax into register rsi
        call _write
    sub r8, 1
    add r9, 1

combine_numbers_cond:
    cmp r8, 0
    jge combine_numbers_loop

call _exit
;r10 = Potenz, x von 10^x
;r11 = Index für pow_loop
;r12 = Startwert und Resultat
pow_init:
    mov r11, 0
    mov r12, 1

pow_loop:
    mov rax, r12
    mov rbx, 10
    imul rax, rbx
    mov r12, rax
    add r11, 1

pow_cond:
    cmp r11, r8
    jle pow_loop
    jmp pow_return

; division

; convert numbers to string

; output string

;
call _exit