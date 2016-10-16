package com.rudikershaw.assembler;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static com.rudikershaw.assembler.CommandType.L_COMMAND;

/** The main class for the HackAssembler. */
public class Main {

    /** Start here! */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("HackAssembler takes 1 argument. The name of the .asm file to convert to a .hack file.");
            System.out.println("Example below :");
            System.out.println("java HackAssembler example.asm");
        } else {
            final File f = new File(args[0]);
            if (!validateFile(f)) return;

            try {
                final StringBuilder binaryContent = new StringBuilder();
                final Map<String, Integer> labels = getLabels(f);

                try (final Parser parser = new Parser(f)) {
                    final Encoder encoder = new Encoder();
                    int variableCounter = 16;

                    while (parser.hasMoreCommands()) {
                        parser.advance();
                        switch(parser.commandType()){
                            case A_COMMAND: {
                                if (!parser.symbol().matches("^-?\\d+$") && !labels.containsKey(parser.symbol())) {
                                    labels.put(parser.symbol(), variableCounter++);
                                }
                                Integer labelValue = labels.get(parser.symbol());
                                if (labelValue == null) labelValue = Integer.parseInt(parser.symbol());
                                final String binary = Integer.toBinaryString(labelValue);
                                final String missingBits = new String(new char[15-binary.length()]).replace('\0','0');
                                binaryContent.append("0").append(missingBits).append(binary).append('\n');
                                break;
                            }
                            case C_COMMAND: {
                                final String compute = encoder.comp(parser.comp());
                                final String destination = encoder.dest(parser.dest());
                                final String jump = encoder.jump(parser.jump());
                                binaryContent.append("111").append(compute).append(destination).append(jump).append('\n');
                                break;
                            }
                        }
                    }

                    parser.close();
                    writeToHackFile(binaryContent.toString(), f);
                }
            } catch (IOException fnfe) {
                System.out.println("A problem occurred accessing '" + f.getPath() + "'.");
            }
        }
    }

    /** Check whether the file parsed as an argument is valid. */
    public static boolean validateFile(File f) {
        String extension = "";
        int i = f.getName().lastIndexOf('.');
        int p = Math.max(f.getName().lastIndexOf('/'), f.getName().lastIndexOf('\\'));

        if (i > p) {
            extension = f.getName().substring(i+1);
        }

        if (!f.exists()) {
            System.out.println("The file '" + f.getPath() + "' does not exist.");
            return false;
        } else if (!f.isFile()) {
            System.out.println("HackAssembler cannot operate on a directory.");
            return false;
        } else if (!"asm".equals(extension)) {
            System.out.println("HackAssembler can only operate on .asm files.");
            return false;
        } else {
            return true;
        }
    }

    /** Get a map of label / integer value pairs. */
    public static Map<String, Integer> getLabels(final File f) throws IOException {
        final Map<String, Integer> labels = new HashMap<>();
        // Populate predefined symbols.
        labels.put("SP", 0);
        labels.put("LCL", 1);
        labels.put("ARG", 2);
        labels.put("THIS", 3);
        labels.put("THAT", 4);
        labels.put("SCREEN", 16384);
        labels.put("KBD", 24576);
        for (int i = 0; i <= 15; i++) labels.put("R"+i, i);

        // Parse
        try (final Parser parser = new Parser(f)) {
            int lineCounter = 0;
            // Loop through .asm file for symbols that require defining.
            while (parser.hasMoreCommands()) {
                parser.advance();
                final CommandType ct = parser.commandType();
                if (L_COMMAND.equals(ct)) {
                    labels.put(parser.symbol(), lineCounter);
                } else {
                    lineCounter++;
                }
            }
            parser.close();
        }

        return labels;
    }

    /** Write the provided string to a hack file in the same location as the original file. */
    public static void writeToHackFile(final String fileContent, final File f) {
        File hackFile = new File(f.getAbsolutePath().replaceAll("\\.asm$", ".hack"));
        try (FileWriter writer = new FileWriter(hackFile)) {
            writer.append(fileContent);
            writer.flush();
            writer.close();
            System.out.println("Hack file successfully written to the same location as the original assembly.");
        } catch (IOException e) {
            System.out.println("Could not write content to the below file;");
            System.out.println("    " + hackFile.getAbsolutePath());
        }
    }
}
