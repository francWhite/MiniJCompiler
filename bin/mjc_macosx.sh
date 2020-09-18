#!/bin/bash

# if there are not at least two arguments: exit and show usage
if [ $# -lt 2 ]; then
  echo "usage: ./compile_macosx <input> <output>"
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
bin/nasm_macosx -f macho64 -o build/tmp/compile/file.o -dMACOS_X64 build/tmp/compile/file.asm
if [ ! $? -eq 0 ]; then
  exit 1
fi

# link including runtime
ld -macosx_version_min 10.12 -e _start -o ${2} build/tmp/compile/file.o build/runtime/objects/*.o -lC -no_pie
if [ ! $? -eq 0 ]; then
  exit 1
fi
