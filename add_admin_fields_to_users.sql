-- Skrypt migracyjny do dodania kolumn active i is_admin do tabeli users
-- Dodaje pola wymagane dla panelu administratora
-- Uruchom ten skrypt na bazie danych PostgreSQL

-- Dodanie kolumny active (domyślnie TRUE - wszyscy istniejący użytkownicy są aktywni)
ALTER TABLE users ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE;

-- Ustawienie wszystkich istniejących użytkowników jako aktywnych
UPDATE users SET active = TRUE WHERE active IS NULL;

-- Ustawienie kolumny active jako NOT NULL (po ustawieniu wartości domyślnych)
ALTER TABLE users ALTER COLUMN active SET NOT NULL;

-- Dodanie kolumny is_admin (domyślnie FALSE - żaden użytkownik nie jest administratorem)
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_admin BOOLEAN DEFAULT FALSE;

-- Ustawienie wszystkich istniejących użytkowników jako nie-adminów
UPDATE users SET is_admin = FALSE WHERE is_admin IS NULL;

-- Ustawienie kolumny is_admin jako NOT NULL (po ustawieniu wartości domyślnych)
ALTER TABLE users ALTER COLUMN is_admin SET NOT NULL;

-- Sprawdzenie, czy wszystkie rekordy zostały zaktualizowane
SELECT 
    id, 
    email, 
    username, 
    active, 
    is_admin,
    CASE WHEN active IS NULL THEN 'BRAK WARTOSCI' ELSE 'OK' END as active_status,
    CASE WHEN is_admin IS NULL THEN 'BRAK WARTOSCI' ELSE 'OK' END as admin_status
FROM users
ORDER BY email;

-- Przykład ustawienia użytkownika jako administratora (zamień EMAIL na rzeczywisty email)
-- UPDATE users SET is_admin = TRUE WHERE email = 'admin@example.com';

-- Przykład deaktywacji użytkownika (zamień EMAIL na rzeczywisty email)
-- UPDATE users SET active = FALSE WHERE email = 'user@example.com';
