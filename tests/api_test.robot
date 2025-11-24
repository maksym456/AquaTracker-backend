*** Settings ***
Library         RequestsLibrary
Library         Collections
Library         OperatingSystem

Suite Setup     Create Session    api    http://localhost:8080

*** Variables ***
${VALID_TOKEN}      supersekretnytoken
${INVALID_TOKEN}    wrongtoken123
${API_ENDPOINT}     /fish

*** Test Cases ***
Get Fish List With Valid Token
    [Tags]    smoke    positive
    ${headers}=    Create Dictionary    X-API-TOKEN=${VALID_TOKEN}
    ${response}=   GET On Session    api    ${API_ENDPOINT}    headers=${headers}    expected_status=200

    Should Be Equal As Strings    ${response.status_code}    200
    Dictionary Should Contain Key    ${response.json()}    rybki
    ${fish_list}=    Set Variable    ${response.json()['rybki']}
    Should Be True    isinstance(${fish_list}, list)
    Length Should Be    ${fish_list}    4
    List Should Contain Value    ${fish_list}    Gupik
    List Should Contain Value    ${fish_list}    Złota rybka
    List Should Contain Value    ${fish_list}    Molinezja
    List Should Contain Value    ${fish_list}    Neon Innesa

Access Fish Endpoint Without Token
    [Tags]    negative
    ${response}=   GET On Session    api    ${API_ENDPOINT}    expected_status=401
    Should Be Equal As Strings    ${response.status_code}    401
    Should Be Equal    ${response.json()['error']}    Brak poprawnego tokenu API

Access Fish Endpoint With Invalid Token
    [Tags]    negative
    ${headers}=    Create Dictionary    X-API-TOKEN=${INVALID_TOKEN}
    ${response}=   GET On Session    api    ${API_ENDPOINT}    headers=${headers}    expected_status=401
    Should Be Equal As Strings    ${response.status_code}    401
    Should Be Equal    ${response.json()['error']}    Brak poprawnego tokenu API