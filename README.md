**COLLEGIUM WITELONA**

**Uczelnia Państwowa**

**Wydział Nauk Technicznych i Ekonomicznych**

**Kierunek Informatyka**

**Specjalność PAM**

**NATALIA KLIMASZEWSKA**  
**MAKSYM WILK**

**BARTOSZ DOBROŚ**  
**ROBERT KUŹBA**  
**JAKUB JANIEC**

**System zarządzania domowym akwarium - AquaTracker**

**Legnica 2025**

**Spis treści**

[Rozdział 1. Wstęp do projektu. 2](#_Toc2076231146)

[Opis projektu 2](#_Toc1000131666)

[Funkcjonalności systemu 3](#_Toc1227174376)

[Technologie 4](#_Toc248261460)

[Grupa projektowa i role w zespole 4](#_Toc355052822)

# Rozdział 1. Wstęp do projektu

## Opis projektu

Celem projektu jest stworzenie webowego systemu informatycznego do zarządzania

domowym akwarium, który będę wspomagał użytkowników w utrzymaniu domowej hodowli ryb i roślin akwariowych. System umożliwi rejestrację użytkowników, tworzenie profili akwariów, dodanie ryb i roślin oraz monitorowanie zgodności warunków środowiskowych. System będzie analizował parametry takie jak pH oraz zasolenie wody, temperaturę, a także ostrzeże, gdy w akwarium będą znajdować się gatunki, które nie powinny być trzymane razem. Dzięki integracji z modułem AI możliwe będą podpowiedzi, jakie nowe gatunki poleca się dodać, w celu zachowania równowagi ekosystemu.

System będzie wyposażony w panel superadministratora, który umożliwi zarządzanie użytkownikami, kontrolę danych systemowych oraz przeglądanie logów wszystkich akcji wykonywanych w aplikacji. Każde działanie użytkownika będzie rejestrowane wraz z datą, godziną i identyfikatorem użytkownika.

## Funkcjonalności systemu

- Rejestracja i logowanie
  - Rejestracja przez e-mail.
  - Rejestracja przez konto Facebook.
  - Logowanie z użyciem hasła lub autoryzacji zewnętrznej (OAuth).
- Zarządzanie akwariami
  - Stworzenie akwariów.
  - Dodanie i edycja akwariów.
  - Zapraszanie do współdzielenia akwarium.
  - Możliwość usuwania i archiwizacji akwariów.
  - Użytkownik może zasymulować dodanie nowych ryb przed faktycznym zakupem.
- Zarządzanie gatunkami
  - Dodawanie ryb i roślin do akwarium.
  - Przechowywanie informacji o parametrach środowiskowych.
  - System ostrzegający o niezgodnościach środowiskowych.
- Statystyki i historia
  - Wyświetlanie liczby gatunków w akwarium.
  - Obliczanie średniej długości życia ryb.
  - Historia dodawania i usuwania ryb.
  - Raporty stanu akwarium.
- Sugestie AI
  - Moduł sztucznej inteligencji rekomendujący kompatybilne gatunki ryb i roślin.
  - Analiza bieżącego stanu akwarium i proponowanie usprawnień w środowisku.
- Panel SuperAdministratora
  - Zarządzanie użytkownikami (blokowanie, usuwanie, restartowanie haseł).
  - Podgląd logów aplikacji.
  - Edycja oraz nadzór nad danymi systemowymi.
- System poziomów i osiągnięć (gamefication)
  - Użytkownik może zbierać punkty za:
    - Utrzymanie stabilnych parametrów wody.
    - Regularne karmienie.
    - Brak uśmierconych rybek.
  - Użytkowni może uzyskać odznaki za osiągnięcia.
- System powiadomień i planner
  - Użytkownik może utworzyć harmonogram zadań i przypisać użytkownika.
  - Powiadomienia e-mail / push o:
    - Karmieniu ryb.
    - Przekroczeniu temperatury / pH wody.
    - Urodzinach ryb.
    - Czyszczeniu akwarium.

## Tech stack

Frontend: Javascript + Tailwind

Backend: Java + Azure

Baza danych: Azure SQL Database

Autoryzacja: Authentication and authorization in Azure App Service and Azure Functions

AI / Rekomendacje: Github copilot

Logowanie zdarzeń: Azure Monitor Logs

Testowanie i automatyzacja testów: Robot framework + playwright

## Grupa projektowa i role w zespole

| Imię i nazwisko | Rola w zespole | Zakres obowiązków |
| --- | --- | --- |
| Maksym Wilk | Projekt Manager<br><br>Programista backend<br><br>DevOps | Rozdzielanie zadań, koordynowanie działań zespołu, kontakt z prowadzącym zajęcia,<br><br>implementacja logiki biznesowej, baza danych, API, system logowania akcji użytkowników |
| Natalia Klimaszewska | Tester<br><br>Frontend<br><br>Researcher | Testowanie funkcjonalności, opracowanie scenariuszy testowych i przypadków testowych, analiza dot. gatunków ryb, roślin i parametrów środowiskowych, projektowanie i implementacja interfejsu graficznego, |
| Bartosz Dobroś | Tester | Testowanie aplikacji, weryfikacja poprawności wprowadzania danych, testowanie zabezpieczeń, sprawdzanie wydajności i stabilności aplikacji. |
| Robert Kuźba | Backend | Logika aplikacji, API, baza danych |
| Jakub Janiec | Frontend | Implementacja interfejsu graficznego, współpraca z testerami i backendem, integracja API |
| Wszyscy | Ogólne | Sporządzanie dokumentacji projektowej, udzielanie pomocy, przekazywanie informacji o niedociągnięciach oraz niedziałających funkcjach. |
