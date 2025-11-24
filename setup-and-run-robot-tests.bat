@echo off
setlocal EnableDelayedExpansion
cd /d "%~dp0"

set "GREEN=[92m"
set "RED=[91m"
set "YELLOW=[93m"
set "CYAN=[96m"
set "RESET=[0m"

echo %CYAN%===================================================%
echo       AquaTracker Backend - One-Click Test Setup      %
echo %RESET%
echo This script will install everything needed to run%
echo the Robot Framework tests on Windows - even from scratch%
echo %CYAN%===================================================% %RESET%
echo.

set PYTHON_CMD=
python --version >nul 2>&1
if !errorlevel! == 0 (
    for /f "tokens=2" %%v in ('python --version 2^>nul') do set "PYVER=%%v"
    set "PYMAJOR=!PYVER:~0,1!"
    set "PYMINOR=!PYVER:~2,2!"
    if !PYMAJOR! geq 3 (
        if !PYMINOR! geq 11 (
            set "PYTHON_CMD=python"
            echo %GREEN%[OK] Python !PYVER! already installed%RESET%
            goto :python_ok
        )
    )
)

py --version >nul 2>&1
if !errorlevel! == 0 (
    for /f "tokens=2" %%v in ('py --version 2^>nul') do set "PYVER=%%v"
    set "PYMAJOR=!PYVER:~0,1!"
    set "PYMINOR=!PYVER:~2,2!"
    if !PYMAJOR! geq 3 (
        if !PYMINOR! geq 11 (
            set "PYTHON_CMD=py -3"
            echo %GREEN%[OK] Python !PYVER! available via 'py' launcher%RESET%
            goto :python_ok
        )
    )
)

echo %YELLOW%[INFO] Python 3.11+ not found. Installing Python 3.12...%RESET%

winget --version >nul 2>&1
if !errorlevel! == 0 (
    echo %YELLOW%[INFO] Using winget to install Python...%RESET%
    winget install Python.Python.3.12 -e --silent --accept-package-agreements --accept-source-agreements --force
    if !errorlevel! == 0 (
        echo %GREEN%[OK] Python installed successfully via winget%RESET%
        :: Refresh PATH
        set "PATH=!PATH!;%ProgramFiles%\Python312;%ProgramFiles%\Python312\Scripts"
        set "PYTHON_CMD=python"
        goto :python_ok
    ) else (
        echo %RED%[ERROR] winget failed, falling back to direct download...%RESET%
    )
) else (
    echo %YELLOW%[INFO] winget not available, downloading installer...%RESET%
)

set "PYTHON_URL=https://www.python.org/ftp/python/3.12.7/python-3.12.7-amd64.exe"
set "INSTALLER=%TEMP%\python-3.12.7-amd64.exe"
echo %YELLOW%[INFO] Downloading Python 3.12.7 installer...%RESET%
powershell -Command "Invoke-WebRequest -Uri '%PYTHON_URL%' -OutFile '%INSTALLER%' -UseBasicParsing"
if not exist "%INSTALLER%" (
    echo %RED%[ERROR] Failed to download Python installer%RESET%
    pause
    exit /b 1
)

echo %YELLOW%[INFO] Installing Python silently (this may take 1-2 minutes)...%RESET%
"%INSTALLER%" /quiet InstallAllUsers=1 PrependPath=1 Include_launcher=1 Include_test=0
if !errorlevel! neq 0 (
    echo %RED%[ERROR] Python installation failed%RESET%
    pause
    exit /b 1
)

del "%INSTALLER%" 2>nul
call RefreshEnv.cmd >nul 2>&1
set "PATH=!PATH!;%ProgramFiles%\Python312;%ProgramFiles%\Python312\Scripts"
set "PYTHON_CMD=python"
:python_ok
echo.

echo %YELLOW%[INFO] Upgrading pip...%RESET%
%PYTHON_CMD% -m pip install --upgrade pip
if !errorlevel! neq 0 (
    echo %RED%[ERROR] Failed to upgrade pip%RESET%
    pause
    exit /b 1
)
echo %GREEN%[OK] pip upgraded%RESET%
echo.

if not exist ".venv" (
    echo %YELLOW%[INFO] Creating virtual environment (.venv)...%RESET%
    %PYTHON_CMD% -m venv .venv
    if !errorlevel! neq 0 (
        echo %RED%[ERROR] Failed to create virtual environment%RESET%
        pause
        exit /b 1
    )
)

call .venv\Scripts\activate.bat
if !errorlevel! neq 0 (
    echo %RED%[ERROR] Failed to activate virtual environment%RESET%
    pause
    exit /b 1
)
echo %GREEN%[OK] Virtual environment activated%RESET%
echo.

if exist "requirements.txt" (
    echo %YELLOW%[INFO] Found requirements.txt - installing from it...%RESET%
    pip install -r requirements.txt
    if !errorlevel! neq 0 (
        echo %RED%[ERROR] Failed to install requirements.txt%RESET%
        pause
        exit /b 1
    )
) else (
    echo %YELLOW%[INFO] Installing standard Robot Framework stack...%RESET%
    pip install robotframework robotframework-requests robotframework-seleniumlibrary robotframework-databaselibrary robotframework-jsonlibrary
    if !errorlevel! neq 0 (
        echo %RED%[ERROR] Failed to install Robot Framework libraries%RESET%
        pause
        exit /b 1
    )
)

echo %YELLOW%[INFO] Installing webdrivermanager...%RESET%
pip install webdrivermanager
if !errorlevel! neq 0 (
    echo %RED%[ERROR] Failed to install webdrivermanager%RESET%
    pause
    exit /b 1
)

echo %YELLOW%[INFO] Downloading and setting up ChromeDriver...%RESET%
webdrivermanager chrome
if !errorlevel! neq 0 (
    echo %YELLOW%[WARN] webdrivermanager failed - you may need to install ChromeDriver manually if tests use Selenium%RESET%
) else (
    echo %GREEN%[OK] ChromeDriver is ready%RESET%
)
echo.

set "TEST_DIR="
if exist "tests\" set "TEST_DIR=tests"
if exist "robot_tests\" set "TEST_DIR=robot_tests"
if exist "test\" set "TEST_DIR=test"
if "!TEST_DIR!"=="" set "TEST_DIR=."

echo %GREEN%===================================================%
echo           SETUP COMPLETE!                              %
echo %RESET%
echo You can now run the Robot Framework tests with one of these commands:
echo.
if "!TEST_DIR!"=="tests" (
    echo   robot tests
    echo   robot -d results tests
) else if "!TEST_DIR!"=="robot_tests" (
    echo   robot robot_tests
    echo   robot -d results robot_tests
) else (
    echo   robot "!TEST_DIR!"
    echo   robot -d results "!TEST_DIR!"
)
echo.
echo %CYAN%Tip: Just open a new terminal and run the command above.%RESET%
echo %CYAN%     Or run this batch file again - it will skip installation steps.%RESET%
echo.
echo %GREEN%Happy testing!%RESET%
echo.

pause
exit /b 0