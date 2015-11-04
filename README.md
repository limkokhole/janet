
# Java Native Extensions (JANET)



## Overview

JANET allows you to use arbitrary native code C and C++ libraries in your Java
applications, without the hassle that comes with Java Native Interface (JNI)
or Swig. If you have ever used JNI or Swig, you know how error-prone and
complex they can be. JANET, on the other hand, allows you to embed native
code directly in your Java files. From that native code, you can access Java
fields, call Java methods, throw Java exceptions, synchronize on Java
monitors, and do pretty much anything else that you can normally do via JNI.
Unlike JNI, however, you do all that using the familiar Java syntax.
JANET then does the hard work of generating JNI interface code for you,
taking care of hairy issues such as resolving signatures and references,
checking for exceptions, converting strings, pinning arrays, reference
counting, etc.

## Licensing

JANET is distributed under MPL 2.0.

Files generated by JANET translator (on the basis of your input files) are
yours. I only ask that you acknowledge the use of JANET in your documentation.

## Installation

If you downloaded a source release, unarchive and run `ant`. If you downloaded
a binary release, simply unarchive.

## Usage

Run `janet.jar [options] <source files>` to process `.janet` input files and
generate `java` and native files that contain the JNI binding. Then, compile
the native files into a dynamic library.

### Hello World example

In the JANET root directory, Create a file `Test.janet` with the following
contents:

```Java
public class Test {
    public static void main(String[] args) {
        hello("World");
    }
    
    public static native "C" void hello(String msg) {
        printf("Hello %s!\n", `#&msg`);
        `System.out.println("Hello via JNI")`;
    }
}

```

Now run:

    $ ./janet.jar Test.janet
    $ javac Test.java

This should have generated the JNI bindings. Now to the hard part, which is
to build the bindings into a dynamically linked library:

    $ JAVA_HOME=`realpath \`which javac\` | sed 's/\/bin\/javac//'`; \
      PLATFORM=`basename \`find ${JAVA_HOME}/include/* -type d\``; \
      gcc -shared -fPIC -ansi \
          -I${JAVA_HOME}/include -I${JAVA_HOME}/include/${PLATFORM} \
          -Inative/c/include \
          native/c/janet.c Test.c TestImpl.c \
          -o libTest.so

Now let's run it:

    $ java -Djava.library.path=. Test

And you should see:

    Hello World!
    Hello via JNI

### More examples

See the `examples` subdirectory and its makefile.

## Documentation

