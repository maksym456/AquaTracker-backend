md
# AquaTracker API v1

Base URL:  
`http://localhost:3001/api/v1/`

Wszystkie requesty i response’y są w JSON (`Content-Type: application/json`).
Autoryzacja dla endpointów chronionych:

`Authorization: Bearer <JWT>`

## 1. Modele danych

### 1.1 User

```json
{
  "id": "u_123",
  "username": "aquarist123",
  "email": "user@example.com",
  "createdAt": "2025-10-27T12:00:00Z",
  "settings": {
    "language": "pl",
    "theme": "dark",
    "dataSource": "production"
  }
}
````

`settings.language`: `"pl"` lub `"en"`
`settings.theme`: `"light"` lub `"dark"`
`settings.dataSource`: np. `"production"`, `"demo"`

### 1.2 Fish

Pola oparte wyłącznie na arkuszu `Ryby` + techniczne `id` i `iconName`.

```json
{
  "id": "fish_001",
  "name": "Neon Innesa",
  "waterType": "Słodkowodna",
  "temperature": "22-26",
  "biotope": "Ameryka Południowa",
  "ph": "6.5-7.5",
  "hardnessDGH": "1-12",
  "temperament": "spokojne",
  "minShoalSize": 10,
  "lifeSpan": "3-5 lat",
  "iconName": "neon_innesa.png"
}
```

Mapowanie na kolumny arkusza:

* `name` ↔ „Nazwa Ryby”
* `waterType` ↔ „Typ Wody”
* `temperature` ↔ „Temperatura (°C)” (np. `"22-26"`)
* `biotope` ↔ „Biotyp”
* `ph` ↔ „Ph wody”
* `hardnessDGH` ↔ „Twardość wody (°dGH)`
* `temperament` ↔ „Usposobienie”
* `minShoalSize` ↔ „Ilość w stadzie (minimalnie)”
* `lifeSpan` ↔ „Średnia długość życia”
* `iconName` – nazwa ikonki używanej w UI

### 1.3 Plant

Pola oparte wyłącznie na arkuszu `Rośliny` + techniczne `id` i `iconName`.

```json
{
  "id": "plant_001",
  "name": "Moczarka",
  "temperature": "12-20",
  "biotope": "Ameryka Północna",
  "ph": "6.0-8.0",
  "hardnessDGH": "5-20",
  "iconName": "moczarka.png"
}
```

Mapowanie na kolumny:

