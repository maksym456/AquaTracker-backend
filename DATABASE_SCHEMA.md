# Struktura bazy danych AquaTracker

## Tabele w bazie danych

### 1. `users` - Użytkownicy
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `username` (VARCHAR)
- `email` (VARCHAR, UNIQUE, NOT NULL)
- `password` (VARCHAR)
- `created_at` (TIMESTAMP)
- `settings_language` (VARCHAR) - domyślnie "pl"
- `settings_theme` (VARCHAR) - domyślnie "light"
- `settings_session_length_minutes` (INTEGER) - domyślnie 60
- `settings_data_source` (VARCHAR) - domyślnie "production"

**Unikalność:**
- `email` - UNIQUE

**Relacje:**
- Jeden użytkownik może mieć wiele akwariów (`aquariums.user_id`)
- Jeden użytkownik może mieć wiele logów (`log_entries.user_id`)
- Jeden użytkownik może mieć wiele kontaktów (`contacts.user_id`)
- Jeden użytkownik może wysyłać wiele zaproszeń (`invitations.sender_id`)
- Jeden użytkownik może otrzymać wiele zaproszeń (`invitations.recipient_id`)

### 2. `aquariums` - Akwaria
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `user_id` (BIGINT, FOREIGN KEY -> users.id, nullable)
- `name` (VARCHAR)
- `description` (TEXT)
- `volume_liters` (INTEGER)
- `water_type` (VARCHAR) - "Słodkowodna", "Słonowodna", "Słonawowodna"
- `temperaturec` (DOUBLE)
- `ph` (DOUBLE, nullable)
- `hardness` (INTEGER, nullable) - stara kolumna, zachowana dla kompatybilności
- `hardness_dgh` (INTEGER, nullable)
- `biotope` (VARCHAR)
- `created_at` (TIMESTAMP)

**Relacje:**
- Należy do użytkownika (`user_id` -> `users.id`)
- Może mieć wiele ryb (`aquarium_fish.aquarium_id`)
- Może mieć wiele roślin (`aquarium_plant.aquarium_id`)
- Może mieć wiele logów (`log_entries.aquarium_id`)

### 3. `fish_species` - Gatunki ryb
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `name` (VARCHAR)
- `water_type` (VARCHAR)
- `biotype` (VARCHAR)
- `temp_minc` (INTEGER)
- `temp_maxc` (INTEGER)
- `ph_min` (DOUBLE)
- `ph_max` (DOUBLE)
- `gh_min` (INTEGER)
- `gh_max` (INTEGER)
- `temperament` (VARCHAR) - "spokojne", "pół-agresywne", "agresywne"
- `min_school_size` (INTEGER)
- `lifespan` (VARCHAR)
- `description` (TEXT)
- `image` (VARCHAR) - ścieżka do obrazu
- `icon_name` (VARCHAR) - nazwa ikony

**Relacje:**
- Może być w wielu akwariach (`aquarium_fish.fish_species_id`)

### 4. `plants` - Rośliny
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `name` (VARCHAR)
- `species` (VARCHAR)
- `biotope` (VARCHAR)
- `temp_minc` (INTEGER)
- `temp_maxc` (INTEGER)
- `ph_min` (DOUBLE)
- `ph_max` (DOUBLE)
- `gh_min` (INTEGER)
- `gh_max` (INTEGER)
- `icon_name` (VARCHAR)

**Relacje:**
- Może być w wielu akwariach (`aquarium_plant.plant_id`)

### 5. `aquarium_fish` - Relacja akwarium-ryba (z liczbą)
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `aquarium_id` (BIGINT, FOREIGN KEY -> aquariums.id)
- `fish_species_id` (BIGINT, FOREIGN KEY -> fish_species.id)
- `fish_count` (INTEGER) - liczba ryb danego gatunku w akwarium

**Relacje:**
- Należy do akwarium (`aquarium_id` -> `aquariums.id`)
- Odnosi się do gatunku ryby (`fish_species_id` -> `fish_species.id`)

**Unikalność:**
- Kombinacja `(aquarium_id, fish_species_id)` - UNIQUE (jeden gatunek ryby raz na akwarium)

**Ograniczenia:**
- `aquarium_id` - NOT NULL
- `fish_species_id` - NOT NULL
- `fish_count` - NOT NULL

### 6. `aquarium_plant` - Relacja akwarium-roślina (z liczbą)
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `aquarium_id` (BIGINT, FOREIGN KEY -> aquariums.id)
- `plant_id` (BIGINT, FOREIGN KEY -> plants.id)
- `plant_count` (INTEGER) - liczba roślin danego gatunku w akwarium

