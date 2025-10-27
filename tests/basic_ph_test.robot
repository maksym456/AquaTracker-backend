*** Settings ***

*** Variables ***
${TEST_PH}    7.0

*** Test Cases ***
Verify Safe pH Level
    ${result}=    Evaluate Safe pH    ${TEST_PH}
    Should Be True    ${result}    msg=The pH ${TEST_PH} should be safe!

*** Keywords ***
Evaluate Safe pH
    [Arguments]    ${ph}
    ${is_safe}=    Run Keyword If    ${ph} >= 6.5 and ${ph} <= 7.5    Set Variable    ${True}    ELSE    Set Variable    ${False}
    [Return]    ${is_safe}