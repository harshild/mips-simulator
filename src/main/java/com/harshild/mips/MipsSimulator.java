package com.harshild.mips;

import com.harshild.mips.di.ClassFactory;
import com.harshild.mips.exception.ConfigurationReadErrorException;
import com.harshild.mips.in.*;
import com.harshild.mips.manager.InputManager;
import com.harshild.mips.stages.*;
import com.harshild.mips.stages.execution.ExecutionManger;

import java.util.List;

import static com.harshild.mips.AppConstants.*;

public class MipsSimulator {

    public static void main(String[] args) throws ConfigurationReadErrorException {
        InputManager inputManager = ClassFactory.getInputManager();

        Program program = inputManager.readInst("./test_cases/test_case_1/inst.txt");
        List<Mem> data = inputManager.readData("./test_cases/test_case_1/data.txt");
        List<Reg> regs = inputManager.readReg("./test_cases/test_case_1/reg.txt");
        List<Config> configs = inputManager.readConfig("./test_cases/test_case_1/config.txt");

        initializeRegisters(regs);
        initializeMemory(data);

        ICacheStage iCache = ClassFactory.initICacheStage(configs);
        FetchStage fetchStage = ClassFactory.initFetchStage(configs);
        DecodeStage decodeStage = ClassFactory.initDecodeStage(configs);
        DCacheStage dCacheStage = ClassFactory.initDCacheStage(configs);
        ExecutionStage executionStage = ClassFactory.initExecutionStage(configs);
        WriteBackStage writeBackStage = ClassFactory.initWriteBackStage(configs);

        int clockCycle = 1;

        int iCacheUsedUp = 0;
        int ifUsedUp = 0;
        int idUsedUp = 0;
        int dCacheUsedUp = 0;
        int execUsedUp = 0;
        int wBUsedUp = 0;

        int lastInstToEndId = -1;
        ExecutionManger executionManger = ClassFactory.initExecutionManger(configs);

        while (true) {
            Instruction controlIns = null;
            for (Instruction instruction : program.getInstructions()) {
                switch (instruction.getCurrentStage()) {
                    case ICACHE:
                        if (iCacheUsedUp != clockCycle) {
                            iCacheUsedUp = clockCycle;
                            if (!iCache.isBusy() && lastInstToEndId == instruction.getInsIndex() - 1 && instruction.getStartClockCycleForCurrentStage() == 0) {
                                instruction.setEndClockCycleForCurrentStage(clockCycle + iCache.getClockCycleReq(instruction) - 1);
                                instruction.setStartClockCycleForCurrentStage(clockCycle);
                                iCache.setBusy(true);
                            }
                            if (instruction.getEndClockCycleForCurrentStage() == clockCycle) {
                                instruction.setCurrentStage(ID);
                                if (fetchStage.isControl(instruction) || fetchStage.isSpecialPurpose(instruction)) {
                                    if (program.getInstructions().get(instruction.getInsIndex() - 2).getCurrentStage().equals(FINISH)) {
                                        lastInstToEndId++;
                                        instruction.setCurrentStage(FINISH);
                                    }
                                }
                                instruction.setStartClockCycleForCurrentStage(0);
                                instruction.setClockCycleIF(clockCycle);
                                iCache.setBusy(false);
                            }
                        }
                        break;
                    case IF:
                        if (ifUsedUp != clockCycle) {
                            ifUsedUp = clockCycle;
                            if (!fetchStage.isBusy() && instruction.getStartClockCycleForCurrentStage() == 0) {
                                instruction.setEndClockCycleForCurrentStage(clockCycle + fetchStage.getClockCycleReq(instruction) - 1);
                                instruction.setStartClockCycleForCurrentStage(clockCycle);
                                fetchStage.setBusy(true);
                            }
                            if (instruction.getEndClockCycleForCurrentStage() == clockCycle) {
                                instruction.setCurrentStage(ID);
                                instruction.setStartClockCycleForCurrentStage(0);
                                fetchStage.setBusy(false);
                                instruction.setClockCycleIF(clockCycle);
                            }
                        }
                        break;
                    case ID:
                        if (idUsedUp != clockCycle) {
                            idUsedUp = clockCycle;


                            if (!decodeStage.isBusy() && !decodeStage.areSourceBusy(instruction) && !decodeStage.istDesLocBusy(instruction) && instruction.getStartClockCycleForCurrentStage() == 0) {
                                instruction.setEndClockCycleForCurrentStage(clockCycle + decodeStage.getClockCycleReq(instruction) - 1);
                                instruction.setStartClockCycleForCurrentStage(clockCycle);
                                decodeStage.blockDesLoc(instruction);
                                decodeStage.setBusy(true);
                            }else{
                                if(decodeStage.areSourceBusy(instruction))
                                    instruction.setHzRAW(true);
                            }
//                            if(instruction.getEndClockCycleForCurrentStage() == clockCycle && decodeStage.areSourceBusy(instruction)){
//                                instruction.setEndClockCycleForCurrentStage(clockCycle + 1);
//                                idUsedUp = clockCycle + 1;
//                            }
                        }
                        if (instruction.getEndClockCycleForCurrentStage() == clockCycle) {
                            if (fetchStage.isControl(instruction)) {
                                controlIns = instruction;
                                instruction.setCurrentStage(FINISH);
                                decodeStage.unblockDesLoc(instruction);
                            } else if (executionManger.isDCacheReq(instruction))
                                instruction.setCurrentStage(DCACHE);
                            else
                                instruction.setCurrentStage(EX);

                            lastInstToEndId = instruction.getInsIndex();
                            instruction.setStartClockCycleForCurrentStage(0);
                            decodeStage.setBusy(false);
                            instruction.setClockCycleID(clockCycle);

                        }
                        break;
                    case DCACHE:
                        dCacheUsedUp = clockCycle;
                        if (instruction.getStartClockCycleForCurrentStage() == 0) {
                            System.out.println(instruction.getInsIndex());
                            instruction.setEndClockCycleForCurrentStage(clockCycle + dCacheStage.getClockCycleReq(instruction) - 1);
                            instruction.setStartClockCycleForCurrentStage(clockCycle);
                            dCacheStage.setBusy(true);
                        }
                        if (instruction.getEndClockCycleForCurrentStage() == clockCycle) {
                            instruction.setCurrentStage(EX);
                            instruction.setStartClockCycleForCurrentStage(0);
                            dCacheStage.setBusy(false);
                        }
                        break;
                    case EX:
                        if (instruction.getStartClockCycleForCurrentStage() == 0) {
                            int i = ClassFactory.getExecutionManger().executeInstruction(instruction, clockCycle);
                            if(i==-1){
                                instruction.setHzStruct(true);
                            }
                            if (i != -1) {
                                instruction.setEndClockCycleForCurrentStage(clockCycle + i - 1);
                                instruction.setStartClockCycleForCurrentStage(clockCycle);
                            }
                        }
                        if (instruction.getEndClockCycleForCurrentStage() == clockCycle) {
                            ClassFactory.getExecutionManger().updateValues(instruction);
                            instruction.setClockCycleEX(clockCycle);
                            instruction.setCurrentStage(WB);
                            instruction.setStartClockCycleForCurrentStage(0);
                        }
                        break;
                    case WB:
                        if (wBUsedUp != clockCycle) {
                            wBUsedUp = clockCycle;
                            if (!writeBackStage.isBusy() && instruction.getStartClockCycleForCurrentStage() == 0) {
                                instruction.setEndClockCycleForCurrentStage(clockCycle + writeBackStage.getClockCycleReq(instruction) - 1);
                                instruction.setStartClockCycleForCurrentStage(clockCycle);
                                writeBackStage.setBusy(true);
                            }
                            if (instruction.getEndClockCycleForCurrentStage() == clockCycle - 1) {
                                writeBackStage.updateResults(instruction);
                                decodeStage.unblockDesLoc(instruction);
                            }
                            if (instruction.getEndClockCycleForCurrentStage() == clockCycle) {
                                writeBackStage.updateResults(instruction);
                                instruction.setClockCycleWB(clockCycle);
                                instruction.setCurrentStage(FINISH);
                                instruction.setStartClockCycleForCurrentStage(0);
                                decodeStage.unblockDesLoc(instruction);
                                writeBackStage.setBusy(false);
                            }
                        }
                        break;
                    case FINISH:
                        if (program.getInstructions().get(program.getInstructions().size() - 1) == instruction) {
                            printOutPutAsTable(program.getInstructions());
                            System.exit(0);
                        }
                        break;
                }
            }

            if(controlIns!=null){
                executionManger.executeControlInstruction(controlIns);
            }
            clockCycle++;
        }
    }