* `name` ↔ „Nazwa”
* `temperature` ↔ „Temperatura (°C)”
* `biotope` ↔ „Biotyp”
* `ph` ↔ „Ph wody”
* `hardnessDGH` ↔ „Twardość wody (°dGH)`
* `iconName` – nazwa ikonki używanej w UI

### 1.4 Aquarium

```json
{
  "id": "aq_777",
  "ownerId": "u_123",
  "name": "Moje pierwsze akwarium",
  "description": "Pierwsze domowe akwarium",
  "volumeLiters": 120,
  "waterType": "Słodkowodna",
  "temperatureC": 26,
  "ph": 7.2,
  "hardnessDGH": 10,
  "fish": [
    { "fishId": "fish_001", "count": 1 }
  ],
  "plants": [
    { "plantId": "plant_001", "count": 1 },
    { "plantId": "plant_002", "count": 1 }
  ],
  "status": {
    "level": "OK",
    "issues": [],
    "lastCheckedAt": "2025-01-25T10:00:00Z"
  },
  "createdAt": "2025-01-20T09:00:00Z"
}
```

Przykładowy status z problemami:

```json
{
  "level": "WARNING",
  "issues": [
    {
      "type": "TEMPERATURE_OUT_OF_RANGE",
      "message": "Temperatura 30°C poza zakresem dla Neon Innesa (22-26)."
    },
    {
      "type": "INSUFFICIENT_GROUP_SIZE",
      "message": "Zbyt mała liczebność stada Neon Innesa (min. 10, jest 3)."
    }
  ],
  "lastCheckedAt": "2025-01-25T11:00:00Z"
}
```

### 1.5 LogEntry (historia)

```json
{
  "id": "log_001",
  "userId": "u_123",
  "aquariumId": "aq_777",
  "aquariumName": "Moje pierwsze akwarium",
  "actionType": "PARAM_CHANGED",
  "title": "Zmieniono parametr",
  "message": "Zmieniono twardość wody: 10 dGH → 12 dGH",
  "createdAt": "2025-01-23T14:30:00Z",
  "metadata": {
    "parameter": "hardnessDGH",
    "oldValue": 10,
    "newValue": 12
  }
}
```

Przykładowe `actionType`:

* `AQUARIUM_CREATED`
* `AQUARIUM_UPDATED`
* `FISH_ADDED`
* `FISH_REMOVED`
* `PLANT_ADDED`
* `PLANT_REMOVED`
* `PARAM_CHANGED`

### 1.6 Contact

```json
{
  "id": "c_001",
  "userId": "u_123",
  "friendId": "u_456",
  "friendName": "Jan Kowalski",
  "friendEmail": "jan.kowalski@example.com",
  "status": "ACCEPTED",
  "createdAt": "2025-01-20T10:00:00Z"
}
```

### 1.7 Invitation

```json
{
  "id": "inv_001",
  "senderId": "u_123",
  "recipientEmail": "anna.nowak@example.com",
  "recipientUserId": "u_789",
  "status": "PENDING",
  "createdAt": "2025-01-21T09:00:00Z",
  "respondedAt": null
}
```

### 2.3 Get current user

## 3. Settings

### 3.1 Get settings

`GET /settings`

Headers: `Authorization: Bearer <token>`

Response 200:

```json
{
  "language": "pl",
  "theme": "dark",
  "sessionLengthMinutes": 60,
  "dataSource": "production"
}
```

### 3.2 Update settings

`PATCH /settings`

Headers: `Authorization: Bearer <token>`

Request (dowolny podzbiór):

```json
{
  "language": "en",
  "theme": "dark",
  "sessionLengthMinutes": 90,
  "dataSource": "demo"
}
```

Response 200:

```json
{
  "language": "en",
  "theme": "dark",
  "sessionLengthMinutes": 90,
  "dataSource": "demo"
}
```
## 4. Fish API

### 4.1 List fish

`GET /fish`

Query params (opcjonalne):

* `q` – fragment nazwy (`name`)
* `waterType` – `"Słodkowodna"`, `"Słonawowodna"`, `"Słonowodna"`
* `temperament` – np. `"spokojne"`, `"pół-agresywne"`
* `tempMin` – minimalna temperatura (number, °C)
* `tempMax` – maksymalna temperatura (number, °C)
* `limit` – rozmiar strony
* `offset` – przesunięcie

Przykład:

`GET /fish?waterType=Słodkowodna&temperament=spokojne&q=Neon`

Response 200:

```json
[
  {
    "id": "fish_001",
    "name": "Neon Innesa",
    "waterType": "Słodkowodna",
    "temperature": "22-26",
    "biotope": "Ameryka Południowa",
    "ph": "6.5-7.5",
    "hardnessDGH": "1-12",
    "temperament": "spokojne",
    "minShoalSize": 10,
    "lifeSpan": "3-5 lat",
    "iconName": "neon_innesa.png"
  }
]
```

### 4.2 Get fish by id

`GET /fish/{fishId}`

Response 200:

```json
{
  "id": "fish_002",
  "name": "Gupik (Głupik)",
  "waterType": "Słodkowodna",
  "temperature": "24-28",
  "biotope": "Ameryka Południowa",
  "ph": "6.0-8.0",
  "hardnessDGH": "10-30",
  "temperament": "spokojne",
  "minShoalSize": 5,
  "lifeSpan": "2-3 lata",
  "iconName": "gupik.png"
}
```

## 5. Plant API

### 5.1 List plants

`GET /plants`

Query params (opcjonalne):

* `q` – fragment nazwy (`name`)
* `biotope` – np. `"Azja"`, `"Ameryka Północna"`
* `tempMin` – minimalna temperatura (number, °C)
* `tempMax` – maksymalna temperatura (number, °C)
* `limit`, `offset` – paginacja

Przykład:

`GET /plants?q=Moczarka`

Response 200:

```json
[
  {
    "id": "plant_001",
    "name": "Moczarka",
    "temperature": "12-20",
    "biotope": "Ameryka Północna",
    "ph": "6.0-8.0",
    "hardnessDGH": "5-20",
    "iconName": "moczarka.png"
  }
]
```

### 5.2 Get plant by id

`GET /plants/{plantId}`

Response 200:

```json
{
  "id": "plant_002",
  "name": "Nurzaniec",
  "temperature": "20-28",
  "biotope": "Afryka, Azja, Europa",
  "ph": "6.8-9.5",
  "hardnessDGH": "5-15",
  "iconName": "nurzaniec.png"
}
```

## 6. Aquarium API

Wszystkie endpointy w tej sekcji: `Authorization: Bearer <token>`
Użytkownik widzi i modyfikuje tylko swoje akwaria (`ownerId`).

### 6.1 List my aquariums

`GET /aquariums`

Response 200:

```json
[
  {
    "id": "aq_777",
    "ownerId": "u_123",
    "name": "Moje pierwsze akwarium",
    "description": "Pierwsze domowe akwarium",
    "volumeLiters": 120,
    "waterType": "Słodkowodna",
    "temperatureC": 26,
    "ph": 7.2,
    "hardnessDGH": 10,
    "fish": [
      { "fishId": "fish_001", "count": 1 }
    ],
    "plants": [
      { "plantId": "plant_001", "count": 1 },
      { "plantId": "plant_002", "count": 1 }
    ],
    "status": {
      "level": "OK",
      "issues": [],
      "lastCheckedAt": "2025-01-25T10:00:00Z"
    },
    "createdAt": "2025-01-20T09:00:00Z"
  }
]
```

### 6.2 Create aquarium

`POST /aquariums`

Request:

```json
{
  "name": "Malawi Setup",
  "description": "Drugie domowe akwarium",
  "volumeLiters": 200,
  "waterType": "Słodkowodna",
  "temperatureC": 27,
  "ph": 7.8,
  "hardnessDGH": 12,
  "fish": [
    { "fishId": "fish_malawi_1", "count": 6 }
  ],
  "plants": [
    { "plantId": "plant_rock_anubias", "count": 3 }
  ]
}
```

Response 201:

```json
{
  "id": "aq_901",
  "ownerId": "u_123",
  "name": "Malawi Setup",
  "description": "Drugie domowe akwarium",
  "volumeLiters": 200,
  "waterType": "Słodkowodna",
  "temperatureC": 27,
  "ph": 7.8,
  "hardnessDGH": 12,
  "fish": [
    { "fishId": "fish_malawi_1", "count": 6 }
  ],
  "plants": [
    { "plantId": "plant_rock_anubias", "count": 3 }
  ],
  "status": {
    "level": "OK",
    "issues": [],
    "lastCheckedAt": "2025-10-27T12:05:00Z"
  },
  "createdAt": "2025-10-27T12:05:00Z"
}
```

### 6.3 Get aquarium by id

`GET /aquariums/{aquariumId}`

Response 200:

```json
{
  "id": "aq_901",
  "ownerId": "u_123",
  "name": "Malawi Setup",
  "description": "Drugie domowe akwarium",
  "volumeLiters": 200,
  "waterType": "Słodkowodna",
  "temperatureC": 27,
  "ph": 7.8,
  "hardnessDGH": 12,
  "fish": [
    { "fishId": "fish_malawi_1", "count": 6 }
  ],
  "plants": [
    { "plantId": "plant_rock_anubias", "count": 3 }
  ],
  "status": {
    "level": "OK",
    "issues": [],
    "lastCheckedAt": "2025-10-27T12:05:00Z"
  },
  "createdAt": "2025-10-27T12:05:00Z"
}
```

### 6.4 Update aquarium (full)

`PUT /aquariums/{aquariumId}`

Request:

```json
{
  "name": "Malawi Display Tank",
  "description": "Drugie domowe akwarium",
  "volumeLiters": 200,
  "waterType": "Słodkowodna",
  "temperatureC": 27,
  "ph": 7.8,
  "hardnessDGH": 12,
  "fish": [
    { "fishId": "fish_malawi_1", "count": 8 }
  ],
  "plants": [
    { "plantId": "plant_rock_anubias", "count": 3 }
  ]
}
```

Response 200:

```json
{
  "id": "aq_901",
  "ownerId": "u_123",
  "name": "Malawi Display Tank",
  "description": "Drugie domowe akwarium",
  "volumeLiters": 200,
  "waterType": "Słodkowodna",
  "temperatureC": 27,
  "ph": 7.8,
  "hardnessDGH": 12,
  "fish": [
    { "fishId": "fish_malawi_1", "count": 8 }
  ],
  "plants": [
    { "plantId": "plant_rock_anubias", "count": 3 }
  ],
  "status": {
    "level": "OK",
    "issues": [],
    "lastCheckedAt": "2025-10-27T13:00:00Z"
  },
  "createdAt": "2025-10-27T12:05:00Z"
}
```

### 6.5 Update aquarium parameters (partial)

`PATCH /aquariums/{aquariumId}/parameters`

Request:

```json
{
  "temperatureC": 25,
  "ph": 7.0,
  "hardnessDGH": 12
}
```

Response 200:

```json
{
  "id": "aq_901",
  "ownerId": "u_123",
  "name": "Malawi Display Tank",
  "description": "Drugie domowe akwarium",
  "volumeLiters": 200,
  "waterType": "Słodkowodna",
  "temperatureC": 25,
  "ph": 7.0,
  "hardnessDGH": 12,
  "fish": [
    { "fishId": "fish_malawi_1", "count": 8 }
  ],
  "plants": [
    { "plantId": "plant_rock_anubias", "count": 3 }
  ],
  "status": {
    "level": "WARNING",
    "issues": [
      {
        "type": "TEMPERATURE_OUT_OF_RANGE",
        "message": "Temperatura 25°C poza zakresem dla jakiegoś gatunku."
      }
    ],
    "lastCheckedAt": "2025-10-27T13:10:00Z"
  },
  "createdAt": "2025-10-27T12:05:00Z"
}
```

### 6.6 Add fish to aquarium

`POST /aquariums/{aquariumId}/fish`

Request:

```json
{
  "fishId": "fish_001",
  "count": 5
}
```

Response 200:

```json
{
  "aquarium": {
    "id": "aq_777",
    "ownerId": "u_123",
    "name": "Moje pierwsze akwarium",
    "volumeLiters": 120,
    "waterType": "Słodkowodna",
    "temperatureC": 26,
    "ph": 7.2,
    "hardnessDGH": 10,
    "fish": [
      { "fishId": "fish_001", "count": 6 }
    ],
    "plants": [
      { "plantId": "plant_001", "count": 1 },
      { "plantId": "plant_002", "count": 1 }
    ],
    "status": {
      "level": "OK",
      "issues": [],
      "lastCheckedAt": "2025-01-25T10:05:00Z"
    },
    "createdAt": "2025-01-20T09:00:00Z"
  },
  "logEntry": {
    "id": "log_010",
    "userId": "u_123",
    "aquariumId": "aq_777",
    "aquariumName": "Moje pierwsze akwarium",
    "actionType": "FISH_ADDED",
    "title": "Dodano ryby",
    "message": "Dodano 5 x Neon Innesa.",
    "createdAt": "2025-01-25T10:05:00Z",
    "metadata": {
      "fishId": "fish_001",
      "count": 5
    }
  }
}
```

### 6.7 Update fish count in aquarium

`PATCH /aquariums/{aquariumId}/fish/{fishId}`

Request:

```json
{
  "count": 3
}
```

Response 200:

```json
{
  "aquarium": {
    "id": "aq_777",
    "fish": [
      { "fishId": "fish_001", "count": 3 }
    ],
    "status": {
      "level": "WARNING",
      "issues": [
        {
          "type": "INSUFFICIENT_GROUP_SIZE",
          "message": "Zbyt mała liczebność stada Neon Innesa (min. 10, jest 3)."
        }
      ],
      "lastCheckedAt": "2025-01-25T10:15:00Z"
    }
  },
  "logEntry": {
    "id": "log_011",
    "userId": "u_123",
    "aquariumId": "aq_777",
    "aquariumName": "Moje pierwsze akwarium",
    "actionType": "FISH_UPDATED",
    "title": "Zmieniono ilość ryb",
    "message": "Zmieniono ilość Neon Innesa na 3.",
    "createdAt": "2025-01-25T10:15:00Z",
    "metadata": {
      "fishId": "fish_001",
      "count": 3
    }
  }
}
```

### 6.8 Remove fish from aquarium

`DELETE /aquariums/{aquariumId}/fish/{fishId}`
Opcjonalnie: `DELETE /aquariums/{aquariumId}/fish/{fishId}?count=2`

Response 200:

```json
{
  "aquarium": {
    "id": "aq_777",
    "fish": [],
    "status": {
      "level": "OK",
      "issues": [],
      "lastCheckedAt": "2025-01-25T10:20:00Z"
    }
  },
  "logEntry": {
    "id": "log_012",
    "userId": "u_123",
    "aquariumId": "aq_777",
    "aquariumName": "Moje pierwsze akwarium",
    "actionType": "FISH_REMOVED",
    "title": "Usunięto ryby",
    "message": "Usunięto Neon Innesa.",
    "createdAt": "2025-01-25T10:20:00Z",
    "metadata": {
      "fishId": "fish_001"
    }
  }
}
```

### 6.9 Add plant to aquarium

`POST /aquariums/{aquariumId}/plants`

Request:

```json
{
  "plantId": "plant_001",
  "count": 3
}
```

Response 200:

```json
{
  "aquarium": {
    "id": "aq_777",
    "plants": [
      { "plantId": "plant_001", "count": 3 }
    ],
    "status": {
      "level": "OK",
      "issues": [],
      "lastCheckedAt": "2025-01-25T10:25:00Z"
    }
  },
  "logEntry": {
    "id": "log_013",
    "userId": "u_123",
    "aquariumId": "aq_777",
    "aquariumName": "Moje pierwsze akwarium",
    "actionType": "PLANT_ADDED",
    "title": "Dodano rośliny",
    "message": "Dodano 3 x Moczarka.",
    "createdAt": "2025-01-25T10:25:00Z",
    "metadata": {
      "plantId": "plant_001",
      "count": 3
    }
  }
}
```

### 6.10 Update plant count in aquarium

`PATCH /aquariums/{aquariumId}/plants/{plantId}`

Request:

```json
{
  "count": 2
}
```

Response 200:

```json
{
  "aquarium": {
    "id": "aq_777",
    "plants": [
      { "plantId": "plant_001", "count": 2 }
    ],
    "status": {
      "level": "OK",
      "issues": [],
      "lastCheckedAt": "2025-01-25T10:30:00Z"
    }
  },
  "logEntry": {
    "id": "log_014",
    "userId": "u_123",
    "aquariumId": "aq_777",
    "aquariumName": "Moje pierwsze akwarium",
    "actionType": "PLANT_UPDATED",
    "title": "Zmieniono ilość roślin",
    "message": "Zmieniono ilość Moczarki na 2.",
    "createdAt": "2025-01-25T10:30:00Z",
    "metadata": {
      "plantId": "plant_001",
      "count": 2
    }
  }
}
```

### 6.11 Remove plant from aquarium

`DELETE /aquariums/{aquariumId}/plants/{plantId}`
Opcjonalnie: `?count=1`

Response 200:

```json
{
  "aquarium": {
    "id": "aq_777",
    "plants": [],
    "status": {
      "level": "OK",
      "issues": [],
      "lastCheckedAt": "2025-01-25T10:35:00Z"
    }
  },
  "logEntry": {
    "id": "log_015",
    "userId": "u_123",
    "aquariumId": "aq_777",
    "aquariumName": "Moje pierwsze akwarium",
    "actionType": "PLANT_REMOVED",
    "title": "Usunięto rośliny",
    "message": "Usunięto Moczarkę.",
    "createdAt": "2025-01-25T10:35:00Z",
    "metadata": {
      "plantId": "plant_001"
    }
  }
}
```

### 6.12 Recalculate aquarium status

`POST /aquariums/{aquariumId}/status/recalculate`

Response 200:

```json
{
  "status": {
    "level": "WARNING",
    "issues": [
      {
        "type": "TEMPERATURE_OUT_OF_RANGE",
        "message": "Temperatura 30°C poza zakresem dla Neon Innesa (22-26)."
      }
    ],
    "lastCheckedAt": "2025-01-25T11:00:00Z"
  }
}
```

### 6.13 Delete aquarium

`DELETE /aquariums/{aquariumId}`

Response 204

## 7. Statistics API

Wszystkie endpointy: `Authorization: Bearer <token>`

### 7.1 Global stats – all my aquariums

`GET /aquariums/stats`

Response 200:

```json
{
  "totalAquariums": 2,
  "totalFishCount": 2,
  "totalPlantCount": 3,
  "fishSpeciesCount": 1,
  "plantSpeciesCount": 2,
  "fishDistribution": [
    {
      "fishId": "fish_001",
      "name": "Neon Innesa",
      "count": 1,
      "percentage": 100.0
    }
  ],
  "plantDistribution": [
    {
      "plantId": "plant_001",
      "name": "Moczarka",
      "count": 1,
      "percentage": 50.0
    },
    {
      "plantId": "plant_002",
      "name": "Nurzaniec",
      "count": 1,
      "percentage": 50.0
    }
  ],
  "mostCommonFish": {
    "fishId": "fish_001",
    "name": "Neon Innesa",
    "count": 1,
    "percentage": 100.0
  },
  "mostCommonPlant": {
    "plantId": "plant_001",
    "name": "Moczarka",
    "count": 1,
    "percentage": 50.0
  }
}
```

### 7.2 Stats for single aquarium

`GET /aquariums/{aquariumId}/stats`

Response 200:

```json
{
  "aquariumId": "aq_777",
  "fishCount": 1,
  "plantCount": 2,
  "fishSpeciesCount": 1,
  "plantSpeciesCount": 2,
  "fishDistribution": [
    {
      "fishId": "fish_001",
      "name": "Neon Innesa",
      "count": 1,
      "percentage": 100.0
    }
  ],
  "plantDistribution": [
    {
      "plantId": "plant_001",
      "name": "Moczarka",
      "count": 1,
      "percentage": 50.0
    },
    {
      "plantId": "plant_002",
      "name": "Nurzaniec",
      "count": 1,
      "percentage": 50.0
    }
  ],
  "mostCommonFish": {
    "fishId": "fish_001",
    "name": "Neon Innesa",
    "count": 1,
      "percentage": 100.0
  },
  "mostCommonPlant": {
    "plantId": "plant_001",
    "name": "Moczarka",
    "count": 1,
    "percentage": 50.0
  }
}
```

## 8. Logs API (Historia)

`Authorization: Bearer <token>`

### 8.1 List logs for current user

`GET /logs`

Query params (opcjonalne):

* `actionType` – np. `FISH_ADDED`, `PARAM_CHANGED`
* `aquariumId` – filtrowanie po akwarium
* `sort` – `"asc"` lub `"desc"` (domyślnie `"desc"`)
* `limit`, `offset` – paginacja

Przykład:

`GET /logs?actionType=PARAM_CHANGED&aquariumId=aq_777&sort=desc`

Response 200:

```json
[
  {
    "id": "log_001",
    "userId": "u_123",
    "aquariumId": "aq_777",
    "aquariumName": "Moje pierwsze akwarium",
    "actionType": "PARAM_CHANGED",
    "title": "Zmieniono parametr",
    "message": "Zmieniono twardość wody: 10 dGH → 12 dGH",
    "createdAt": "2025-01-23T14:30:00Z",
    "metadata": {
      "parameter": "hardnessDGH",
      "oldValue": 10,
      "newValue": 12
    }
  },
  {
    "id": "log_002",
    "userId": "u_123",
    "aquariumId": "aq_888",
    "aquariumName": "Drugie akwarium",
    "actionType": "PLANT_ADDED",
    "title": "Dodano roślinę",
    "message": "Dodano roślinę: Moczarka",
    "createdAt": "2025-01-24T12:00:00Z",
    "metadata": {
      "plantId": "plant_001",
      "count": 1
    }
  }
]
```

## 9. Contacts API (Kontakty)

`Authorization: Bearer <token>`

### 9.1 Get contacts and invitations

`GET /contacts`

Response 200:

```json
{
  "friends": [
    {
      "id": "c_001",
      "userId": "u_123",
      "friendId": "u_456",
      "friendName": "Jan Kowalski",
      "friendEmail": "jan.kowalski@example.com",
      "status": "ACCEPTED",
      "createdAt": "2025-01-20T10:00:00Z"
    },
    {
      "id": "c_002",
      "userId": "u_123",
      "friendId": "u_789",
      "friendName": "Anna Nowak",
      "friendEmail": "anna.nowak@example.com",
      "status": "PENDING",
      "createdAt": "2025-01-21T09:00:00Z"
    }
  ],
  "invitations": [
    {
      "id": "inv_001",
      "senderId": "u_123",
      "recipientEmail": "anna.nowak@example.com",
      "recipientUserId": "u_789",
      "status": "PENDING",
      "createdAt": "2025-01-21T09:00:00Z",
      "respondedAt": null
    }
  ]
}
```

### 9.2 Send invitation by email

`POST /contacts/invitations`

Request:

```json
{
  "email": "anna.nowak@example.com"
}
```

Response 201 (user istnieje):

```json
{
  "invitation": {
    "id": "inv_001",
    "senderId": "u_123",
    "recipientEmail": "anna.nowak@example.com",
    "recipientUserId": "u_789",
    "status": "PENDING",
    "createdAt": "2025-01-21T09:00:00Z",
    "respondedAt": null
  }
}
```

Response 404 (user nie istnieje):

```json
{
  "error": "UserNotFound",
  "message": "Użytkownik o podanym adresie e-mail nie istnieje."
}
```

### 9.3 Accept invitation

`POST /contacts/invitations/{invitationId}/accept`

Response 200:

```json
{
  "invitation": {
    "id": "inv_001",
    "senderId": "u_123",
    "recipientEmail": "anna.nowak@example.com",
    "recipientUserId": "u_789",
    "status": "ACCEPTED",
    "createdAt": "2025-01-21T09:00:00Z",
    "respondedAt": "2025-01-22T11:00:00Z"
  },
  "contact": {
    "id": "c_003",
    "userId": "u_789",
    "friendId": "u_123",
    "friendName": "aquarist123",
    "friendEmail": "user@example.com",
    "status": "ACCEPTED",
    "createdAt": "2025-01-22T11:00:00Z"
  }
}
```

### 9.4 Reject invitation

`POST /contacts/invitations/{invitationId}/reject`

Response 200:

```json
{
  "invitation": {
    "id": "inv_001",
    "senderId": "u_123",
    "recipientEmail": "anna.nowak@example.com",
    "recipientUserId": "u_789",
    "status": "REJECTED",
    "createdAt": "2025-01-21T09:00:00Z",
    "respondedAt": "2025-01-22T11:05:00Z"
  }
}
```

### 9.5 Remove friend

`DELETE /contacts/{contactId}`

Response 204

## 10. Errors

Standardowe kody:

* 200 OK
* 201 Created
* 204 No Content
* 400 Bad Request
* 401 Unauthorized
* 403 Forbidden
* 404 Not Found
* 422 Unprocessable Entity
* 500 Internal Server Error

Rekomendowany format błędu:

```json
{
  "error": "ValidationError",
  "message": "temperatureC must be between 15 and 35",
  "field": "temperatureC"
}
```

Przykład błędu kompatybilności:

```json
{
  "error": "CompatibilityError",
  "message": "Bojownik syjamski nie powinien być trzymany z innymi samcami tego gatunku.",
  "details": {
    "fishId": "fish_betta",
    "conflictingFishIds": ["fish_betta_male_2"]
  }
}
```