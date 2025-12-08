*** Settings ***
Library    RequestsLibrary
Library    Collections

*** Variables ***
${API_BASE_URL}     http://127.0.0.1:8000
${ENDPOINT}         /is_ph_safe/

# Dane testowe
${SAFE_PH}          7.0
${UNSAFE_PH}        5.0


*** Test Cases ***
Scenariusz 1: Weryfikacja bezpiecznego poziomu pH
    [Documentation]    
    Sprawdz pH i Oczekuj Bezpiecznego  ${SAFE_PH}  ${True}

Scenariusz 2: Weryfikacja niebezpiecznego poziomu pH
    [Documentation]    
    Sprawdz pH i Oczekuj Bezpiecznego  ${UNSAFE_PH}  ${False}

Scenariusz 3: Weryfikacja granicznej wartości bezpiecznej (dolnej)
    [Documentation]    
    Sprawdz pH i Oczekuj Bezpiecznego  6.5  ${True}

Scenariusz 4: Weryfikacja granicznej wartości niebezpiecznej (poniżej dolnej)
    [Documentation]    
    Sprawdz pH i Oczekuj Bezpiecznego  6.4  ${False}


*** Keywords ***
Sprawdz pH i Oczekuj Bezpiecznego
    [Arguments]    ${ph_value}    ${expected_safety}
    
    Create Session    ph_session    ${API_BASE_URL}
    
    ${response}=    GET On Session    ph_session    ${ENDPOINT}    params=ph=${ph_value}
    
    Should Be Equal As Integers    ${response.status_code}    200    
    ...    msg=Oczekiwano statusu HTTP 200, otrzymano ${response.status_code}.
    
    ${json_body}=    Set Variable    ${response.json()}
    
    ${actual_safety}=    Get From Dictionary    ${json_body}    is_safe
    
    ${expected_safety_str}=    Convert To String    ${expected_safety}
    ${actual_safety_str}=    Convert To String    ${actual_safety}

    Should Be Equal    ${actual_safety_str}    ${expected_safety_str}    
    ...    msg=Dla pH ${ph_value} oczekiwano 'is_safe'=${expected_safety}, otrzymano ${actual_safety}.
    
    Log To Console    \nTest dla pH: ${ph_value}, Odpowiedź: ${json_body}