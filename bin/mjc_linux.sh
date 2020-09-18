#!/bin/bash

# if there are not at least two arguments: exit and show usage
if [ $# -lt 2 ]; then
  echo "usage: ./compile_linux <input> <output>"
  exit 1;
fi

# create temporary directory if necessary
mkdir -p build/tmp/compile

# compile MiniJ
java -jar build/libs/MiniJCompiler.jar < ${1} > build/tmp/compile/file.asm
if [ ! $? -eq 0 ]; then
  exit 1
fi

# assemble
bin/nasm_linux -f elf64 -o build/tmp/compile/file.o -dLINUX_X64 build/tmp/compile/file.asm
if [ ! $? -eq 0 ]; then
  exit 1
fi

# link including runtime
ld -o ${2} build/tmp/compile/file.o build/runtime/objects/*.o
if [ ! $? -eq 0 ]; then
  exit 1
fi
