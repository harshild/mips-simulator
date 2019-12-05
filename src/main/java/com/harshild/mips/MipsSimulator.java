package com.harshild.mips;

import com.harshild.mips.di.ClassFactory;
import com.harshild.mips.exception.ConfigurationReadErrorException;
import com.harshild.mips.in.*;
import com.harshild.mips.manager.InputManager;
import com.harshild.mips.stages.*;

import java.util.List;

import static com.harshild.mips.AppConstants.*;

public class MipsSimulator {

    public static void main(String[] args) throws ConfigurationReadErrorException {
        InputManager inputManager = ClassFactory.getInputManager();

        Program program = inputManager.readInst("inst.txt");
        List<Mem> data = inputManager.readData("data.txt");
        List<Reg> regs = inputManager.readReg("reg.txt");
        List<Config> configs = inputManager.readConfig("config.txt");

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

        while (true) {
            System.out.println(clockCycle);
            for (Instruction instruction : program.getInstructions()) {
                    switch (instruction.getCurrentStage()) {
                        case ICACHE:
                            if(iCacheUsedUp != clockCycle) {
                                iCacheUsedUp = clockCycle;
                                if (!iCache.isBusy() && instruction.getStartClockCycleForCurrentStage() == 0) {
                                    instruction.setEndClockCycleForCurrentStage(clockCycle + iCache.getClockCycleReq(instruction) - 1);
                                    instruction.setStartClockCycleForCurrentStage(clockCycle);
                                    iCache.setBusy(true);
                                }
                                if (instruction.getEndClockCycleForCurrentStage() == clockCycle) {
                                    instruction.setCurrentStage(IF);
                                    instruction.setStartClockCycleForCurrentStage(0);
                                    iCache.setBusy(false);
                                }
                            }
                            break;
                        case IF:
                            if(ifUsedUp != clockCycle) {
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
                            if(idUsedUp != clockCycle) {
                                idUsedUp = clockCycle;

                                if (!decodeStage.isBusy() && instruction.getStartClockCycleForCurrentStage() == 0) {
                                    instruction.setEndClockCycleForCurrentStage(clockCycle + decodeStage.getClockCycleReq(instruction) - 1);
                                    instruction.setStartClockCycleForCurrentStage(clockCycle);
                                    decodeStage.setBusy(true);
                                }
                                if (instruction.getEndClockCycleForCurrentStage() == clockCycle) {
                                    instruction.setCurrentStage(DCACHE);
                                    instruction.setStartClockCycleForCurrentStage(0);
                                    decodeStage.setBusy(false);
                                    instruction.setClockCycleID(clockCycle);
                                }
                            }
                            break;
                        case DCACHE:
                            if(dCacheUsedUp != clockCycle) {
                                dCacheUsedUp = clockCycle;
                                if (!dCacheStage.isBusy() && instruction.getStartClockCycleForCurrentStage() == 0) {
                                    instruction.setEndClockCycleForCurrentStage(clockCycle + dCacheStage.getClockCycleReq(instruction) - 1);
                                    instruction.setStartClockCycleForCurrentStage(clockCycle);
                                    dCacheStage.setBusy(true);
                                }
                                if (instruction.getEndClockCycleForCurrentStage() == clockCycle) {
                                    instruction.setCurrentStage(EX);
                                    instruction.setStartClockCycleForCurrentStage(0);
                                    dCacheStage.setBusy(false);
                                }
                            }
                            break;
                        case EX:
                            if(execUsedUp != clockCycle) {
                                execUsedUp = clockCycle;
                                if (!executionStage.isBusy() && instruction.getStartClockCycleForCurrentStage() == 0) {
                                    instruction.setEndClockCycleForCurrentStage(clockCycle + executionStage.getClockCycleReq(instruction) - 1);
                                    instruction.setStartClockCycleForCurrentStage(clockCycle);
                                    executionStage.setBusy(true);
                                }
                                if (instruction.getEndClockCycleForCurrentStage() == clockCycle) {
                                    instruction.setClockCycleEX(clockCycle);
                                    instruction.setCurrentStage(WB);
                                    instruction.setStartClockCycleForCurrentStage(0);
                                    executionStage.setBusy(false);
                                }
                            }
                            break;
                        case WB:
                            if(wBUsedUp != clockCycle) {
                                wBUsedUp = clockCycle;
                                if (!writeBackStage.isBusy() && instruction.getStartClockCycleForCurrentStage() == 0) {
                                    instruction.setEndClockCycleForCurrentStage(clockCycle + executionStage.getClockCycleReq(instruction) - 1);
                                    instruction.setStartClockCycleForCurrentStage(clockCycle);
                                    writeBackStage.setBusy(true);
                                }
                                if (instruction.getEndClockCycleForCurrentStage() == clockCycle) {
                                    instruction.setClockCycleWB(clockCycle);
                                    instruction.setCurrentStage(FINISH);
                                    instruction.setStartClockCycleForCurrentStage(0);
                                    writeBackStage.setBusy(false);
                                }
                            }
                            break;
                        case FINISH:
                            if(program.getInstructions().get(program.getInstructions().size() - 1) == instruction)
                                System.exit(0);
                            break;
                    }
            }
            clockCycle ++;
        }
    }

    private static void initializeMemory(List<Mem> data) {
        ClassFactory.initMemory(data);
    }

    private static void initializeRegisters(List<Reg> regs) {
        ClassFactory.initRegister(regs);
    }
}
