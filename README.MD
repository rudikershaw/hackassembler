# HackAssembler
This is an example Java implementation of a command-line Hack Assembler for the Hack platform in the 'Elements of Computing Systems - Nand to Tetris Companion' book and course. This implementation is built to fulfill the specification set down in Chapter 6 and passes the tests described at the end of the chapter. It will assemble, to binary code, the files provided with Chapter 6 exactly as the provided stock assembler does.
## Usage
After compiling and building the HackAssembler module into a .jar file, simply run the following command in the command line.
```bash
    # java -jar [Path to your HackAssembler.jar] [Path to a .asm file].
    java -jar HackAssembler.jar /path/to/your/file.asm;
```
The HackAssembler should provide you with basic feedback if anything goes wrong, and will notify you on the command line when the program has finished assembling.

___

I understand that the Nand 2 Tetris course is run at numerous universities. This implementation is for you to use however you like, but please do not use this to skip writing your own implementation for a course. That would be dishonest and the real understanding comes from implementing it yourself.