# Dog Walk - Aplikacja mobilna do śledzenia aktywności Twojego psa

### Spis treści
* [Informacje](#informacje)
* [Zrzuty ekranu](#zrzuty-ekranu)
* [Struktura](#struktura)
* [Funkcjonalności](#funkcjonalności)
* [Biblioteki](#biblioteki)




### Informacje
Aplikacja mobilna na system Android,  napisana w języku Kotlin z wykorzystaniem wzorca MVVM. Dzięki aplikacji użytkownik może m.in. sledzić spacery psa, ustawiać przypomniennia, zapisywać podstawoe informacje o pupilu. Projekt wykonany na zaliczenie przedmiotu Aplikacje użytkowe.

### Zrzuty ekranu

![Tracking](https://github.com/jakubrzeznicki/Dog-Walk/blob/master/screenshots/first.png "Tracking")
![Profile](https://github.com/jakubrzeznicki/Dog-Walk/blob/master/screenshots/second.png "Profile")
![Notifications](https://github.com/jakubrzeznicki/Dog-Walk/blob/master/screenshots/third.png "Notifications")



### Funkcjonalności
Lista funkcjonalności jakie zapewnia aplikacja Dog Walk
* Podgląd spacerów, między innymi takich informacji jak dystans, czas, data oraz mapa przebytej drogi.
* Dodanie nowego spaceru, aplikacja śledzi nasz ruch na bieżąco. Uaktualnia pozycję na mapie, czas oraz dystans.
* Przechowywanie informacji o naszym psie, takich tak: Imie, Rasa, Data urodzenia, Płeć, Waga, Zdjęcie, Aktywności.
* Kalkulator kalori obliczający zapotrzebowanie w zależności od wieku, wagi oraz aktywności psa.
* Powiadomienia o wyjściu na spacer, od - do konkretnej godziny.
* Spersonalizowane powiadomienia na konkretny dzień.

### Struktura

Aplikacja posiada następująco strukturę:
1. **adapters**: Zawiera adaptery zapewniające wyświetlanie i działanie widoków, które są w Recyclerview.
2. **data/local**: Zawiera modele danych (encje) , DAO - Obiekty dostępu do danych, oraz klasę bazy danych.
4. **di**: Zawiera obiekty dependency injection dla całej aplikacji oraz servisu.
5. **other**: Zawiera pozostałe klasy/obiekty m.in stałe, konwertory.
6. **repositories**: Zawiera klase oraz interfejs repozytorium.
7. **service**: Zawiera klase TrackingService, obsługującą śledzenie użytkownika, uaktualnianie czasu oraz dystansu.
8. **ui**: Zawiera wszystkie aktywności i fragmenty aplikacji.



### Biblioteki

- Jetpack Compose
    - [androidx.room](https://developer.android.com/jetpack/androidx/releases/room) - version 2.2.6
    - [androidx.hilt](https://developer.android.com/jetpack/androidx/releases/hilt)- version 1.0.0-alpha02
    - [androidz.lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle)- version 2.2.0
    - [androidx.navigation](https://developer.android.com/jetpack/androidx/releases/navigation) - version 2.3.2
- [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android)- version 2.28-alpha
- [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines)- version 1.4.2
- [Glide](https://github.com/bumptech/glide)- version 4.11.0
- [EasyPermissions](https://github.com/googlesamples/easypermissions)- version 3.0.0
