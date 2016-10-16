package com.rudikershaw.assembler;

import java.util.*;

/** The code module for encoding hack assembly mnemonics into binary. */
public class Encoder {
    
    /** Converts a hack assembly destination into it's 3 bit binary code. */
    public String dest(String dest) {
        if (dest == null) {
            return "000";
        } else {
            return (dest.contains("A") ? "1" : "0") + (dest.contains("D") ? "1" : "0") + (dest.contains("M") ? "1" : "0");
        }
    }

    /** Map of valid computations. */
    public static final Map<String, String> COMPUTATIONS = new HashMap<String, String>(){{
        put("0", "0101010");   put("1", "0111111");   put("-1", "0111010");
        put("D", "0001100");   put("A", "0110000");   put("!D", "0001101");
        put("!A", "0110001");  put("-D", "0001111");  put("-A", "0110011");
        put("D+1", "0011111"); put("1+D", "0011111"); put("A+1", "0110111");
        put("1+A", "0110111"); put("D-1", "0001110"); put("A-1", "0110010");
        put("D+A", "0000010"); put("A+D", "0000010"); put("D-A", "0010011");
        put("A-D", "0000111"); put("D&A", "0000000"); put("A&D", "0000000");
        put("D|A", "0010101"); put("A|D", "0010101"); put("M", "1110000");
        put("!M", "1110001");  put("-M", "1110011");  put("M+1", "1110111");
        put("1+M", "1110111"); put("M-1", "1110010"); put("D+M", "1000010");
        put("M+D", "1000010"); put("D-M", "1010011"); put("M-D", "1000111");
        put("D&M", "1000000"); put("M&D", "1000000"); put("D|M", "1010101");
        put("M|D", "1010101");
    }};
    
    /** Converts hack assembly computation into it's 7 bit binary code. */
    public String comp(String comp) {
        String bits = COMPUTATIONS.get(comp);
        if (bits == null) {
            throw new UnsupportedOperationException("Unrecognised computaion '" + comp + "'");
        } else {
            return bits;
        }
    }

    /** Map of valid jumps. */
    public static final Map<String, String> JUMPS = new HashMap<String, String>(){{
        put("JGT", "001"); put("JEQ", "010"); put("JGE", "011"); put("JLT", "100");
        put("JNE", "101"); put("JLE", "110"); put("JMP", "111");
    }};

    /** Converts hack assembly jump into it's 3 bit binary code. */
    public String jump(String jump) {
        String bits = JUMPS.get(jump);
        if (bits == null) {
            return "000";
        } else {
            return bits;
        }
    }
}
