*** Settings ***
Library    RequestsLibrary

*** Variables ***
${API_URL}    http://localhost:8080

*** Test Cases ***
Simple Connection Test - Backend is Running
    Create Session    localapi    ${API_URL}    disable_warnings=1
    ${resp}=    GET On Session    localapi    /anything-i-want    expected_status=any
    Log To Console    \nCONNECTED SUCCESSFULLY! Server responded with status: ${resp.status_code}
    Log To Console    If you see 404 or 401 here → everything is perfect, server is alive