package com.harshild.mips.in;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Program {
    List<Instruction> instructions = new ArrayList<>();
    List<Label> labels = new ArrayList<>();

    public void addInstruction(Instruction inst) {
        this.instructions.add(inst);
    }
}
