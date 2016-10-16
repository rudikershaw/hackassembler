package com.rudikershaw.assembler;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.rudikershaw.assembler.CommandType.*;
import static com.rudikershaw.assembler.Encoder.COMPUTATIONS;
import static com.rudikershaw.assembler.Encoder.JUMPS;

/** The parser module, for parsing a .asm file. */
public class Parser implements Closeable {

    /** The file to parse. */
    private final BufferedReader br;
    /** Current command. */
    private String current;
    /** The next command. */
    private String next;
    /** Set of valid destinations. */
    public static final Set<String> DESTINATIONS = new HashSet<>(Arrays.asList("M", "D", "MD", "A", "AM", "AD", "AMD", "DM",
                                                                               "MA", "DA", "DMA", "MAD", "ADM", "MDA", "DAM"));

    /** Constructor. */
    public Parser(File f) throws FileNotFoundException {
        this.br = new BufferedReader(new FileReader(f));
    }

    /** Return true if the .asm file has more commands to parse. */
    public boolean hasMoreCommands() throws IOException {
        if (next == null || next.isEmpty() || next.startsWith("//")) {
            next = br.readLine();
            if (next == null) {
                return false;
            } else {
                if (next.contains("//")) next = next.substring(0, next.indexOf("//"));
                next = next.trim();
                return (!next.isEmpty() && !next.startsWith("//")) || hasMoreCommands();
            }
        } else {
            return true;
        }
    }

    /** Advance to the next command in the file if one exists. */
    public void advance() throws IOException {
        if (next != null || hasMoreCommands()) {
            current = next;
            next = null;
        }
    }

    /** Get the command type of the current command. */
    public CommandType commandType() {
        if (current.startsWith("@") && current.length() >= 2 && current.charAt(1) != ' ') {
            return A_COMMAND;
        } else if (current.startsWith("(") && current.endsWith(")") && current.length() >= 3) {
            return L_COMMAND;
        } else if ((current.startsWith("D") || current.startsWith("M") || current.startsWith("A")
                || current.startsWith("0")) && (current.contains("=") || current.contains(";"))) {
            return C_COMMAND;
        } else {
            System.out.println("Unrecognised command: '" + current + "'.");
            throw new UnsupportedOperationException(current);
        }
    }

    /** Get the symbol from the current command. Only call if the current command is an A or L command. */
    public String symbol() {
        switch (commandType()) {
            case A_COMMAND:
                return current.substring(current.indexOf("@") + 1);
            case L_COMMAND:
                return current.substring(current.indexOf("(") + 1, current.lastIndexOf(")"));
            case C_COMMAND:
                throw new UnsupportedOperationException("symbol() not supported for C_COMMAND.");
            default:
                throw new UnsupportedOperationException("Unsupported command type - " + commandType());
        }
    }

    /** Get the destination from the current command. Only call if the current command is a C command. */
    public String dest() {
        if (!commandType().equals(C_COMMAND)) {
            throw new UnsupportedOperationException("dest() not supported for " + commandType());
        } else if (current.contains("=") && DESTINATIONS.contains(current.substring(0, current.indexOf('=')))) {
            return current.substring(0, current.indexOf('='));
        } else if (current.contains(";")) {
            return null;
        } else {
            throw new UnsupportedOperationException("Invalid destination in '" + current + "'");
        }
    }

    /** Get the computation from the current command. Only call if the current command is a C command. */
    public String comp() {
        if (!commandType().equals(C_COMMAND)) {
            throw new UnsupportedOperationException("dest() not supported for " + commandType());
        } else if (current.contains("=") && COMPUTATIONS.containsKey(current.substring(current.indexOf('=') + 1))) {
            return current.substring(current.indexOf('=') + 1);
        } else if (current.contains(";") && COMPUTATIONS.containsKey(current.substring(0, current.indexOf(';')))) {
            return current.substring(0, current.indexOf(';'));
        } else {
            throw new UnsupportedOperationException("Invalid computation in '" + current + "'");
        }
    }

    /** Get the jump from the current command. Only call if the current command is a C command. */
    public String jump() {
        if (!commandType().equals(C_COMMAND)) {
            throw new UnsupportedOperationException("dest() not supported for " + commandType());
        } else if (current.contains("=")) {
            return null;
        } else if (current.contains(";") && JUMPS.containsKey(current.substring(current.indexOf(';') + 1))) {
            return current.substring(current.indexOf(';') + 1);
        } else {
            throw new UnsupportedOperationException("Invalid jump in '" + current + "'");
        }
    }

    @Override
    public void close() {
        try {
            br.close();
        } catch (IOException e) {
            // Do nothing.
        }
    }
}
