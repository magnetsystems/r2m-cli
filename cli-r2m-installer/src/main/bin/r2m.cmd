@REM ----------------------------------------------------------------------------
@REM Magnet Start Up Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM MAGNET_TOOL_HOME - location of the Magnet Tools installation
@REM
@REM Optional ENV vars
@REM MAGNET_OPTS - parameters passed to the Java VM when running Magnet
@REM     e.g. to debug Maven itself, use
@REM set MAGNET_OPTS=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM MAGNET_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM MAGNET_BATCH_PAUSE - set to 'on' to wait for a key stroke before ending
@REM MYSQL_HOME - location of the MysQL installation for use when working with local persistence.
@REM ----------------------------------------------------------------------------
@echo off

set MAGNET_TOOL_VERSION=${project.version}
echo Starting ReST-to-Mobile %MAGNET_TOOL_VERSION%...
@REM Begin all REM lines with '@' in case MAGNET_BATCH_ECHO is 'on'

@REM enable echoing my setting MAGNET_BATCH_ECHO to 'on'
@if "%MAGNET_BATCH_ECHO%" == "on"  echo %MAGNET_BATCH_ECHO%

set ERROR_CODE=0

@REM set local scope for the variables
@setlocal

@REM ==== START VALIDATION - JUST FOR JAVA and MAB_TOOLS ====
if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo ERROR: JAVA_HOME not found in your environment.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto MagnetTools

echo.
echo ERROR: JAVA_HOME is set to an invalid directory.
echo JAVA_HOME = "%JAVA_HOME%"
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:MagnetTools
if not "%MAGNET_TOOL_HOME%" == "" goto OkMagnetTools 

@REM Try to auto set the tool dir relative to this executing script
set MAGNET_TOOL_HOME=%~dp0
set MAGNET_TOOL_HOME=%MAGNET_TOOL_HOME:~0,-1%\..

set MAGNET_LIB_DIR=%MAGNET_TOOL_HOME%\lib
set MAGNET_CONFIG_DIR=%MAGNET_TOOL_HOME%\config
if exist "%MAGNET_LIB_DIR%" goto init
if exist "%MAGNET_CONFIG_DIR%" goto init

echo.
echo ERROR: MAGNET_TOOL_HOME not found in your environment.
echo Please set the MAGNET_TOOL_HOME variable in your environment to match the
echo location of your Magnet Tools installation
echo.
goto error

:OkMagnetTools 
set MAGNET_LIB_DIR=%MAGNET_TOOL_HOME%\lib
set MAGNET_CONFIG_DIR=%MAGNET_TOOL_HOME%\config
if exist "%MAGNET_LIB_DIR%" goto init
if exist "%MAGNET_CONFIG_DIR%" goto init

echo.
echo ERROR: MAGNET_TOOL_HOME is set to an invalid directory.
echo MAGNET_TOOL_HOME = "%MAGNET_TOOL_HOME%"
echo Please set the MAGNET_TOOL_HOME variable in your environment to match the
echo location of your Magnet Tools installation
echo.
goto error

@REM ==== END VALIDATION ====
:init
set CLASSPATH="%MAGNET_CONFIG_DIR%";"%MAGNET_LIB_DIR%\*";
if exist "%MAB_EXTRA_CLASSPATH%" set CLASSPATH=%MAB_EXTRA_CLASSPATH%;%CLASSPATH%

SET MAGNET_CMD_LINE_ARGS=%*
SET MAGNET_JAVA_EXE="%JAVA_HOME%\bin\java.exe"

@REM Start Magnet Tools

%MAGNET_JAVA_EXE% %MAGNET_OPTS% -classpath %CLASSPATH% com.magnet.tools.cli.core.Main %MAGNET_CMD_LINE_ARGS%

if ERRORLEVEL 1 goto error
goto end

:error
@endlocal
set ERROR_CODE=1

:end
@REM set local scope for the variables with windows NT shell
@endlocal & set ERROR_CODE=%ERROR_CODE%

@REM pause the batch file if MAGNET_BATCH_PAUSE is set to 'on'
if "%MAGNET_BATCH_PAUSE%" == "on" pause

cmd /C exit /B %ERROR_CODE%
