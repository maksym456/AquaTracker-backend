-- Skrypt SQL do uzupełnienia opisów i nazw plików dla ryb w bazie danych AWS
-- Uruchom ten skrypt na bazie danych PostgreSQL na AWS

UPDATE fish_species SET 
    description = 'Welonka to klasyczna, spokojna ryba akwariowa, idealna dla początkujących. Jest odporna i łatwa w utrzymaniu.',
    image = '/fish/Welonka__Złota_rybka.png',
    icon_name = 'Welonka__Złota_rybka.png'
WHERE name = 'Welonka (Złota rybka)';

UPDATE fish_species SET 
    description = 'Gupik to mała, kolorowa ryba, która najlepiej czuje się w grupie. Jest bardzo aktywna i łatwa w hodowli.',
    image = '/fish/Gupik__Głupik.png',
    icon_name = 'Gupik__Głupik.png'
WHERE name = 'Gupik (Głupik)';

UPDATE fish_species SET 
    description = 'Bojownik syjamski to efektowna, majestatyczna ryba znana z długich, falujących płetw i intensywnych barw. Samce są terytorialne i potrafią być agresywne wobec innych samców oraz ryb o podobnych płetwach, dlatego zwykle trzyma się je pojedynczo.',
    image = '/fish/Bojownik_syjamski.png',
    icon_name = 'Bojownik_syjamski.png'
WHERE name = 'Bojownik syjamski';

UPDATE fish_species SET 
    description = 'Neon Innesa to drobna, energiczna ryba ławicowa, znana z intensywnego niebieskiego połysku widocznego nawet w słabym oświetleniu.',
    image = '/fish/Neon_Innesa.png',
    icon_name = 'Neon_Innesa.png'
WHERE name = 'Neon Innesa';

UPDATE fish_species SET 
    description = 'Skalar (Żaglowiec) to ryba pół-agresywna, która najlepiej czuje się w grupie. Lubi dużo miejsca do pływania i rośliny, przy których może się chować. Może pokazywać dominujące zachowania wobec innych ryb, dlatego najlepiej trzymać ją z gatunkami o podobnym temperamencie.',
    image = '/fish/Skalar__Żaglowiec.png',
    icon_name = 'Skalar__Żaglowiec.png'
WHERE name = 'Skalar (Żaglowiec)';

UPDATE fish_species SET 
    description = 'Mieczyk Hellera to żyworodna, wyrazista ryba znana z charakterystycznego ''mieczyka'' na ogonie samców. Jest ruchliwa, wytrzymała i dobrze odnajduje się w większych akwariach. Choć generalnie towarzyska, potrafi wykazywać lekko dominujące zachowania, zwłaszcza samce między sobą, dlatego najlepiej trzymać ją w większej grupie.',
    image = '/fish/Mieczyk_Hellera.png',
    icon_name = 'Mieczyk_Hellera.png'
WHERE name = 'Mieczyk Hellera';

UPDATE fish_species SET 
    description = 'Molinezja to spokojna ryba, która najlepiej czuje się w grupie. Jest aktywna i lubi pływać wśród roślin. Jest odporna i łatwa w utrzymaniu, dobrze nadaje się do akwarium z innymi spokojnymi rybami.',
    image = '/fish/Molinezja.png',
    icon_name = 'Molinezja.png'
WHERE name = 'Molinezja';

UPDATE fish_species SET 
    description = 'Gurami mozaikowy to spokojna ryba o charakterystycznym, drobnym, mozaikowym wzorze na ciele. Porusza się powoli i często wykorzystuje wydłużone płetwy piersiowe do badania otoczenia.',
    image = '/fish/Gurami_mozaikowy.png',
    icon_name = 'Gurami_mozaikowy.png'
WHERE name = 'Gurami mozaikowy';

