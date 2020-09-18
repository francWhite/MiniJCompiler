@echo off
if "%~1" == "" goto show_usage
if "%~2" == "" goto show_usage

:check
if exist "build\tmp\compile" goto compile
mkdir build\tmp\compile

:compile
java -jar build\libs\MiniJCompiler.jar < %1 > build\tmp\compile\file.asm
if %errorlevel% neq 0 goto end

:assemble
bin\nasm_windows.exe -f win64 -o build\tmp\compile\file.obj -dWINDOWS_X64 build\tmp\compile\file.asm
echo %errorlevel%
if %errorlevel% neq 0 goto end

:link
bin\golink.exe /console /fo %2 /entry:_start kernel32.dll user32.dll build\tmp\compile\file.obj build\runtime\objects\*.obj
goto end

:show_usage
echo "usage: mjc_windows.bat <input> <output>"

:end
