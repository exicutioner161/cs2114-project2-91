# Calculated Combat

Calculated Combat a text-based tournament
fighting game. It supports equipment purchases, five level-scaled shops,
named save/load, and an eight-fighter double-elimination tournament that runs
one match at a time and prints its bracket after every battle.

## Compile and run

In **Command Prompt**, first navigate to the repository root:
```cmd
cd /d "C:\Users\example\example2\"
```

In **PowerShell**, use:
```powershell
Set-Location "C:\Users\example\example2\"
```

Compile and run the game in Command Prompt:
```cmd
if not exist bin mkdir bin
dir /s /b src\*.java > sources.txt
javac -d bin @sources.txt
del sources.txt
java -cp bin main.GameDriver
```

Compile and run the JUnit tests in Command Prompt:
```cmd
if not exist bin mkdir bin
dir /s /b src\*.java > sources.txt
javac -d bin @sources.txt
del sources.txt

dir /s /b tests\*.java > tests.txt
javac -cp "lib\junit-platform-console-standalone-1.10.2.jar;bin" -d bin @tests.txt
del tests.txt

java -jar lib\junit-platform-console-standalone-1.10.2.jar execute --class-path bin --scan-class-path
```

The `bin` directory contains generated `.class` files. Recompile all source
files after source changes so those artifacts cannot become stale. Save/load
uses a private UTF-8 Base64 encoding for names and item names, and rejects
malformed records, duplicate equipment slots, and out-of-range slots without
replacing the current player state.

Compile and run the game in Powershell:
```powershell
New-Item -ItemType Directory -Force bin | Out-Null
$sources = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }
javac -d bin $sources
java -cp bin main.GameDriver
```

Compile and run the JUnit tests in Powershell:

```powershell
New-Item -ItemType Directory -Force bin | Out-Null
$sources = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }
javac -d bin $sources
$tests = Get-ChildItem -Recurse -Filter *.java tests | ForEach-Object { $_.FullName }
javac -cp "lib\junit-platform-console-standalone-1.10.2.jar;bin" -d bin $tests
java -jar lib\junit-platform-console-standalone-1.10.2.jar execute --class-path bin --scan-class-path
```

## System Diagram
<img width="1536" height="1024" alt="image" src="https://github.com/user-attachments/assets/e89fb6ed-11c8-40f0-a8b9-022299ba614e" />


## Attribution

| File | Contributor | Work |
| --- | --- | --- |
| `src/characters/Item.java` | Brian, Ben, Leo | Revised and tested with GenAI assistance |
| `src/characters/GameCharacter.java` | Brian, Leo | Revised and tested with GenAI assistance |
| `src/characters/Player.java` | Brian, Leo | Revised and tested with GenAI assistance |
| `src/shop/Shop.java` | Brian, Ben | Revised and tested with GenAI assistance |
| `src/shop/ShopProgression.java` | Brian | Wrote and tested with GenAI assistance |
| `src/tournament/Tournament.java` | Brian, David | Revised and tested with GenAI assistance |
| `src/tournament/TournamentQueue.java` | Brian | Wrote and tested with GenAI assistance |
| `src/tournament/AdaptiveOpponentFactory.java` | Brian | Wrote and tested with GenAI assistance |
| `src/tournament/BackgroundTournament.java` | Brian | Wrote and tested with GenAI assistance |
| `src/tournament/SameRoundLossQueue.java` | Brian | Wrote and tested with GenAI assistance |
| `src/tournament/TournamentRound.java` | Brian | Revised and tested with GenAI assistance |
| `src/main/GameDriver.java` | Brian, David, Ben | Revised and tested with GenAI assistance |
| `src/gamesaves/GameSave.java` | David, Ben, Brian | Revised and tested with GenAI assistance |
| `tests/ItemTest.java` | David, Brian, Ben | Revised and tested with GenAI assistance |
| `tests/GameCharacterTest.java` | Leo, Brian | Wrote and tested with GenAI assistance |
| `tests/GameDriverTest.java` | Brian | Wrote and tested with GenAI assistance |
| `tests/GameSaveTest.java` | David, Brian | Wrote and tested with GenAI assistance |
| `tests/ShopProgressionTest.java` | Brian | Wrote and tested with GenAI assistance |
| `tests/PlayerTest.java` | Ben, Brian | Wrote and tested with GenAI assistance |
| `tests/ShopTest.java` | Ben, Brian, David | Wrote and tested with GenAI assistance |
| `tests/TournamentTest.java` | Brian, Leo | Wrote and tested with GenAI assistance |
| `tests/TournamentQueueTest.java` | Brian | Wrote and tested with GenAI assistance |
| `tests/StretchGoalTest.java` | Brian | Wrote and tested with GenAI assistance |

GenAI was used to help inspect the existing codebase, identify compile blockers,
draft edge-case tests, review public-method coverage, propose the save/load and
scaled-shop designs, and review the README. The team selected the two
committed stretch goals after discussion; implementation details and tests
were then adapted and run by Brian. The completed prototype test suite currently contains 63 passing tests.
