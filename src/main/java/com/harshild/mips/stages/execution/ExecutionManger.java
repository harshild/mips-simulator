package com.harshild.mips.stages.execution;

import com.harshild.mips.AppConstants;
import com.harshild.mips.di.ClassFactory;
import com.harshild.mips.in.Config;
import com.harshild.mips.in.Instruction;
import com.harshild.mips.in.Label;
import com.harshild.mips.in.Mem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    public int executeInstruction(Instruction instruction, int currentClockCycle) {
        if (AppConstants.data_transfer.contains(instruction.getInstructionName())) {
            return executeUsingDataTransferFlow(instruction, currentClockCycle);
        } else if (AppConstants.iu.contains(instruction.getInstructionName())) {
            return executeUsingIntegerUnitFlow(instruction, currentClockCycle);
        } else if (AppConstants.arith.contains(instruction.getInstructionName())) {
            return executeUsingArithUnitFlow(instruction, currentClockCycle);
        } else if (AppConstants.div.contains(instruction.getInstructionName())) {
            return executeUsingDivUnitFlow(instruction, currentClockCycle);
        } else if (AppConstants.mul.contains(instruction.getInstructionName())) {
            return executeUsingMulUnitFlow(instruction, currentClockCycle);
        }
        return -1;
    }

    private int executeUsingDataTransferFlow(Instruction instruction, int currentClockCycle) {
        if (dataTranferBusyTill < currentClockCycle) {
            int clockCycleRequired = instruction.getInstructionName().contains(".D") ? 2 : 1;
            if (instruction.getInstructionName().contains("S.D") || instruction.getInstructionName().contains("SW"))
                clockCycleRequired++;
            dataTranferBusyTill = currentClockCycle + clockCycleRequired - 1;
            return clockCycleRequired;
        }
        return -1;
    }

    private int executeUsingMulUnitFlow(Instruction instruction, int currentClockCycle) {
        if (mulUnitBusyTill < currentClockCycle) {
            Config fp_multiplier = configs.get(configs.indexOf(new Config("FP Multiplier")));
            int clockCycleReq = fp_multiplier.getClockCycle();
            if (fp_multiplier.isPipelined())
                mulUnitBusyTill = currentClockCycle;
            else
                mulUnitBusyTill = clockCycleReq + clockCycleReq - 1;
            return clockCycleReq;
        }
        return -1;
    }

    private int executeUsingDivUnitFlow(Instruction instruction, int currentClockCycle) {
        if (divUnitBusyTill < currentClockCycle) {
            Config fp_divtiplier = configs.get(configs.indexOf(new Config("FP divider")));
            int clockCycleReq = fp_divtiplier.getClockCycle();
            if (fp_divtiplier.isPipelined())
                divUnitBusyTill = currentClockCycle;
            else
                divUnitBusyTill = clockCycleReq + clockCycleReq - 1;
            return clockCycleReq;
        }
        return -1;
    }

    private int executeUsingArithUnitFlow(Instruction instruction, int currentClockCycle) {
        if (arithUnitBusyTill < currentClockCycle) {
            Config fp_arithtiplier = configs.get(configs.indexOf(new Config("FP adder")));
            int clockCycleReq = fp_arithtiplier.getClockCycle();
            if (fp_arithtiplier.isPipelined())
                arithUnitBusyTill = currentClockCycle;
            else
                arithUnitBusyTill = clockCycleReq + clockCycleReq - 1;
            return clockCycleReq;
        }
        return -1;
    }


    private int executeUsingIntegerUnitFlow(Instruction instruction, int currentClockCycle) {
        if (iuUnitBusyTill < currentClockCycle) {
            int clockCycleReq = 2;
            iuUnitBusyTill = clockCycleReq + clockCycleReq - 1;
            return clockCycleReq;
        }
        return -1;
    }

    public boolean isDCacheReq(Instruction instruction) {
        return AppConstants.data_transfer.contains(instruction.getInstructionName().trim());
    }

    public void updateValues(Instruction instruction) {
        String opCode = instruction.getInstructionName();
        List<String> src = ClassFactory.getDecodeStage().getSrc(instruction);
        int output = 0;
        if (AppConstants.store.contains(opCode)) {
            output = getValForSrc(Collections.singletonList(instruction.getString_ins().split(",")[0].split(" ")[1].trim()), 0);
        } else if (AppConstants.load.contains(opCode)) {
            int val1 = Integer.parseInt(instruction.getString_ins().split(",")[1].split("\\(")[0].trim());
            int val2 = getValForSrc(Collections.singletonList(instruction.getString_ins().split(",")[1].split("\\(")[1].split("\\)")[0].trim()), 0);

            int i = ClassFactory.getMemory().getData().indexOf(new Mem(val1 + val2));
            output = ClassFactory.getMemory().getData().get(i).getData();
        } else {
            int val1 = getValForSrc(src, 0);
            int val2 = getValForSrc(src, 1);
            if (AppConstants.op_add.contains(opCode)) {
                output = val1 + val2;
            } else if (AppConstants.op_sub.contains(opCode)) {
                output = val1 - val2;
            } else if (AppConstants.div.contains(opCode)) {
                output = val1 / val2;
            } else if (AppConstants.mul.contains(opCode)) {
                output = val1 * val2;
            } else if (AppConstants.op_bwand.contains(opCode)) {
                output = val1 & val2;
            } else if (AppConstants.op_bwor.contains(opCode)) {
                output = val1 | val2;
            }
        }
        System.out.println(instruction.getString_ins() + "   " + output);
        instruction.setInsOut(output);
    }

    private int getValForSrc(List<String> src, int i) {
        int val;
        if (src.get(i).charAt(0) == 'R')
            val = ClassFactory.getRegisterInteger().getRegs().get(Integer.parseInt(String.valueOf(src.get(i).charAt(1)))).getValue();
        else if (src.get(i).charAt(0) == 'F')
            val = ClassFactory.getRegisterFloat().getRegs().get(Integer.parseInt(String.valueOf(src.get(i).charAt(1)))).getValue();
        else
            val = Integer.parseInt(src.get(i));
        return val;
    }

    public void executeControlInstruction(Instruction instruction) {
        String opCode = instruction.getInstructionName();
        String label = "";
        if(opCode.equalsIgnoreCase("J")){
            label = instruction.getString_ins().split(" ")[1].trim();
        }else{
            String[] s = instruction.getString_ins().replace(",","").split(" ");
            String reg1 = s[1].trim();
            String reg2 = s[2].trim();
            String labelTemp = s[3].trim();
            if(opCode.equalsIgnoreCase("BNE"))
                label = getValForSrc(Arrays.asList(reg1),0) != getValForSrc(Arrays.asList(reg2),0) ? labelTemp : "";
            if(opCode.equalsIgnoreCase("BEQ"))
                label = getValForSrc(Arrays.asList(reg1),0) == getValForSrc(Arrays.asList(reg2),0) ? labelTemp : "";
        }

        if(!label.equals("")) {
            Label label1 = ClassFactory.getProgram().getLabels().get(ClassFactory.getProgram().getLabels().indexOf(new Label(label)));


            List<Instruction> instructions = new ArrayList<>(ClassFactory.getProgram().getInstructions());
            int startIndex = instruction.getInsIndex() + 2;
            List<Instruction> remainingIns = new ArrayList<>(instructions.subList(startIndex-1, instructions.size() - 1));

            for (int in = label1.getStartInstructionIndex(); in <= label1.getEndInstructionIndex() + 1; in++) {
                ClassFactory.getProgram().addInstruction(instructions.get(in), startIndex + (in - label1.getStartInstructionIndex()));
            }
        }
    }
}
