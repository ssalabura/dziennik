Dziennik elektroniczny
Autorzy:
Grzegorz Gawryał
Łukasz Prajer
Szymon Salabura

Krótki opis projektu:
Baza danych modeluje prosty dziennik elektroniczy z możliwościami wystawiania ocen, obecności, planowania sprawdzianów i przydzielania różnych grup przedmiotowych.
Aplikacja obsługiwać będzie konta nauczycieli, rodziców/opiekunów prawnych i uczniów.
W bazie danych zawarte są również dane kontaktowe do wszystkich osób.

Obecność krotki w tabeli absences oznacza, że dany uczeń był na danych zajęciach nieobecny; gdy takiej krotki nie ma, to był on obecny.

Ocenę z danego przedmiotu można wystawić tylko uczniowi, który chodzi na dany przedmiot, ale nie musi jej wystawiać nauczyciel uczący daną grupę - może to być dowolny nauczyciel, który może uczyć tego przedmiotu. Ma to umożliwić wpisywanie ocen w czasie zastępstwa nauczyciela.
Tabela exams ma przechowywać egzaminy zaplanowane lub przeprowadzone na konkretnej lekcji.
Lekcje mogą odbywać się tylko w określonych przedziałach czasu (slotach). Każdy slot jest przedziałem czasu, w którym może odbyć się lekcja (wszystkie lekcje trwają 45 min) - ma to modelować typowy podział godzin w szkołach (inaczej niż na studiach, gdzie godziny zajęć nie są narzucone z góry).


Podział pracy:

Grzegorz Gawryał:
- Enum grades
- Przykładowe przedmioty, obecności
- Tabele: nauczyciele, uczniowie, przedmioty, nauczyciele-przedmioty, lekcje, obecności, oceny
- Relacje między nauczycielami, przedmiotami i ocenami
- Check do absences (danej osobie można dać obecność tylko, gdy jest w odpowiedniej grupie), oraz telefonów
- Diagram ER

Łukasz Prajer:
- Tabele: opiekunowie, opiekonuowie-uczniowie
- Triggery na usuwanie ucznia/opiekuna
- Trigger na PESEL (z zadania H), dodawanie nauczycieli (generowanie następnego wolnego indeksu)
- Checki na imie, nazwisko, email
- Przykładowe grupy, nauczyciele, studenci, opiekunowie, egzaminy, oceny
- Relacje między opiekunami a uczniami

Szymon Salabura:
- Tabele: sprawdziany, grupy, grupy-uczniowie
- Widok classes_avg pokazujący średnie uczniów z poszczególnych przedmiotów
- Dobór odpowiednich typów danych i constraint'ów do tabel
- Refaktoryzacja kodu


Aplikacja:
Jak uruchomić projekt ze źródeł:
Do uruchomienia projektu ze źródeł należy mieć zainstalowane:
javę 11 (sudo apt install openjdk-11-jdk)
javafx 11 (sudo apt install openjfx)
maven (sudo apt install maven).

przed uruchomieniem projektu należy skonfigurować połączenie z bazą danych w pliku dbconnection.json - należy wprowadzić nazwę bazy danych, w której został uruchomiony dziennik.sql, nazwa użytkownika mającego prawa do tej bazy i hasło do niego.

Następnie do uruchomienia projektu wystarczy komenda:
mvn compile exec:java

Aby się zalogować należy wpisać w polu:
- email - email dowolnej osoby z bazy danych
- password - email podany w polu 'email' z literami w odwrotnej kolejności
Przykładowe dane do logowania:
Uczniowie:
    Shad@eric.io    oi.cire@dahS
    Mark@jace.ca    ac.ecaj@kraM
Nauczyciele:
    kazik123@op.pl    lp.po@321kizak
    Sadye@niko.biz    zib.okin@eydaS
Opiekunowie:
    Brad@luna.io    oi.anul@darB    - 1 podopieczny
    Fletcher_Legros@aryanna.net    ten.annayra@sorgeL_rehctelF    - 2 podopiecznych






