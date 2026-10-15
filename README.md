# Calculated Combat Prototype

This repository contains the first running slice of Calculated Combat, a
text-based tournament fighting game. The prototype supports a player, a
linked-list-backed shop, equipment purchases, a fighter queue, and an
automatic match.

## Compile and run

In Command Prompt or Powershell, run this to navigate to the folder containing the program:
```
cd "C:\InsertFolderNameHere\KeepDoingThis\UntilYouGet\ToTheFolder\ThatStoresTheProject"
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

Compile and run the game in Powershell:
```powershell
$sources = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }
javac -d bin $sources
java -cp bin main.GameDriver
```

Compile and run the JUnit tests in Powershell:

```powershell
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
- `TournamentRound` calculates a bounded win probability from Aggro, Control,
  and Midrange counter relationships, then resolves a match.
- `GameDriver` provides a retrying menu for status, buying a sword, fighting,
  and exiting.
- `GameSave` writes a small save marker to a caller-selected path.

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

The `LinkedChain<T>` was changed to `CustomLinkedList<T>`. It was changed from singly-linked to doubly-linked, with more of the functionality of a normal java.util LinkedList. Application code accesses it only through public methods such as `add`, `get`, `remove`, `removeFirst`,
`isEmpty`, and `size`. `Shop` uses `CustomLinkedList<Item>` and `Tournament`
uses `CustomLinkedList<GameCharacter>`; there are no type-specific copies of
the data structure.

### Unknowns

The prototype resolved a first-pass counter-based probability algorithm and
made it testable with a seeded random source. The exact balance of stats,
prices, gold rewards, queue progression, and bracket advancement remains open
until the full game loop exists. The complete double-elimination bracket and
adaptive opponents are also still open.

### What does not work yet

The prototype does not yet have the eight-player bracket, grand-final rules,
win/loss run termination, round gold rewards, randomized opponent pools,
level-unlocked gear, save/load of complete character state, or background
tournaments. The current tests cover every public method in the implemented
application classes, while future features will require additional tests.

## Next Steps

1. **Team- tournament progression:** implement the eight-player
   double-elimination bracket and queue replacement rules.
2. **Team- game state:** add total wins/losses, round rewards, level
   progression, and the five-win/ten-loss end condition.
3. **Team- shop:** add unlock levels, sell/equip commands, and enforce the
   final catalog rules through the same linked-list API.
4. **Team- driver:** expand the current validated menu with tournament
   progression and complete state displays.
5. **Team- stretch goals:** attempt adaptive opponents; defer full save/load,
   random stores, background tournaments, and same-round loss queues unless
   the MVP is stable.
6. **Team- tests:** add bracket order tests, queue-after-win/loss tests,
   rewards and run-ending tests, level unlock tests, and interactive bad-input
   tests for every new public method.

## Attribution

| File | Contributor | Work |
| --- | --- | --- |
| `src/characters/Item.java` | Brian, Ben, Leo | Revised and tested with GenAI assistance |
| `src/characters/GameCharacter.java` | Brian, Leo | Revised and tested with GenAI assistance |
| `src/characters/Player.java` | Brian, Leo | Revised and tested with GenAI assistance |
| `src/shop/Shop.java` | Brian, Ben | Revised and tested with GenAI assistance |
| `src/tournament/Tournament.java` | Brian, David | Revised with GenAI assistance |
| `src/tournament/TournamentRound.java` | Brian | Revised and tested with GenAI assistance |
| `src/main/GameDriver.java` | Brian, David, Ben | Revised and tested with GenAI assistance |
| `src/gamesaves/GameSave.java` | David, Ben | Revised and tested with GenAI assistance |
| `tests/ItemTest.java` | David, Brian, Ben | Revised and tested with GenAI assistance |
| `tests/GameCharacterTest.java` | Leo, Brian | Wrote and tested with GenAI assistance |
| `tests/GameDriverTest.java` | Brian | Wrote and tested with GenAI assistance |
| `tests/GameSaveTest.java` | David, Brian | Wrote and tested with GenAI assistance |
| `tests/PlayerTest.java` | Ben, Brian | Wrote and tested with GenAI assistance |
| `tests/ShopTest.java` | Ben, Brian, David | Wrote and tested with GenAI assistance |
| `tests/TournamentTest.java` | Brian, Leo | Wrote and tested with GenAI assistance |

GenAI was used to help inspect the existing codebase, identify compile blockers, draft edge-case tests,
review public-method coverage, and review the README. All generated changes
and tests were read, adapted, and run by the contributor. The completed
prototype test suite contains 20 passing tests.