UPDATE fish_species SET 
    description = 'Danio pręgowany to szybka, energiczna ryba ławicowa o smukłym ciele i wyraźnych, poziomych pręgach. Jest bardzo odporna i dobrze adaptuje się do różnych warunków, dzięki czemu świetnie nadaje się dla początkujących.',
    image = '/fish/Danio_pręgowany.png',
    icon_name = 'Danio_pręgowany.png'
WHERE name = 'Danio pręgowany';

UPDATE fish_species SET 
    description = 'Kardynałek chiński to niewielka, żywa i spokojna ryba o metalicznym połysku i czerwonym zabarwieniu płetw. Jest wyjątkowo odporna i dobrze czuje się nawet w chłodniejszych akwariach. W grupie prezentuje naturalne, harmonijne zachowania, tworząc efektowne mini-ławice.',
    image = '/fish/Kardynałek_chiński.png',
    icon_name = 'Kardynałek_chiński.png'
WHERE name = 'Kardynałek chiński';

UPDATE fish_species SET 
    description = 'Razbora klinowa to spokojna ryba ławicowa, która najlepiej czuje się w grupie. Jest aktywna i porusza się wśród roślin, tworząc efektowne grupy. Lubi dobrze oświetlone akwaria z miejscami do pływania i kryjówkami.',
    image = '/fish/Razbora_klinowa.png',
    icon_name = 'Razbora_klinowa.png'
WHERE name = 'Razbora klinowa';

UPDATE fish_species SET 
    description = 'Tęczanka neonowa to spokojna ryba ławicowa, która najlepiej czuje się w grupie. Ma kolorowe, metaliczne ubarwienie i lubi poruszać się wśród roślin. Jest odporna i łatwa w utrzymaniu, dobrze nadaje się do akwarium z innymi spokojnymi rybami.',
    image = '/fish/Tęczanka_neonowa.png',
    icon_name = 'Tęczanka_neonowa.png'
WHERE name = 'Tęczanka neonowa';

UPDATE fish_species SET 
    description = 'Kirysek pstry to spokojna ryba, która lubi przebywać przy dnie akwarium i chować się między roślinami. Najlepiej czuje się w grupie, wtedy porusza się naturalnie i aktywnie.',
    image = '/fish/Kirys_pstry.png',
    icon_name = 'Kirys_pstry.png'
WHERE name = 'Kirys pstry';

UPDATE fish_species SET 
    description = 'Glonojad / Zbrojnik to spokojna ryba, która pomaga utrzymać akwarium w czystości, zjadając glony z roślin i szybów. Lubi kryjówki i spokojne miejsca w zbiorniku. Jest odporna i łatwa w utrzymaniu, dobrze nadaje się do akwarium z innymi spokojnymi rybami.',
    image = '/fish/GlonojadZbrojnik-.png',
    icon_name = 'GlonojadZbrojnik-.png'
WHERE name = 'Glonojad (Zbrojnik)';

UPDATE fish_species SET 
    description = 'Błazenek pomarańczowy to spokojna ryba, która najlepiej czuje się w parze. Lubi miejsca do ukrycia, np. między skałami lub wśród korali. Jest odporna, ale wymaga stabilnych warunków wody słonowodnej i odpowiedniej temperatury.',
    image = '/fish/Błazenek_pomarańczowy.png',
    icon_name = 'Błazenek_pomarańczowy.png'
WHERE name = 'Błazenek pomarańczowy';

UPDATE fish_species SET 
    description = 'Pirania czerwona to agresywna ryba, która najlepiej żyje w grupie. Potrzebuje dużo miejsca do pływania i odpowiedniego akwarium, aby mogła wykazywać naturalne zachowania.',
    image = '/fish/Pirania_czerwona.png',
    icon_name = 'Pirania_czerwona.png'
WHERE name = 'Pirania czerwona';

UPDATE fish_species SET 
    description = 'Pokolec królewski to spokojna ryba, którą najlepiej trzymać pojedynczo. Lubi mieć miejsca do ukrycia, np. między skałami lub koralami. Jest odporna i może żyć długo w akwarium słonowodnym przy stabilnych warunkach wody.',
    image = '/fish/Pokolec_królewski.png',
    icon_name = 'Pokolec_królewski.png'
