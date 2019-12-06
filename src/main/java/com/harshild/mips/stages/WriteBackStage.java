package com.harshild.mips.stages;

import com.harshild.mips.AppConstants;
import com.harshild.mips.di.ClassFactory;
import com.harshild.mips.in.Config;
import com.harshild.mips.in.Instruction;
import com.harshild.mips.in.Reg;

import java.util.Arrays;
import java.util.List;

public class WriteBackStage {
    Instruction currentInst;
    int currentInstIndex;
    int clockCycleRequired;
    private boolean busy = false;

    public WriteBackStage(List<Config> configs) {
    }


    private int getClockCycleRequired(Instruction instruction) {
        return 1;
    }

    private boolean isInUse() {
        return currentInst != null;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public int getClockCycleReq(Instruction instruction) {
        return 1;
    }

    public void updateResults(Instruction instruction) {
        System.out.println(instruction.getString_ins()+"  "+instruction.getInsOut());
        String des = ClassFactory.getDecodeStage().getDes(instruction);
        if(AppConstants.store.contains(instruction.getInstructionName())){
            System.out.println("STORE IMPLEMENT");
        }else {
            if (des.charAt(0) == 'R')
                ClassFactory.getRegisterInteger().getRegs().get(Integer.parseInt(String.valueOf(des.charAt(1)))).setValue(instruction.getInsOut());
            else if (des.charAt(0) == 'F')
                ClassFactory.getRegisterFloat().getRegs().get(Integer.parseInt(String.valueOf(des.charAt(1)))).setValue(instruction.getInsOut());
        }
    }
}
