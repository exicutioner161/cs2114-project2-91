# Calculated Combat Prototype

This is a working MVP slice of Calculated Combat, a text-based tournament
fighting game. It supports equipment purchases, four level-scaled shops,
named save/load, and an eight-fighter double-elimination tournament that runs
one match at a time and prints its bracket after every battle.

## Compile and run

In **Command Prompt**, first navigate to the repository root:
```cmd
cd /d "C:\Users\brian\Desktop\stuff\code\cs2114-project2-91"
```

In **PowerShell**, use:
```powershell
Set-Location "C:\Users\brian\Desktop\stuff\code\cs2114-project2-91"
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

## Prototype slice

- `Player` owns gold and five equipment slots.
- `Shop` stores `Item` offers in `CustomLinkedList<Item>`, including duplicate
  offers with different prices.
- `Tournament` stores `GameCharacter` entrants in the same generic
  `CustomLinkedList<T>` implementation and runs the winners and losers
  brackets through the public list API.
- `ShopProgression` stores level-specific `Shop` objects in that same generic
  `CustomLinkedList<T>`, so three application element types use one chain.
  Levels 1 through 4 each contain 25 offers: five unique named items in each
  of the five equipment slots.
- `TournamentRound` calculates a bounded win probability from Aggro, Control,
  and Midrange counter relationships, then resolves a match.
- `GameDriver` provides a retrying menu for status, buying level gear, fighting,
  saving, loading, and exiting, records rewards and run termination, and
  prints the full ASCII bracket after every completed battle.
- `GameSave` writes and validates named player progress, including equipment,
  tournament wins, and tournament losses.
- `TournamentQueue` preserves waiting opponents after a loss and repopulates
  stronger opponents after a win.
- `AdaptiveOpponentFactory`, `BackgroundTournament`, and
  `SameRoundLossQueue` implement the three stretch goals from the scope.

Invalid actions return `false` or `null` without changing state. Invalid
constructor values throw `IllegalArgumentException`, so bad input is rejected
at the validation boundary rather than crashing later in the game.

## Reflection

### Changes from the specification

The specification described a complete eight-character double-elimination
tournament, changing queues, level-based shop unlocks, and a full interactive
menu. The implementation now has an explicit eight-fighter bracket with
winner/loser transitions, a single no-reset grand final, one-match-at-a-time
driver execution, rewards, level advancement, and five-win/ten-loss
termination. Validation remains at application boundaries so state
transitions can be tested independently from console I/O.

The specification left the probability formula open. The implementation compares
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
| `GameSave.save`, `load` | O(n) | n is the five-slot inventory/save text |

The original specification estimated catalog and queue scans as O(n), fixed
equipment as O(1), and catalog formatting as O(n). The implementation matches
those estimates except that level-shop lookup is a direct indexed lookup and
the save file is bounded by five equipment slots.

### Unknowns

The implementation resolved a first-pass counter-based probability algorithm,
named progress serialization, level-scaled randomized store generation, the
double-elimination bracket lifecycle, and deterministic seeded outcomes.
The shop now uses a fixed name catalog and randomized stats/prices with
stronger level ranges. Exact balance of stats, prices, and rewards remains a
tuning concern.

### What does not work yet

Mid-tournament bracket snapshots are not implemented. The save file persists
the player progression state and restarts a fresh queue after loading. The
implemented queue and stretch-goal services are covered by focused tests. The
current suite includes empty, missing, malformed, invalid-input, deterministic,
and full-bracket cases.

## Next Steps

1. **Brian - save state:** decide whether mid-tournament bracket snapshots are
   required and add `GameSaveTest` coverage if they are.
2. **Team - balance:** tune combat probabilities, shop prices, and rewards.
3. **Team - maintenance:** keep save/load, queue progression, adaptive
   opponents, background tournaments, and same-round loss queues integrated.

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
| `src/gamesaves/GameSave.java` | David, Ben | Revised and tested with GenAI assistance |
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
were then adapted and run by Brian. The completed prototype test suite
contains 51 passing tests.
