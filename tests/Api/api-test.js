const BASE_URL = process.env.BASE_URL || "http://localhost:3001";  
const JWT = process.env.JWT || "";  
async function checkEndpoint(name, path, options = {}) {
    const url = `${BASE_URL}${path}`;
    const headers = {
        "Accept": "application/json"
    };
    if (JWT || options.forceAuth) {
        headers["Authorization"] = `Bearer ${JWT}`;
    }

    try {
        const response = await fetch(url, { headers });
        if (response.status === 200) {
            const contentType = response.headers.get("content-type") || "";
            if (contentType.includes("application/json")) {
                console.log(`✅ ${name} – działa! (200 OK, JSON)`);
            } else {
                console.log(`🟡 ${name} – 200 OK, ale nie JSON (typ: ${contentType})`);
            }
        } else {
            console.log(`❌ ${name} – błąd: ${response.status} ${response.statusText}`);
        }
    } catch (error) {
        console.log(`❌ ${name} – błąd połączenia: ${error.message}`);
    }
}

async function runTests() {
    console.log(`Testuję API na: ${BASE_URL}\n`);
    await checkEndpoint("Dokumentacja API", "/v3/api-docs");
    if (!JWT) {
        console.log("\n🟡 Większość endpointów wymaga JWT – podaj token, aby przetestować resztę!");
        console.log("   Jak zdobyć JWT: krok po kroku poniżej.\n");
    } else {
        await checkEndpoint("Lista ryb (fish)", "/fish?limit=10"); 
        await checkEndpoint("Lista roślin (plants)", "/plants?limit=10");
        await checkEndpoint("Lista moich akwariów", "/aquariums");
        await checkEndpoint("Globalne statystyki akwariów", "/aquariums/stats");
        await checkEndpoint("Ustawienia użytkownika", "/settings");
        await checkEndpoint("Lista logów", "/logs");
        await checkEndpoint("Kontakty i zaproszenia", "/contacts");
    }

    console.log("\nKoniec testów!");
}

runTests();