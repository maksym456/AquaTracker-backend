# AquaTracker API (v1)

Base URL:
`https://api.aquatracker.com/v1`

All requests and responses are JSON.

Authentication (except for register and login) uses a Bearer token header:
`Authorization: Bearer <JWT>`

## 1. Authentication

### 1.1 Register

Create a new account.

`POST /auth/register`

Request:

```json
{
  "username": "aquarist123",
  "email": "user@example.com",
  "password": "StrongPassword123"
}
```

Response 201:

```json
{
  "id": "u_123",
  "username": "aquarist123",
  "email": "user@example.com",
  "createdAt": "2025-10-27T12:00:00Z"
}
```

### 1.2 Login

Get a session token.

`POST /auth/login`

Request:

```json
{
  "email": "user@example.com",
  "password": "StrongPassword123"
}
```

Response 200:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "user": {
    "id": "u_123",
    "username": "aquarist123",
    "email": "user@example.com"
  }
}
```

### 1.3 Get current user

Return info about the logged-in user.

`GET /auth/me`

Headers:
`Authorization: Bearer <token>`

Response 200:

```json
{
  "id": "u_123",
  "username": "aquarist123",
  "email": "user@example.com",
  "createdAt": "2025-10-27T12:00:00Z"
}
```

## 2. Data models

These are the core objects returned by the API.

### 2.1 Fish

A species entry in the catalog.
Note: no field for "Środowisko naturalne".

```json
{
  "id": "fish_001",
  "name": "Neon Innesa",
  "waterType": "Słodkowodna", 
  "temperatureC": [22, 26], 
  "temperament": "Spokojna",
  "compatibleWith": ["Razbory", "Kirysy", "Glonojady"],
  "recommendedPlants": ["Moczarka", "Nurzaniec"]
}
```

### 2.2 Aquarium

An aquarium saved by the user.

```json
{
  "id": "aq_777",
  "name": "Tropical Haven",
  "volumeLiters": 120,
  "temperatureC": 26,
  "waterType": "Słodkowodna",
  "fish": [
    { "fishId": "fish_001", "count": 10 },
    { "fishId": "fish_010", "count": 2 }
  ],
  "plants": ["Moczarka", "Nurzaniec"],
  "createdAt": "2025-10-27T12:00:00Z"
}
```

### 2.3 Disease

A known fish illness.

```json
{
  "id": "dis_ich",
  "name": "Ospa rybia (Ichtio)",
  "symptoms": "Białe kropki, ocieranie się o dekoracje",
  "causes": "Pasożyt, nagłe zmiany temperatury",
  "treatment": "Leczenie FMC, Esha Exit, podgrzanie wody",
  "prevention": "Kwarantanna nowych ryb, stabilna temperatura 25–26°C"
}
```

## 3. Fish API

Read-only public catalog of fish species.

### 3.1 List fish

`GET /fish`

Optional query params:

* `waterType`
  Example: `Słodkowodna`, `Słonawowodna`, `Słonowodna`
* `temperament`
  Example: `Spokojna`, `Półagresywny`, `Agresywny`
* `tempMin` (number, °C)
* `tempMax` (number, °C)

Response 200:

```json
[
  {
    "id": "fish_001",
    "name": "Neon Innesa",
    "waterType": "Słodkowodna",
    "temperatureC": [22, 26],
    "temperament": "Spokojna",
    "compatibleWith": ["Razbory", "Kirysy", "Glonojady"],
    "recommendedPlants": ["Moczarka", "Nurzaniec"]
  }
]
```

### 3.2 Get fish by id

`GET /fish/{fishId}`

Response 200:

```json
{
  "id": "fish_010",
  "name": "Bojownik syjamski",
  "waterType": "Słodkowodna",
  "temperatureC": [25, 30],
  "temperament": "Półagresywny (samiec)",
  "compatibleWith": ["Kirysy", "Glonojady"],
  "recommendedPlants": ["Mech Jawajski", "Anubias"]
}
```

## 4. Aquarium API

All Aquarium endpoints require authentication.
A user can only access and modify their own aquariums.

### 4.1 List my aquariums

`GET /aquariums`

Headers:
`Authorization: Bearer <token>`

Response 200:

```json
[
  {
    "id": "aq_777",
    "name": "Tropical Haven",
    "volumeLiters": 120,
    "temperatureC": 26,
    "waterType": "Słodkowodna",
    "fish": [
      { "fishId": "fish_001", "count": 10 },
      { "fishId": "fish_010", "count": 2 }
    ],
    "plants": ["Moczarka", "Nurzaniec"],
    "createdAt": "2025-10-27T12:00:00Z"
  }
]
```

### 4.2 Create aquarium

`POST /aquariums`

Headers:
`Authorization: Bearer <token>`

Request:

```json
{
  "name": "Malawi Setup",
  "volumeLiters": 200,
  "temperatureC": 27,
  "waterType": "Słodkowodna",
  "fish": [
    { "fishId": "fish_malawi_1", "count": 6 }
  ],
  "plants": ["Skały", "Anubias"]
}
```

Response 201:

```json
{
  "id": "aq_901",
  "name": "Malawi Setup",
  "volumeLiters": 200,
  "temperatureC": 27,
  "waterType": "Słodkowodna",
  "fish": [
    { "fishId": "fish_malawi_1", "count": 6 }
  ],
  "plants": ["Skały", "Anubias"],
  "createdAt": "2025-10-27T12:05:00Z"
}
```

### 4.3 Get aquarium by id

`GET /aquariums/{aquariumId}`

Headers:
`Authorization: Bearer <token>`

Response 200:

```json
{
  "id": "aq_901",
  "name": "Malawi Setup",
  "volumeLiters": 200,
  "temperatureC": 27,
  "waterType": "Słodkowodna",
  "fish": [
    { "fishId": "fish_malawi_1", "count": 6 }
  ],
  "plants": ["Skały", "Anubias"],
  "createdAt": "2025-10-27T12:05:00Z"
}
```

### 4.4 Update aquarium

`PUT /aquariums/{aquariumId}`

Headers:
`Authorization: Bearer <token>`

Request:

```json
{
  "name": "Malawi Display Tank",
  "volumeLiters": 200,
  "temperatureC": 27,
  "waterType": "Słodkowodna",
  "fish": [
    { "fishId": "fish_malawi_1", "count": 8 }
  ],
  "plants": ["Skały"]
}
```

Response 200:

```json
{
  "id": "aq_901",
  "name": "Malawi Display Tank",
  "volumeLiters": 200,
  "temperatureC": 27,
  "waterType": "Słodkowodna",
  "fish": [
    { "fishId": "fish_malawi_1", "count": 8 }
  ],
  "plants": ["Skały"],
  "createdAt": "2025-10-27T12:05:00Z"
}
```

### 4.5 Delete aquarium

`DELETE /aquariums/{aquariumId}`

Headers:
`Authorization: Bearer <token>`

Response 204
(no body)

## 5. Disease API

Read-only list of fish illnesses for diagnosis and prevention.

### 5.1 List diseases

`GET /diseases`

Response 200:

```json
[
  {
    "id": "dis_finrot",
    "name": "Martwica płetw",
    "symptoms": [
      "Postrzępione, szare lub białe końcówki płetw",
      "skracanie płetw",
      "apatia"],
    "causes": "Bakterie, stres, zła jakość wody",
    "treatment": "Poprawa filtracji, kąpiele w Baktopur / Esha 2000",
    "prevention": "Regularna podmiana wody, unikanie przerybienia"
  }
]
```

### 5.2 Get disease by id

`GET /diseases/{diseaseId}`

Response 200:

```json
{
  "id": "dis_ich",
  "name": "Ospa rybia (Ichtio)",
  "symptoms": "Białe kropki, ocieranie się o dekoracje",
  "causes": "Pasożyt, nagłe zmiany temperatury",
  "treatment": "Leczenie FMC, Esha Exit, podgrzanie wody",
  "prevention": "Kwarantanna nowych ryb, stabilna temperatura 25–26°C"
}
```

## 6. Errors

Your API should return standard HTTP codes:

* 200 OK
* 201 Created
* 204 No Content
* 400 Bad Request (invalid data)
* 401 Unauthorized (no or bad token)
* 403 Forbidden (trying to access or modify something that is not yours)
* 404 Not Found
* 422 Unprocessable Entity (rules/validation failed, e.g. impossible fish combination)
* 500 Internal Server Error

Recommended error body:

```json
{
  "error": "ValidationError",
  "message": "temperatureC must be between 15 and 35",
  "field": "temperatureC"
}
```