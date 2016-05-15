# mars-rovers

A Clojure library designed to ... pilot rovers on a simplified Mars?

## Usage

Running it:

```
$ lein uberjar
<compiler output goes here>
$ cat instructions.txt
Plateau:5 5
Rover1 Landing:1 2 N
Rover1 Instructions:LMLMLMLMM
Rover2 Landing:3 3 E
Rover2 Instructions:MMRMMRMRRM
$ cat instructions.txt | java -jar path/to/mars-rovers-0.1.0-SNAPSHOT-standalone.jar
Rover1:1 3 N
Rover2:5 1 E
$
```

Testing it:

```
$ lein test

lein test mars-rovers.core-test
{:result true, :num-tests 100, :seed 1463283520351, :test-var "read-instructions-can-handle-it"}
{:result true, :num-tests 100, :seed 1463283521886, :test-var "no-series-of-instructions-moves-a-rover-out-of-the-plateau"}
{:result true, :num-tests 100, :seed 1463283521911, :test-var "landing+instruction-pair-contributs-name-and-starting-state"}
{:result true, :num-tests 100, :seed 1463283521959, :test-var "turn-is-the-same-as-calling-left-or-right"}
{:result true, :num-tests 100, :seed 1463283521975, :test-var "left-right-is-commutative"}
{:result true, :num-tests 100, :seed 1463283521982, :test-var "left-right-is-identity"}

Ran 10 tests containing 19 assertions.
0 failures, 0 errors.
$
```

## License

Copyright © 2016 inaimathi

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
