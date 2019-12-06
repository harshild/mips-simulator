package com.harshild.mips.stages;

import com.harshild.mips.AppConstants;
import com.harshild.mips.di.ClassFactory;
import com.harshild.mips.in.Config;
import com.harshild.mips.in.Instruction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DecodeStage {

    Instruction currentInst;
    int currentInstIndex;
    int clockCycleRequired;
    private boolean busy = false;

    public DecodeStage(List<Config> configs) {
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

    public boolean areSourceBusy(Instruction instruction) {
        for (String loc : getSrc(instruction)) {
            if(loc.charAt(0)=='R' && ClassFactory.getRegister().getRegs().get(Integer.parseInt(String.valueOf(loc.charAt(1)))).isBusy())
                return true;
            if(loc.charAt(0)=='F' && ClassFactory.getMemory().getData().get(Integer.parseInt(String.valueOf(loc.charAt(1)))).isBusy())
                return true;

        }
        return false;
    }
    private List<String> getSrc(Instruction instruction) {
        if(AppConstants.store.contains(instruction.getInstructionName())){
            return Arrays.asList(instruction.getString_ins().split(",")[0].split(" ")[1]);
        }else if(AppConstants.data_transfer.contains(instruction.getInstructionName())){
            return Arrays.asList(instruction.getString_ins().split(",")[1].trim().split("\\(")[1].split("\\)")[0]);
        }
        else {
            return Arrays.asList(instruction.getString_ins().split(",")[1].trim(),instruction.getString_ins().split(",")[2].trim());
        }
    }

    public void blockDesLoc(Instruction instruction) {
        String des = getDes(instruction);
        System.out.println("BLOCKED "+ des);
        if(!des.equals("")){
            if(des.charAt(0)=='R')
                ClassFactory.getRegister().getRegs().get(Integer.parseInt(String.valueOf(des.charAt(1)))).setBusy(true);
            if(des.charAt(0)=='F')
                ClassFactory.getMemory().getData().get(Integer.parseInt(String.valueOf(des.charAt(1)))).setBusy(true);

        }
    }

    public boolean istDesLocBusy(Instruction instruction) {
        String des = getDes(instruction);
        System.out.println("CHECKED "+des);
        if(!des.equals("")){
            if(des.charAt(0)=='R')
                return ClassFactory.getRegister().getRegs().get(Integer.parseInt(String.valueOf(des.charAt(1)))).isBusy();
            if(des.charAt(0)=='F')
                return ClassFactory.getMemory().getData().get(Integer.parseInt(String.valueOf(des.charAt(1)))).isBusy();

        }
        return false;
    }

    private String getDes(Instruction instruction) {
        if(AppConstants.store.contains(instruction.getInstructionName())){
            return instruction.getString_ins().split(",")[1].split("\\(")[1].split("\\)")[0];
        }else {
            return instruction.getString_ins().split(",")[0].split(" ")[1];
        }
    }

    public void unblockDesLoc(Instruction instruction) {
        String des = getDes(instruction);
        System.out.println("UNBLOCK "+des);

        if(!des.equals("")){
            if(des.charAt(0)=='R')
                ClassFactory.getRegister().getRegs().get(Integer.parseInt(String.valueOf(des.charAt(1)))).setBusy(false);
            if(des.charAt(0)=='F')
                ClassFactory.getMemory().getData().get(Integer.parseInt(String.valueOf(des.charAt(1)))).setBusy(false);
        }
    }
}