**Relacje:**
- Należy do akwarium (`aquarium_id` -> `aquariums.id`)
- Odnosi się do rośliny (`plant_id` -> `plants.id`)

**Unikalność:**
- Kombinacja `(aquarium_id, plant_id)` - UNIQUE (jeden gatunek rośliny raz na akwarium)

**Ograniczenia:**
- `aquarium_id` - NOT NULL
- `plant_id` - NOT NULL
- `plant_count` - NOT NULL

### 7. `log_entries` - Historia zmian (logi)
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `user_id` (BIGINT, FOREIGN KEY -> users.id, nullable)
- `aquarium_id` (BIGINT, FOREIGN KEY -> aquariums.id, nullable)
- `aquarium_name` (VARCHAR) - kopia nazwy akwarium (dla historii)
- `action_type` (VARCHAR) - typ akcji: "AQUARIUM_CREATED", "AQUARIUM_UPDATED", "FISH_ADDED", "FISH_REMOVED", "FISH_UPDATED", "PLANT_ADDED", "PLANT_REMOVED", "PLANT_UPDATED", "PARAM_CHANGED"
- `title` (VARCHAR)
- `message` (TEXT)
- `metadata` (TEXT) - JSON z dodatkowymi danymi
- `created_at` (TIMESTAMP)

**Relacje:**
- Należy do użytkownika (`user_id` -> `users.id`)
- Odnosi się do akwarium (`aquarium_id` -> `aquariums.id`)

### 8. `contacts` - Kontakty między użytkownikami
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `user_id` (BIGINT, FOREIGN KEY -> users.id)
- `friend_id` (BIGINT, FOREIGN KEY -> users.id)
- `friend_name` (VARCHAR) - kopia nazwy użytkownika
- `friend_email` (VARCHAR) - kopia emaila użytkownika
- `status` (VARCHAR) - "PENDING", "ACCEPTED", "REJECTED"
- `created_at` (TIMESTAMP)

**Relacje:**
- Należy do użytkownika (`user_id` -> `users.id`)
- Odnosi się do znajomego (`friend_id` -> `users.id`)

**Unikalność:**
- Kombinacja `(user_id, friend_id)` - UNIQUE

**Ograniczenia:**
- `user_id` - NOT NULL
- `friend_id` - NOT NULL

### 9. `invitations` - Zaproszenia do kontaktów
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `sender_id` (BIGINT, FOREIGN KEY -> users.id)
- `recipient_email` (VARCHAR)
- `recipient_id` (BIGINT, FOREIGN KEY -> users.id, nullable) - może być null jeśli użytkownik nie istnieje jeszcze
- `status` (VARCHAR) - "PENDING", "ACCEPTED", "REJECTED"
- `created_at` (TIMESTAMP)
- `responded_at` (TIMESTAMP, nullable)

**Relacje:**
- Wysyłający (`sender_id` -> `users.id`)
- Odbiorca (`recipient_id` -> `users.id`, nullable)

## Diagram relacji

```
users (1) ----< (N) aquariums
users (1) ----< (N) log_entries
users (1) ----< (N) contacts (user_id)
users (1) ----< (N) contacts (friend_id)
users (1) ----< (N) invitations (sender_id)
users (1) ----< (N) invitations (recipient_id)

aquariums (1) ----< (N) aquarium_fish
aquariums (1) ----< (N) aquarium_plant
aquariums (1) ----< (N) log_entries

fish_species (1) ----< (N) aquarium_fish
plants (1) ----< (N) aquarium_plant
```

## Automatyczne tworzenie tabel

Tabele są automatycznie tworzone/aktualizowane przez Hibernate przy starcie aplikacji dzięki ustawieniu:
```properties
spring.jpa.hibernate.ddl-auto=update
```

## Uwagi

1. **Unikalność w tabelach pośrednich**: W `aquarium_fish` i `aquarium_plant` powinna być unikalność na kombinacji `(aquarium_id, fish_species_id)` i `(aquarium_id, plant_id)` odpowiednio. Hibernate powinien to obsłużyć automatycznie.

2. **Kaskadowe usuwanie**: 
   - Usunięcie akwarium usuwa wszystkie powiązane `aquarium_fish` i `aquarium_plant`
   - Logi są zachowywane nawet po usunięciu akwarium (nullable foreign key)

3. **Historia**: Tabela `log_entries` przechowuje historię zmian, nawet jeśli akwarium lub użytkownik zostanie usunięty (nullable foreign keys + kopie danych w `aquarium_name`).

4. **Ustawienia użytkownika**: Przechowywane bezpośrednio w tabeli `users`, nie w osobnej tabeli.

