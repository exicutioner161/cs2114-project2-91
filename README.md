# Calculated Combat Prototype

This repository contains the first running slice of Calculated Combat, a
text-based tournament fighting game. The prototype supports a player, a
linked-list-backed shop, equipment purchases, a fighter queue, and an
automatic match.

## Compile and run

In **Command Prompt**, first navigate to the repository root:
```
cd /d "C:\Users\brian\Desktop\stuff\code\cs2114-project2-91"
```

In **PowerShell**, use:
```powershell
Set-Location "C:\Users\brian\Desktop\stuff\code\cs2114-project2-91"
```

Compile and run the game in Command Prompt:
```
if not exist bin mkdir bin
dir /s /b src\*.java > sources.txt
javac -d bin @sources.txt
del sources.txt
java -cp bin main.GameDriver
```

Compile and run the JUnit tests in Command Prompt:
```
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

## Prototype slice

- `Player` owns gold and five equipment slots.
- `Shop` stores `Item` offers in `CustomLinkedList<Item>`, including duplicate
  offers with different prices.
- `Tournament` stores `GameCharacter` opponents in the same generic
  `CustomLinkedList<T>` implementation.
- `ShopProgression` stores level-specific `Shop` objects in that same generic
  `CustomLinkedList<T>`, so three application element types use one chain.
- `TournamentRound` calculates a bounded win probability from Aggro, Control,
  and Midrange counter relationships, then resolves a match.
- `GameDriver` provides a retrying menu for status, buying level gear, fighting,
  saving, loading, and exiting.
- `GameSave` writes and validates named player progress, including equipment.

Invalid actions return `false` or `null` without changing state. Invalid
constructor values throw `IllegalArgumentException`, so bad input is rejected
at the validation boundary rather than crashing later in the game.

## Reflection

### Changes from the specification

The specification described a complete eight-character double-elimination
tournament, queues that change after wins and losses, level-based shop unlocks,
and a full interactive menu. This prototype intentionally implements a smaller
vertical slice: a player can buy one catalog item, opponents can be queued, and
one automatic fight can run. The driver now wraps this slice in a small menu
and retries invalid input. Application methods remain direct Java APIs so the
state transitions can be tested independently from console I/O.

The specification left the probability formula open. The prototype compares
each fighter's Aggro against the opponent's Control, Control against Midrange,
and Midrange against Aggro, combines those differences, and maps the result
through a logistic function to keep the probability between zero and one.
The match accepts an injectable `Random` source so tests can be repeatable.

### Changes to LinkedChain

The provided `LinkedChain<T>` was changed by the team to
`CustomLinkedList<T>`, and it was changed from singly-linked to doubly-linked
with more of the functionality of a normal `java.util.LinkedList`. Application
code accesses it only through public methods such as `add`, `get`, `remove`,
`removeFirst`, `isEmpty`, and `size`; no application class accesses nodes or
chain fields. `Shop` uses `CustomLinkedList<Item>`, `Tournament` uses
`CustomLinkedList<GameCharacter>`, and `ShopProgression` uses
`CustomLinkedList<Shop>`. There are no type-specific copies of the data
structure.

### Big-O reflection

The application methods use the chain through its public API, so their costs
match the original estimates where applicable:

| Method | Cost | Reason |
| --- | --- | --- |
| `Shop.addItem` | O(1) | Append through `add` |
| `Shop.findItem`, `removeItem` | O(n) | Linear catalog scan |
| `Player.buy`, `sell` | O(1) | Fixed five-slot inventory |
| `Tournament.addFighter`, `removeNextFighter` | O(1) | Append/remove front |
| `Tournament.runMatch` | O(1) plus fight calculation | One queued opponent |
| `ShopProgression.getShop` | O(n) | `CustomLinkedList.get(index)` scans from an end |
| `GameSave.save`, `load` | O(i) | i is the five-slot inventory/save text |

The original specification estimated catalog and queue scans as O(n), fixed
equipment as O(1), and catalog formatting as O(n). The implementation matches
those estimates except that level-shop lookup is a direct indexed lookup and
the save file is bounded by five equipment slots.

### Unknowns

The prototype resolved a first-pass counter-based probability algorithm and
made it testable with a seeded random source. It also resolved named progress
serialization and level-scaled randomized store generation. The exact balance
of stats, prices, gold rewards, queue progression, and bracket advancement
remains open until the full game loop exists. The complete double-elimination
bracket and adaptive opponents are still open.

### What does not work yet

The prototype does not yet have the eight-player bracket, grand-final rules,
win/loss run termination, round gold rewards, randomized opponent pools, or
background tournaments. Save/load currently covers the player name, level,
gold, and five equipment slots; it is not a complete tournament snapshot.
The current tests cover every public method in the implemented application
classes, including empty and missing removal cases.

## Next Steps

1. **Team - tournament progression:** implement the eight-player
   double-elimination bracket and queue replacement rules.
2. **Team - game state:** add total wins/losses, round rewards, level
   progression, and the five-win/ten-loss end condition.
3. **Team - shop:** connect the committed level-scaled `ShopProgression`
   stretch goal to unlock rules and enforce the final catalog rules through
   the same linked-list API.
4. **Team - driver:** expand the current validated menu with tournament
   progression and complete state displays.
5. **Team - committed stretch goals:** save/load and random level-scaled
   stores are implemented and will be extended with complete tournament state.
6. **Team - dropped/deferred stretch goals:** adaptive opponents, background
   tournaments, and same-round loss queues are deferred.
7. **Team - tests:** add `TournamentBracketTest` for bracket order and
   queue-after-win/loss behavior, `ProgressionTest` for rewards and run-ending
   rules, and `DriverInputTest` for every new menu command.

## Attribution

| File | Contributor | Work |
| --- | --- | --- |
| `src/characters/Item.java` | Brian, Ben, Leo | Revised and tested with GenAI assistance |
| `src/characters/GameCharacter.java` | Brian, Leo | Revised and tested with GenAI assistance |
| `src/characters/Player.java` | Brian, Leo | Revised and tested with GenAI assistance |
| `src/shop/Shop.java` | Brian, Ben | Revised and tested with GenAI assistance |
| `src/shop/ShopProgression.java` | Brian | Wrote and tested with GenAI assistance |
| `src/tournament/Tournament.java` | Brian, David | Revised with GenAI assistance |
| `src/tournament/TournamentRound.java` | Brian | Revised and tested with GenAI assistance |
| `src/main/GameDriver.java` | Brian, David, Ben | Revised and tested with GenAI assistance |
| `src/gamesaves/GameSave.java` | David, Ben | Revised and tested with GenAI assistance |
| `tests/ItemTest.java` | David, Brian, Ben | Revised and tested with GenAI assistance |
| `tests/GameCharacterTest.java` | Leo, Brian | Wrote and tested with GenAI assistance |
| `tests/GameDriverTest.java` | Brian | Wrote and tested with GenAI assistance |
| `tests/GameSaveTest.java` | David, Brian | Wrote and tested with GenAI assistance |
| `tests/ShopProgressionTest.java` | Brian | Wrote and tested with GenAI assistance |
| `tests/PlayerTest.java` | Ben, Brian | Wrote and tested with GenAI assistance |
| `tests/ShopTest.java` | Ben, Brian, David | Wrote and tested with GenAI assistance |
| `tests/TournamentTest.java` | Brian, Leo | Wrote and tested with GenAI assistance |

GenAI was used to help inspect the existing codebase, identify compile blockers,
draft edge-case tests, review public-method coverage, propose the save/load and
scaled-shop designs, and review the README. The team selected the two
committed stretch goals after discussion; implementation details and tests
were then adapted and run by Brian. The completed prototype test suite
contains 25 passing tests.
