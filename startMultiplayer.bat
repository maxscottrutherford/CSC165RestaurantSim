@echo off
setlocal enabledelayedexpansion

for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /c:"IPv4 Address"') do (
    set ip=%%a
    set ip=!ip:~1!
    echo !ip! > ipconfig.txt
    goto :done
)

:done
