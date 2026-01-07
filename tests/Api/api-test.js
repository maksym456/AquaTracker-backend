const BASE_URL = process.env.BASE_URL || "http://localhost:3001/api/v1";
const JWT = process.env.JWT || "";

function decodeJwtPayload(token) {
    try {
        const parts = token.split('.');
        if (parts.length !== 3) return null;
        const payload = parts[1];
        const padded = payload + '='.repeat((4 - payload.length % 4) % 4);
        const decoded = Buffer.from(padded, 'base64url').toString('utf8');
        return JSON.parse(decoded);
    } catch (e) {
        console.log(" Błąd dekodowania JWT:", e.message);
        return null;
    }
}

async function checkEndpoint(name, path, options = {}) {
    const url = `${BASE_URL}${path}`;
    const headers = {
        "Accept": "application/json",
        ...(JWT ? { "Authorization": `Bearer ${JWT}` } : {})
    };

    try {
        const response = await fetch(url, { headers });
        if (response.status === 200) {
            const ct = response.headers.get("content-type") || "";
            if (ct.includes("application/json")) {
                console.log(` ${name} – działa! (200 OK, JSON)`);
            } else {
                console.log(` ${name} – 200 OK, ale nie JSON`);
            }
        } else {
            console.log(` ${name} – błąd: ${response.status} ${response.statusText}`);
        }
    } catch (error) {
        console.log(` ${name} – błąd połączenia: ${error.message}`);
    }
}

async function runTests() {
    console.log(`Testuję API v1 na: ${BASE_URL}\n`);
    await checkEndpoint("Dokumentacja OpenAPI", "/v3/api-docs");

    if (!JWT) {
        console.log("\n Brak JWT – nie można przetestować endpointów chronionych");
        console.log("   Ustaw: $env:JWT = \"twój-token-z-cookie-next-auth\"");
        return;
    }
    const payload = decodeJwtPayload(JWT);
    if (!payload) {
        console.log("\n Nie udało się dekodować JWT – testy chronione pominięte");
        return;
    }
    const userId = payload.sub || payload.userId || payload["cognito:username"];
    if (!userId) {
        console.log("\n Nie znaleziono userId w JWT (sprawdź claim 'sub')");
        console.log("   Payload JWT:", payload);
        return;
    }

    console.log(` Pobrano userId z JWT: ${userId}\n`);

    
    await checkEndpoint("Lista ryb (fish)", "/fish");
    await checkEndpoint("Lista roślin (plants)", "/plants");

    await checkEndpoint("Lista moich akwariów", `/aquariums/${userId}`);
    await checkEndpoint("Lista logów", `/logs/${userId}`);
    await checkEndpoint("Kontakty i zaproszenia", `/contacts/${userId}`);
    await checkEndpoint("Szczegóły użytkownika", `/users/${userId}`);
    console.log("\nKoniec automatycznych testów!");
}

runTests();