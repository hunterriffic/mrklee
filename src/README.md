
# mrklee

The **M**odern **R**ight-re**K**ursive **L**anguage for **E**veryday **E**xpressions is a fun, musically-inspired, 
dynamically-typed programming language with limited functionality. Check it out!

*Written and designed by Hunter James*

## Getting Started

### Prerequisites

Java version 7.0 or later. To check your version, use the command:

```
java -version
```

### Style Guidelines
_mrklee_ has been written with the musician in mind. Some important syntactical choices have been made as a result of 
this, notably:
* _funky_ : precedes all function definitions
* _record_ : indicates the return value of a function
* _yell_ : produces output to standard I/O
* _!_ : is used to distinguish between lines
* _setlist_ : precedes all array definitions


### Comments
To write a single-line comment in a _mrklee_ file, simply precede the line with `#` (hash symbol). 

`# This is a comment` 

Nothing in the comment will be evaluated. Block comments do not yet exist in _mrklee_.

### Types
_mrklee_ is a dynamically-typed language and does not allow for explicit variable typing. 

The types available to the interpreter are:
* String
* Integer
* Real

### Operators
Available operators in _mrklee_ include `+`, `-`, `*`, `/`, and `%`.

Values can be compared using `<`, `<=`, `>`, `>=`, `==` and `!=`.

### Arrays
All _mrklee_ arrays have a constant access time. 

Arrays are defined using the keyword `setlist` and the size: `setlist s[10]` (where size is 10).

Values stored in arrays are accessed using their index and can be treated as any other variable: `var x = s[4]` 
(where the value is at index 4).

### Conditionals
Conditional statements in _mrklee_ do not use parenthesis- their conditions are bounded by 
keywords rather than punctuation. Options for conditionals are  :
* `if [...] go {...}`
* `or if [...] go {...}`
* `ifnone go {...}`

All conditional blocks must begin with an `if [...] go` statement.

An example of a valid combination of conditional statements:
```
if x == 1 go
    {
    aFunction(x)!
    }
or if x == 5 go
    {
    bFunction(x)!
    }
ifnone go
    {
    cFunction(x)!
    }
```

#### Iteration
A `while` loop is another available conditional in _mrklee_, and will execute until its condition is true.

An example of a valid `while` loop:

```
while x < 10
    {
    yell(x)!
    x = x + 1!
    }
```

### Output
To print to the console using _mrklee_, use the `yell` keyword followed by the variable or text to be outputted. 
All plaintext output must be bounded by `"..."`.

An example of valid output statements:
```
yell("Hello world!")!
var x = 3!
yell(x)!
``` 

### Anonymous Functions (Lambda)
Lambda functions are not yet fully implemented in _mrklee_.

## Testing
### Directory Structure

This directory includes several example tests to demonstrate different capabilities of _mrklee_:
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

### Testing

*NOTE:* All test files must end with a newline.

To run a pre-existing test:
 
 Use `make` alongside the test name to **print** the testing code.
```
make conditionals
make error2
```

Use `make` alongside the `[test name]x` to **run** the testing code.
```
make conditionalsx
make error2x
```


Repeat for as many tests as desired.

To run a test with custom input, enter the command "mrklee" and the file name. If the file is not located in the 
working directory, include the file path.

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

