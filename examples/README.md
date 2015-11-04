
This directory contains some illustrative JANET examples.

## UNIX users

Try this first:

    $ make
    $ ./run

You should see some stdout from the examples. If this fails, you may need to
tweak the makefile to meet your operating system requirements.


## Win32 users

Unfortunately, there are no build macros provided for Win32. In order
to build the library please do the following:

1. run JANET on the `*.janet` files,
2. use javac to compile all `*.java` source files.
3. Build `examples.dll` out of C files generated by JANET in step 1.
   You may want to use some IDE, e.g. MS Visual C++.
   DON'T FORGET TO INCLUDE `%JANET_DIR%\native\c\include\janet.c` FILE
   IN THE LIBRARY.

`%JANET_DIR%` is the directory where you installed JANET.

After you built the library you need to make it accessible through the
`%PATH%` environment variable.

To start examples, go to the `%JANET_DIR%` and write:

    java examples.Main