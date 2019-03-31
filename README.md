
# MRKLEE

The **M**odern **R**ight-re**K**ursive **L**anguage for **E**veryday **E**xpressions is a fun, musically-inspired, 
dynamically-typed programming language with limited functionality. Check it out!

*Written and designed by Hunter James*

## Getting Started

This directory includes several example tests to demonstrate different capabilities of MRKLEE:
* *error1.mrk* - results in an intended SYNTAX error
* *error2.mrk* - results in an intended SYNTAX error
* *error3.mrk* - results in an intended SYNTAX error
* *error4.mrk* - results in an intended SEMANTIC error
* *error5.mrk* - results in an intended SEMANTIC error
* *arrays.mrk*
* *conditionals.mrk*
* *recursion.mrk*
* *iteration.mrk*
* *functions.mrk* - function passing, returns, and nested functions
* *lambda.mrk [COMING SOON]* 
* *objects.mrk [COMING SOON]* - get and update field, method call
* *problem.mrk [COMING SOON]* - read and sum integers from a file

All example files are able to be printed and run unless otherwise stated.

### Prerequisites

Java version 7.0 or later. To check your version, use the command:

```
java -version
```
### Style Guidelines
_MRKLEE_ has been written with the musician in mind. Some important syntactical choices have been made as a result of 
this, notably:
* _funky_ : precedes all function definitions
* _record_ : indicates the return value of a function
* _yell_ : produces output to standard I/O
* _!_ : is used to distinguish between lines
* _setlist_ : precedes all array definitions

Also note that conditional statements do not use parenthesis, and rather have conditions bounded by 
keywords rather than punctuation. Options for conditionals are  :
* _if [...] go_
* _or if [...] go_
* _ifnone go_


### Testing

*NOTE:* All test files must end with a newline.

To run a pre-existing test:
 
 Use _make_ alongside the test name to **print** the testing code.
```
make conditionals
make error2
```

Use _make_ alongside the test name + 'x' to **run** the testing code.
```
make conditionalsx
make error2x
```


Repeat for as many tests as desired.

To run a test with custom input, enter the command "mrklee" and the file name. If the file is not in the working directory, include the file path.

```
mrklee other_test.mrk
```

This will run the test and report any syntax or semantic errors.

## Versioning

I am using [GitHub](https://github.com/hunterriffic) for version control. The *mrklee* repository is currently private, 
but will be made public after its release in the spring of 2019.

## Authors

* **Hunter James** - *Design, Implementation & Testing* - [hunterriffic](https://github.com/hunterriffic)

## Acknowledgments

* **Dr. John Lusth** - *Instruction & Inspiration* - [beastie](http://beastie.cs.ua.edu)

