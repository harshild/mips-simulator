package com.harshild.mips.in;

import com.harshild.mips.AppConstants;
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

    public void addInstruction(Instruction inst, int index) {
        Instruction instruction = new Instruction();
        instruction.setInstructionName(inst.getInstructionName());
        instruction.setStringIns(inst.getStringIns());
        instruction.setRawStringIns(inst.getRawStringIns());
        instruction.setInsIndex(index);
        instruction.setCurrentStage(AppConstants.ICACHE);
        this.instructions.add(index, instruction);

        for (int i = index + 1; i < instructions.size(); i++) {
            instructions.get(i).setInsIndex(i);
        }
    }

    public void addLabel(Label label) {
        this.labels.add(label);
    }


}
