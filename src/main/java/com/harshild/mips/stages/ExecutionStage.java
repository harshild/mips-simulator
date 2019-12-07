package com.harshild.mips.stages;

import com.harshild.mips.in.Config;
import com.harshild.mips.in.Instruction;

import java.util.List;

public class ExecutionStage {
    Instruction currentInst;
    int currentInstIndex;
    int clockCycleRequired;
    private boolean busy = false;

    public ExecutionStage(List<Config> configs) {

    }


    private int getClockCycleRequired(Instruction instruction) {
        return 1;
    }

    private boolean isInUse() {
        return currentInst != null;
    }

    public int getClockCycleReq(Instruction instruction) {
        return 1;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}
