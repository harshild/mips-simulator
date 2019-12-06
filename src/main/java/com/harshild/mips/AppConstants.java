package com.harshild.mips;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AppConstants {

    public static final String ICACHE = "ICACHE";
    public static final String DCACHE = "DCACHE";
    public static final String IF = "IF";
    public static final String ID = "ID";
    public static final String EX = "EX";
    public static final String WB = "WB";
    public static final String FINISH = "FINISH";
    public static final String START = "START";

    public static final List<String> op_add = Arrays.asList("DADD", "DADDI", "ADD.D");
    public static final List<String> op_sub = Arrays.asList("DSUB", "DSUBI", "SUB.D");
    public static final List<String> op_bwand = Arrays.asList("AND", "ANDI");
    public static final List<String> op_bwor = Arrays.asList("OR", "ORI");

    public static final List<String> data_transfer = Arrays.asList("LW", "SW", "L.D", "S.D");
    public static final List<String> iu = Arrays.asList("DADD", "DADDI", "DSUB", "DSUBI", "AND", "ANDI", "OR", "ORI");
    public static final List<String> div = Arrays.asList("DIV.D");
    public static final List<String> arith = Arrays.asList("ADD.D", "SUB.D");
    public static final List<String> mul = Arrays.asList("MUL.D");
    public static final List<String> specialOp = Arrays.asList("HLT");
    public static final List<String> control = Arrays.asList("J", "BEQ", "BNE");
    public static final List<String> store = Arrays.asList("SW", "S.D");
    public static final List<String> load = Arrays.asList("LW", "L.D");

    public static final int ICACHE_BLOCK_SIZE = 4;

}
