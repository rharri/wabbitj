## Description
A compiler for the [Wabbit](https://www.dabeaz.com/compiler.html) language. 

This is not meant to be a full implementation of the language, instead the goal of this project is to explore compiler design and software design in general.

## Setup
A JDK installation is required for development and testing. 

JDK version >= 17 is required.

```
$ git clone https://github.com/rharri/wabbitj.git

$ cd wabbitj/

$ ./mvnw package
```

## Usage
Running a Wabbit program using the interpreter:
```
$ java -jar target/wabbitj-0.0.1.jar examples/simple.wb
```

## Technologies
- Java
- LLVM
- C

## Bugs
There are bugs :)

## License
See LICENSE

## Thanks
This project was made possible by David Beazley's creation of Wabbit and his wonderful [compiler course](https://www.dabeaz.com/compiler.html).

If you are interested in learning about compilers and want to hack on one, I highly recommend taking David's course.