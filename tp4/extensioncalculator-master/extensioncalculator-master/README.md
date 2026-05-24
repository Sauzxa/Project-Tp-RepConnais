# An automated extension calculator for default logic

Default logic is a non-monotonic logic with default rules that extend incomplete information with plausible, but not necessarily true conclusions. The operational semantics of default logic is defined in terms of default extensions. All extensions of a default theory can be found by drawing a process tree. This project is about making an automated extension calculator using process trees for default theories.

## Getting Started
To use the program, use Maven to build and then run.

## Formula Syntax
Inside the program logical formulas can be used. The syntax for these formulas is as follows:

```a       \\Propositional atom
a | b   \\Disjunction
a & b   \\Conjunction
~a      \\Negation
```

## Built With

* [Sat4j](http://www.sat4j.org) - SAT Solver for formulas
* [ANTLR](http://www.antlr.org) - Formula parsing
* [Maven](https://maven.apache.org/) - Dependency Management
* [IntelliJ IDEA](https://www.jetbrains.com/idea/) - Programming Environment

## Authors

* **Marco Breemhaar** - *Initial work* - [mbreemhaar](https://github.com/mbreemhaar)

## License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.
