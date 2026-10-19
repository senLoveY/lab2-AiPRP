# Лабораторная работа №2. Работа на основе сокетных соединений

Вариант 1.

| Папка | Задание | Главный класс |
|---|---|---|
| `src/task1` | Общее 1: приложение из методички (поток-сервер `Account`, поток-клиент `clientThread`), клиент запускается щелчком мыши в окне | `task1.serv` |
| `src/task2` | Общее 2: два клиента в отдельных потоках работают с одним счетом (оба запускаются щелчком мыши) | `task2.serv` |
| `src/task3` | Общее 3: визуальный интерфейс — кнопка запускает клиента, состояние счета в текстовом поле | `task3.serv` |
| `variant1/ServerProject` | Вариант 1: сервер, отдельный проект, без потоков | `ServerApp` |
| `variant1/ClientProject` | Вариант 1: клиент, отдельный проект, без потоков | `ClientApp` |
| `bank/BankServer` | Банк: сервер с окном, хранит общий счет | `BankServer` |
| `bank/BankClient` | Банк: клиент с окном — ввод IP сервера, «Положить», «Снять», «Обновить» | `BankClient` |

Все программы используют порт 3001, поэтому одновременно запускать можно только одну из них
(для варианта 1 — сервер и клиент вместе).

## Запуск из терминала

```sh
# общие задания (N = 1, 2, 3)
javac -d out src/task*/serv.java
java -cp out taskN.serv

# вариант 1: сначала сервер, затем клиент (в другом терминале, можно несколько раз)
javac -d variant1/ServerProject/out variant1/ServerProject/src/ServerApp.java
java -cp variant1/ServerProject/out ServerApp

javac -d variant1/ClientProject/out variant1/ClientProject/src/ClientApp.java
java -cp variant1/ClientProject/out ClientApp
```

## Банк на нескольких компьютерах

Сервер запускается на одном компьютере и показывает в окне свои IP-адреса. Каждый клиент
при запуске вводит один из этих адресов и нажимает «Подключиться», после чего может класть
деньги на общий счет и снимать их (снять больше, чем есть на счете, нельзя).

```sh
javac -d bank/BankServer/out bank/BankServer/src/BankServer.java
java -cp bank/BankServer/out BankServer

javac -d bank/BankClient/out bank/BankClient/src/BankClient.java
java -cp bank/BankClient/out BankClient
```

### Apache NetBeans

`bank/BankServer` и `bank/BankClient` — Maven-проекты, NetBeans открывает их без настройки:
**File → Open Project…** → выбрать папку `bank/BankClient` (или `bank/BankServer`, у нее значок проекта) →
**Run Project** (`F6`). При первом запуске NetBeans скачает плагины Maven, нужен интернет.

Клиент есть также в виде обычного проекта NetBeans (Java with Ant), без Maven и без интернета:
**File → Open Project…** → `bank/BankClientNetBeans` → **Run Project** (`F6`). Исходник он берет
из `bank/BankClient/src`, то есть код у обоих проектов общий.

Протокол — одна строка-команда на соединение: `BALANCE`, `DEPOSIT <сумма>`, `WITHDRAW <сумма>`;
ответ — `OK <баланс>`, `NOMONEY <баланс>` (недостаточно средств) или `ERROR <баланс>`.

В IntelliJ IDEA для варианта 1 папки `variant1/ServerProject` и `variant1/ClientProject`
открываются как два отдельных проекта.