WHERE name = 'Pokolec królewski';

UPDATE fish_species SET 
    description = 'Proporczykowiec to ryba pół-agresywna, która najlepiej czuje się w grupie. Lubi mieć kryjówki i miejsca do pływania. Może wykazywać dominujące zachowania wobec innych ryb, dlatego najlepiej trzymać ją z gatunkami o podobnym temperamencie.',
    image = '/fish/Proporczykowiec.png',
    icon_name = 'Proporczykowiec.png'
WHERE name = 'Proporczykowiec';

UPDATE fish_species SET 
    description = 'Pyszczak (Malawi) to agresywna ryba, która najlepiej czuje się w swoim terytorium. Lubi mieć kryjówki i przestrzeń do pływania.',
    image = '/fish/Pyszczak__Malawi.png',
    icon_name = 'Pyszczak__Malawi.png'
WHERE name = 'Pyszczak (Malawi)';

UPDATE fish_species SET 
    description = 'Księżniczka z Burundi to agresywna ryba, która najlepiej czuje się w grupie. Lubi mieć kryjówki i dużo miejsca do pływania.',
    image = '/fish/Księżniczka_z_Burundi.png',
    icon_name = 'Księżniczka_z_Burundi.png'
WHERE name = 'Księżniczka z Burundi';

UPDATE fish_species SET 
    description = 'Kolcobrzuch karłowaty to agresywna ryba, którą najlepiej trzymać pojedynczo. Ma mocny charakter i potrafi bronić swojego terytorium. Lubi kryjówki i miejsca do ukrycia. Jest odporna, ale wymaga stabilnych warunków wody.',
    image = '/fish/Kolcobrzuch_karłowaty.png',
    icon_name = 'Kolcobrzuch_karłowaty.png'
WHERE name = 'Kolcobrzuch karłowaty';

UPDATE fish_species SET 
    description = 'Mandaryn wspaniały to spokojna ryba, którą najlepiej trzymać pojedynczo. Lubi miejsca do ukrycia i rośliny lub koralowce, w których może się poruszać. Jest wrażliwa na warunki wody, dlatego wymaga stabilnego akwarium słonowodnego.',
    image = '/fish/Mandaryn_wspaniały.png',
    icon_name = 'Mandaryn_wspaniały.png'
WHERE name = 'Mandaryn wspaniały';

UPDATE fish_species SET 
    description = 'Ustnik słoneczny to spokojna ryba, którą najlepiej trzymać pojedynczo. Lubi mieć miejsca do ukrycia, np. między skałami lub koralami. Jest odporna i może żyć długo w akwarium słonowodnym przy stabilnych warunkach wody.',
    image = '/fish/Ustnik_żółty_ryba.png',
    icon_name = 'Ustnik_żółty_ryba.png'
WHERE name = 'Ustnik słoneczny';

UPDATE fish_species SET 
    description = 'Babka złota to spokojna ryba, która najlepiej czuje się w grupie. Jest aktywna i lubi pływać wśród roślin oraz kryjówek. Jest odporna i łatwa w utrzymaniu, dobrze nadaje się do akwarium z innymi spokojnymi rybami.',
    image = '/fish/Babka_złota.png',
    icon_name = 'Babka_złota.png'
WHERE name = 'Babka złota';

-- Sprawdzenie, czy wszystkie rekordy zostały zaktualizowane
SELECT name, 
       CASE WHEN description IS NULL OR description = '' THEN 'BRAK OPISU' ELSE 'OK' END as opis_status,
       CASE WHEN image IS NULL OR image = '' THEN 'BRAK OBRAZU' ELSE 'OK' END as obraz_status,
       CASE WHEN icon_name IS NULL OR icon_name = '' THEN 'BRAK IKONY' ELSE 'OK' END as ikona_status
FROM fish_species
ORDER BY name;

