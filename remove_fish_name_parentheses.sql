-- Skrypt do usunięcia nazw w nawiasach z nazw ryb
-- Aktualizuje istniejące rekordy w bazie danych

UPDATE fish_species
SET name = 'Welonka'
WHERE name = 'Welonka (Złota rybka)';

UPDATE fish_species
SET name = 'Gupik'
WHERE name = 'Gupik (Głupik)';

UPDATE fish_species
SET name = 'Skalar'
WHERE name = 'Skalar (Żaglowiec)';

UPDATE fish_species
SET name = 'Glonojad'
WHERE name = 'Glonojad (Zbrojnik)';

UPDATE fish_species
SET name = 'Pyszczak'
WHERE name = 'Pyszczak (Malawi)';

-- Sprawdź ile rekordów zostało zaktualizowanych
SELECT name FROM fish_species WHERE name IN ('Welonka', 'Gupik', 'Skalar', 'Glonojad', 'Pyszczak');
