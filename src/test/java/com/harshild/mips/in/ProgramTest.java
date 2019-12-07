package com.harshild.mips.in;

import org.junit.Test;

import java.io.File;

public class ProgramTest {

    @Test
    public void itShouldReadIns() {
        Program program = new Program();
        File file = new File("./inst.txt");
        program.parseTxt(file);
    }
}