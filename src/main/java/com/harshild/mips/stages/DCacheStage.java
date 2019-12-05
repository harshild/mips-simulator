package com.harshild.mips.stages;

import com.harshild.mips.in.Instruction;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DCacheStage {
    private boolean busy = false;

    @NonNull
    int clockCycle;

    public int getClockCycleReq(Instruction instruction) {
        return clockCycle;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}
