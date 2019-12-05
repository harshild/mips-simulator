package com.harshild.mips.in;

import com.harshild.mips.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Instruction {
    String string_ins;
    int startClockCycleForCurrentStage;
    int clockCycleWB;
    int clockCycleEX;
    int clockCycleID;
    int clockCycleIF;
    String currentStage;
    private int insIndex;
    private String instructionName;
    private int endClockCycleForCurrentStage;

    public void setInstructionName(String instructionName) {
        this.instructionName = instructionName;
    }

    public String getInstructionName() {
        return instructionName;
    }

    public void setEndClockCycleForCurrentStage(int endClockCycleForCurrentStage) {
        this.endClockCycleForCurrentStage = endClockCycleForCurrentStage;
    }

    public int getEndClockCycleForCurrentStage() {
        return endClockCycleForCurrentStage;
    }
}
