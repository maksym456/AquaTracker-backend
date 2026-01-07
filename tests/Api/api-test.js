const BASE_URL = "http://localhost:3001/api/v1";

const COGNITO_CLIENT_ID = process.env.COGNITO_CLIENT_ID;
const TEST_USER_EMAIL = process.env.TEST_USER_EMAIL;
const TEST_USER_PASSWORD = process.env.TEST_USER_PASSWORD;
const AWS_REGION = process.env.AWS_REGION || "eu-north-1";
const TEST_USER_ID = process.env.TEST_USER_ID;  

let JWT = "";

async function loginToCognito() {
    if (!COGNITO_CLIENT_ID || !TEST_USER_EMAIL || !TEST_USER_PASSWORD) {
        console.log(" Brak danych do logowania – ustaw COGNITO_CLIENT_ID, TEST_USER_EMAIL, TEST_USER_PASSWORD");
        process.exit(1);
    }

    const params = new URLSearchParams();
    params.append('grant_type', 'password');
    params.append('client_id', COGNITO_CLIENT_ID);
    params.append('username', TEST_USER_EMAIL);
    params.append('password', TEST_USER_PASSWORD);

    try {
        const response = await fetch(`https://cognito-idp.${AWS_REGION}.amazonaws.com/`, {
            method: 'POST',
            headers: {
                'X-Amz-Target': 'AWSCognitoIdentityProviderService.InitiateAuth',
                'Content-Type': 'application/x-amz-json-1.1'
            },
            body: JSON.stringify({
                AuthFlow: 'USER_PASSWORD_AUTH',
                ClientId: COGNITO_CLIENT_ID,
                AuthParameters: {
                    USERNAME: TEST_USER_EMAIL,
                    PASSWORD: TEST_USER_PASSWORD
                }
            })
        });

        const data = await response.json();
        if (data.AuthenticationResult) {
            JWT = data.AuthenticationResult.IdToken;
            console.log(" Zalogowano do Cognito – token pobrany");
            return true;
        } else {
            console.log(" Błąd logowania:", data);
            return false;
        }
    } catch (error) {
        console.log(" Błąd połączenia z Cognito:", error.message);
        return false;
    }
}

async function checkEndpoint(name, path, isFullUrl = false) {
    const url = isFullUrl ? path : `${BASE_URL}${path}`;
    const headers = {
        "Accept": "application/json",
        Authorization: `Bearer ${JWT}`
    };

    try {
        const response = await fetch(url, { headers });
        if (response.status === 200) {
            console.log(` ${name} – działa (200 OK)`);
        } else {
            console.log(` ${name} – błąd: ${response.status} ${response.statusText}`);
        }
    } catch (error) {
        console.log(` ${name} – błąd połączenia: ${error.message}`);
    }
}

async function runTests() {
    console.log("Uruchamiam testy API\n");
    await checkEndpoint("Dokumentacja OpenAPI", "http://localhost:3001/v3/api-docs", true); 
    if (!(await loginToCognito())) return;
    if (!TEST_USER_ID) {
        console.log(" Brak TEST_USER_ID – ustaw w env");
        return;
    }
    
    await checkEndpoint("Lista ryb (fish)", "/fish");
    await checkEndpoint("Lista roślin (plants)", "/plants");
    await checkEndpoint("Lista moich akwariów", `/aquariums/${TEST_USER_ID}`);
    await checkEndpoint("Lista logów", `/logs/${TEST_USER_ID}`);
    await checkEndpoint("Kontakty i zaproszenia", `/contacts/${TEST_USER_ID}`);
    await checkEndpoint("Szczegóły użytkownika", `/users/${TEST_USER_ID}`);
    console.log("\nKoniec testów!");
}

runTests();