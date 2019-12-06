package com.harshild.mips.stages.execution;

import com.harshild.mips.AppConstants;
import com.harshild.mips.di.ClassFactory;
import com.harshild.mips.in.Config;
import com.harshild.mips.in.Instruction;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class ExecutionManger {

    @NonNull
    private List<Config> configs;

    private int dataTranferBusyTill;
    private int mulUnitBusyTill;
    private int divUnitBusyTill;
    private int arithUnitBusyTill;
    private int iuUnitBusyTill;

    public int executeInstruction(Instruction instruction, int currentClockCycle){
        if(AppConstants.data_transfer.contains(instruction.getInstructionName())){
            return executeUsingDataTransferFlow(instruction, currentClockCycle);
        }else if(AppConstants.iu.contains(instruction.getInstructionName())){
            return executeUsingIntegerUnitFlow(instruction, currentClockCycle);
        }else if(AppConstants.arith.contains(instruction.getInstructionName())){
            return executeUsingArithUnitFlow(instruction, currentClockCycle);
        }else if(AppConstants.div.contains(instruction.getInstructionName())){
            return executeUsingDivUnitFlow(instruction, currentClockCycle);
        }else if(AppConstants.mul.contains(instruction.getInstructionName())){
            return executeUsingMulUnitFlow(instruction, currentClockCycle);
        }
        return -1;
    }

    private int executeUsingDataTransferFlow(Instruction instruction, int currentClockCycle) {
        if(dataTranferBusyTill < currentClockCycle) {
            int clockCycleRequired = instruction.getInstructionName().contains(".D") ? 2 : 1;
            if(instruction.getInstructionName().contains("S.D"))
                clockCycleRequired ++;
            dataTranferBusyTill = currentClockCycle + clockCycleRequired -1;
            return clockCycleRequired;
        }
        return -1;
    }

    private int executeUsingMulUnitFlow(Instruction instruction, int currentClockCycle) {
        if(mulUnitBusyTill < currentClockCycle){
            Config fp_multiplier = configs.get(configs.indexOf(new Config("FP Multiplier")));
            int clockCycleReq = fp_multiplier.getClockCycle();
            if(fp_multiplier.isPipelined())
                mulUnitBusyTill = currentClockCycle;
            else
                mulUnitBusyTill = clockCycleReq + clockCycleReq - 1;
            return clockCycleReq;
        }
        return -1;
    }

    private int executeUsingDivUnitFlow(Instruction instruction, int currentClockCycle) {
        if(divUnitBusyTill < currentClockCycle){
            Config fp_divtiplier = configs.get(configs.indexOf(new Config("FP divider")));
            int clockCycleReq = fp_divtiplier.getClockCycle();
            if(fp_divtiplier.isPipelined())
                divUnitBusyTill = currentClockCycle;
            else
                divUnitBusyTill = clockCycleReq + clockCycleReq - 1;
            return clockCycleReq;
        }
        return -1;
    }

    private int executeUsingArithUnitFlow(Instruction instruction, int currentClockCycle) {
        if(arithUnitBusyTill < currentClockCycle){
            Config fp_arithtiplier = configs.get(configs.indexOf(new Config("FP adder")));
            int clockCycleReq = fp_arithtiplier.getClockCycle();
            if(fp_arithtiplier.isPipelined())
                arithUnitBusyTill = currentClockCycle;
            else
                arithUnitBusyTill = clockCycleReq + clockCycleReq - 1;
            return clockCycleReq;
        }
        return -1;
    }



    private int executeUsingIntegerUnitFlow(Instruction instruction, int currentClockCycle) {
        if(iuUnitBusyTill < currentClockCycle){
            int clockCycleReq = 2;
                iuUnitBusyTill = clockCycleReq + clockCycleReq - 1;
            return clockCycleReq;
        }
        return -1;
    }

    public boolean isDCacheReq(Instruction instruction) {
        return AppConstants.data_transfer.contains(instruction.getInstructionName().trim());
    }
}
