@echo off
setlocal

set "script_dir=%~dp0"
set AGENT_APPLICATION=%script_dir%.."

call "%script_dir%support\agent.bat"

endlocal