**COLLEGIUM WITELONA**

**Uczelnia Państwowa**

**Wydział Nauk Technicznych i Ekonomicznych**

**Kierunek Informatyka**

**Specjalność PAM**

**NATALIA KLIMASZEWSKA**\
**MAKYSMILAIN WILK**

**BARTOSZ DOBROŚ**\
**ROBERT KUŹBA**\
**JAKUB**

**System zarządzania domowym akwarium**

**Legnica 2025**

**Spis treści**

[Rozdział 1. Wstęp do projektu.
[2](#rozdział-1.-wstęp-do-projektu.)](#rozdział-1.-wstęp-do-projektu.)

[Opis projektu [2](#opis-projektu)](#opis-projektu)

[Funkcjonalności systemu
[2](#funkcjonalności-systemu)](#funkcjonalności-systemu)

[Technologie [3](#technologie)](#technologie)

[Grupa projektowa i role w zespole
[4](#grupa-projektowa-i-role-w-zespole)](#grupa-projektowa-i-role-w-zespole)

# 

# Rozdział 1. Wstęp do projektu.

## Opis projektu

> Celem projektu jest stworzenie webowego systemu informatycznego do
> zarządzania

domowym akwarium, który będę wspomagał użytkowników w utrzymaniu domowej
hodowli ryb i roślin akwariowych. System umożliwi rejestrację
użytkowników, tworzenie profili akwariów, dodanie ryb i roślin oraz
monitorowanie zgodności warunków środowiskowych. System będzie
analizował parametry takie jak pH oraz zasolenie wody, temperaturę, a
także ostrzeże, gdy w akwarium będą znajdować się gatunki, które nie
powinny być trzymane razem. Dzięki integracji z modułem AI możliwe będą
podpowiedzi, jakie nowe gatunki poleca się dodać, w celu zachowania
równowagi ekosystemu.

System będzie wyposażony w panel superadministratora, który umożliwi
zarządzanie użytkownikami, kontrolę danych systemowych oraz przeglądanie
logów wszystkich akcji wykonywanych w aplikacji. Każde działanie
użytkownika będzie rejestrowane wraz z datą, godziną i identyfikatorem
użytkownika.

## Funkcjonalności systemu 

1.  Rejestracja i logowanie

    a.  Rejestracja przez e-mail.

    b.  Rejestracja przez konto Facebook.

    c.  Logowanie z użyciem hasła lub autoryzacji zewnętrznej (OAuth).

2.  Zarządzanie akwariami

    a.  Stworzenie akwariów.

    b.  Dodanie i edycja akwariów.

    c.  Zapraszanie do współdzielenia akwarium.

    d.  Możliwość usuwania i archiwizacji akwariów.

    e.  Użytkownik może zasymulować dodanie nowych ryb przed faktycznym
        zakupem.

3.  Zarządzanie gatunkami

    a.  Dodawanie ryb i roślin do akwarium.

    b.  Przechowywanie informacji o parametrach środowiskowych.

    c.  System ostrzegający o niezgodnościach środowiskowych.

4.  Statystyki i historia

    a.  Wyświetlanie liczby gatunków w akwarium.

    b.  Obliczanie średniej długości życia ryb.

    c.  Historia dodawania i usuwania ryb.

    d.  Raporty stanu akwarium.

5.  Sugestie AI

    a.  Moduł sztucznej inteligencji rekomendujący kompatybilne gatunki
        ryb i roślin.

    b.  Analiza bieżącego stanu akwarium i proponowanie usprawnień w
        środowisku.

6.  Panel SuperAdministratora

    a.  Zarządzanie użytkownikami (blokowanie, usuwanie, restartowanie
        haseł).

    b.  Podgląd logów aplikacji.

    c.  Edycja oraz nadzór nad danymi systemowymi.

7.  System poziomów i osiągnięć (gamefication)

    a.  Użytkownik może zbierać punkty za:

        i.  Utrzymanie stabilnych parametrów wody.

        ii. Regularne karmienie.

        iii. Brak uśmierconych rybek.

    b.  Użytkowni może uzyskać odznaki za osiągnięcia.

8.  System powiadomień i planner

    a.  Użytkownik może utworzyć harmonogram zadań i przypisać
        użytkownika.

    b.  Powiadomienia e-mail / push o:

        i.  Karmieniu ryb.

        ii. Przekroczeniu temperatury / pH wody.

        iii. Urodzinach ryb.

        iv. Czyszczeniu akwarium.

## Technologie

Frontend:

Backend:

Baza danych:

Autoryzacja:

AI / Rekomendacje:

Logowanie zdarzeń:

## Grupa projektowa i role w zespole

+-----------------------+-----------------------+-----------------------+
| Imię i nazwisko       | Rola w zespole        | Zakres obowiązków     |
+=======================+=======================+=======================+
| Maksymilian Wilk      | Projekt Manager       | Rozdzielanie zadań,   |
|                       |                       | koordynowanie działań |
|                       | Programista backend   | zespołu, kontakt z    |
|                       |                       | prowadzącym zajęcia,  |
|                       | DevOps                |                       |
|                       |                       | implementacja logiki  |
|                       |                       | biznesowej, baza      |
|                       |                       | danych, API, system   |
|                       |                       | logowania akcji       |
|                       |                       | użytkowników          |
+-----------------------+-----------------------+-----------------------+
| Natalia Klimaszewska  | Tester                | Testowanie            |
|                       |                       | funkcjonalności,      |
|                       | Frontend              | opracowanie           |
|                       |                       | scenariuszy testowych |
|                       | Researcher            | i przypadków          |
|                       |                       | testowych, analiza    |
|                       |                       | dot. gatunków ryb,    |
|                       |                       | roślin i parametrów   |
|                       |                       | środowiskowych,       |
|                       |                       | projektowanie i       |
|                       |                       | implementacja         |
|                       |                       | interfejsu            |
|                       |                       | graficznego,          |
+-----------------------+-----------------------+-----------------------+
| Bartosz Dobroś        | Tester                | Testowanie aplikacji, |
|                       |                       | weryfikacja           |
|                       |                       | poprawności           |
|                       |                       | wprowadzania danych,  |
|                       |                       | testowanie            |
|                       |                       | zabezpieczeń,         |
|                       |                       | sprawdzanie           |
|                       |                       | wydajności i          |
|                       |                       | stabilności           |
|                       |                       | aplikacji.            |
+-----------------------+-----------------------+-----------------------+
| Robert Kuźba          | Backend               | Logika aplikacji,     |
|                       |                       | API, baza danych      |
+-----------------------+-----------------------+-----------------------+
| Jakub Janiec          | Frontend              | Implementacja         |
|                       |                       | interfejsu            |
|                       |                       | graficznego,          |
|                       |                       | współpraca z          |
|                       |                       | testerami i           |
|                       |                       | backendem, integracja |
|                       |                       | API                   |
+-----------------------+-----------------------+-----------------------+
| Wszyscy               | Ogólne                | Sporządzanie          |
|                       |                       | dokumentacji          |
|                       |                       | projektowej,          |
|                       |                       | udzielanie pomocy,    |
|                       |                       | przekazywanie         |
|                       |                       | informacji o          |
|                       |                       | niedociągnięciach     |
|                       |                       | oraz niedziałających  |
|                       |                       | funkcjach.            |
+-----------------------+-----------------------+-----------------------+