See the [white paper](http://janet-project.sourceforge.net/papers/janet.pdf).

## Supported syntax

JANET allows for embedding native code inside Java source files. That embedded
native code may in turn contain embedded Java statements and expressions,
later translated by JANET into JNI function invocations. Ideally, any valid
Java expression should be recognized by the translator. However, it is not yet
the case - most of the expression types are recognized and properly handled,
but some of more sophisticated are not. The full list of Java expressions and
statements supported by this version of JANET is given below (with Java
Language Specification references in parentheses):

* Blocks [14.2],
* Local Variable Declaration Statements [14.4], except for those containing
  array initializers [10.6],
* Expression Statements [14.8], for all kind of recognized expressions,
* The `return` Statement [14.16], preserving the semantics of finally clauses,
* The `throw` Statement [14.17],
* The `synchronized` Statement [14.18],
* The `try` Statement [14.19],
* Lexical Literals [15.8.1],
* `this` [15.8.3],
* Parenthesized Expressions [15.8.5],
* Class Instance Creation Expressions [15.9] for instantiation of non-inner
  classes, and excluding Anonymous Class Declarations [15.9.5],
* Array Creation Expressions [15.10] without array initializers [10.6],
* Field Access Expressions [15.11] for fields of non-inner classes,
* Method Invocation Expressions [15.12] for methods of non-inner classes,
* Array Access Expressions [15.13] for arrays of primitive types and objects of
  non-inner classes,
* Cast Expressions [15.16] for casting to primitive types and non-inner classes,
* Multiplication [15.17.1] and Division Operators `*`, `/` [15.17.2],
* Additive Operators `+`, `-` for Numeric Types [15.18.2],
* Relational Operators `<`, `>`, `<=`, `>=`, `instanceof` [15.20],
* Equality Operators `==`, `!=` [15.21],
* Simple Assignment Operator [15.26.1].

In addition, JANET introduces pointer-fetch operators (& and #&) applicable to
arrays and strings, and native-to-Java string conversion operator $(...).
Consult the documentation to learn more about that features.

### Unsupported syntax

Limitations of the current version of JANET translator include:

* Lack of support for embedding native languages other than C. Note, however,
  that JANET introduces no syntactic restrictions on the embedded C code,
  because it does not perform its semantic analysis beyond simple block and
  comment detection. Therefore, your C code can still invoke non-C routines
  using common linking techniques.
* Native code can only appear in, and refer to, top-level (non-inner) classes.
* Janet files don't support generics. (This is usually not a problem, because
  the Janet files tend to be thin wrappers around native libraries).
* Certain kinds of Java expressions may not be embedded into native code. The 
  detailed list is given below.
* JANET translator does not recover from errors. The translation stops on the
  first parse or compile error encountered so no more than one error per a
  compilation run is reported.
  
Java expression types that may not currently be embedded into native code
include:

* Class Literals [15.8.2],
* Postfix Expressions `++`, `--` [15.14],
* Unary Operators `++`, `--`, `+`, `-`, `~`, `!` [15.15],
* Remainder Operator `%` [15.17.3],
* String Concatenation Operator `+` [15.18.1],
* Shift Operators `<<`, `>>`, `>>>` [15.19],
* Bitwise and Logical Operators `&`, `^`, `|` [15.22],
* Conditional-And Operator `&&` [15.23],
* Conditional-Or Operator `||` [15.24],
* Conditional Operator `?:` [15.25],
* Compound Assignment Operators `*=`, `/=`, `%=`, `+=`, `-=`, `<<=`, `>>=`,
  `>>>=`, `&`, `^=`, `|=`),
* Array initializers [10.6], for both Local Variable Declaration Statements
  [14.4] and Array Creation Expressions [15.10],
* Expressions related to inner classes: Qualified this [15.8.4], Class Instance
  Creation Expressions [15.9], Field Access Expressions [15.11], Method
  Invocation Expressions [15.12], Array Access Expressions [15.13], and Cast
  Expressions [15.16].

Most of these are easy to substitute by other constructs. For instance:

* Appropriate native operators may be used instead of Java operators.
* String concatenation may be realized with the expression 
  `new StringBuffer().append(...).....toString()`.
* Compound assignment of the form `x op= y` may be substituted by the
  operation `x = x op y`.
* Array initialization may be delegated to a private Java method invoked
  through callback.
* Lack of support for inner classes can be worked around by limiting scope
  of Janet files. Note that it is never recommended to use native methods in
  anonymous inner classes [15.9.5], since the internal naming convention for
  anonymous classes is not defined in Java Language or Virtual Machine
  specifications.

## Change Log

### Version 1.1

* Invocation syntax changed to be more javac-alike, and more user-friendly
  in general.
* It is now possible to specify -classpath and -sourcepath as command-line
  parameters, allowing JANET to properly process sources dependent on external
  classes.
* It is now possible to specify names of dynamic libraries. The libraries may
  be assigned to individual classes, or to groups of classes.
* Output generation for file hierarchies now follows javac semantics. By
  default, output files are generated in the same directories as source files.
  This can be overridden by specifying the output directory; however, JANET
  will still maintain source hierarchy and it will create appropriate
  subdirectories as needed.

### Version 1.0

Initial release


## Acknowledgements

JANET has originated as my
[Master's thesis](http://janet-project.sourceforge.net/papers/janet_msc.pdf)
project in the Institute of Computer
Science at UMM University, Kraków, Poland. I developed it further during my
research scholarship at Emory University, Atlanta.

I wish to thank:

* Dr Marian Bubak (UMM), my Master Thesis supervisor, for his feedback that has
  significantly shaped JANET
* Dr Piotr Łuszczek, currently at University of Tennessee, for numerous
  valuable suggestions
* Prof Vaidy Sunderam (Emory), for supporting the research and advising on
  development directions

JANET parsers were created using the parser generator for Java, written by
Dennis Heimbigner. The JB itself depends on GNU bison, and Jonathan Payne's
regular expression package.
