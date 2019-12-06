package com.harshild.mips.in;

import com.harshild.mips.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Instruction {
    String stringIns;
    String rawStringIns;
    int startClockCycleForCurrentStage;
    int clockCycleWB;
    int clockCycleEX;
    int clockCycleID;
    int clockCycleIF;
    String currentStage;
    private int insIndex;
    private String instructionName;
    private int endClockCycleForCurrentStage;
    private int insOut;

    private boolean hzRAW;
    private boolean hzWAR;
    private boolean hzWAW;
    private boolean hzStruct;

    public int getInsOut() {
        return insOut;
    }
}