    private static void printOutPutAsTable(List<Instruction> instructions) {
        String instructionOutputFormatString = "%-4s %-25s  %-4s  %-4s  %-4s  %-4s  %-4s  %-4s  %-4s  %-4s";

        System.out.println(String.format(instructionOutputFormatString,
                "",
                "Instruction",
                "FT",
                "ID",
                "EX",
                "WB",
                "RAW",
                "WAR",
                "WAW",
                "Struct"
                )
        );

        for (Instruction instruction : instructions) {
            System.out.println(String.format(instructionOutputFormatString,
                    instruction.getRawStringIns().contains(":") ? instruction.getRawStringIns().split(":")[0]+":" : "",
                    instruction.getStringIns(),
                    getCCValueForPrint(instruction.getClockCycleIF()),
                    getCCValueForPrint(instruction.getClockCycleID()),
                    getCCValueForPrint(instruction.getClockCycleEX()),
                    getCCValueForPrint(instruction.getClockCycleWB()),
                    instruction.getClockCycleID() - instruction.getClockCycleIF() > 1 ? "Y":"N",
                    instruction.isHzWAR() ? "Y":"N",
                    instruction.isHzWAW() ? "Y":"N",
                    instruction.isHzStruct() ? "Y":"N"
                    )
            );
        }

        System.out.println("\nTotal number of access requests for instruction cache: " + ICacheStage.accessCount);
        System.out.println("\nNumber of instruction cache hits: " + ICacheStage.hitCount);
        System.out.println("\nTotal number of access requests for data cache: " + DCacheStage.accessCount);
        System.out.println("\nNumber of data cache hits: " + DCacheStage.hitCount);

    }

    private static String getCCValueForPrint(int clockCycle) {
        return clockCycle == 0 ? "": String.valueOf(clockCycle);
    }

    private static void initializeMemory(List<Mem> data) {
        ClassFactory.initMemory(data);
    }

    private static void initializeRegisters(List<Reg> regs) {
        ClassFactory.initRegister(regs);
    }


}
