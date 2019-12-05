package com.harshild.mips.stages;

import com.harshild.mips.in.Instruction;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@RequiredArgsConstructor
public class ICacheStage {
    boolean busy = false;

    @NonNull
    int clockCycle;


    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public int getClockCycleReq(Instruction instruction) {
        return clockCycle;
    }
}
