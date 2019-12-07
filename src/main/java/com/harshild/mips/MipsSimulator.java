package com.harshild.mips;

import com.harshild.mips.di.ClassFactory;
import com.harshild.mips.exception.ConfigurationReadErrorException;
import com.harshild.mips.in.*;
import com.harshild.mips.manager.InputManager;
import com.harshild.mips.stages.*;
import com.harshild.mips.stages.execution.ExecutionManger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.harshild.mips.AppConstants.*;

public class MipsSimulator {
    static String instFile;
    static String dataFile;
    static String regFile;
    static String configFile;
    static String outFile;
    public static void main(String[] args) throws ConfigurationReadErrorException, IOException {
        InputManager inputManager = ClassFactory.getInputManager();

        instFile = "./test_cases/test_case_3/inst.txt";
        dataFile = "./test_cases/test_case_3/data.txt";
        regFile = "./test_cases/test_case_3/reg.txt";
        configFile = "./test_cases/test_case_3/config.txt";
        outFile = "./test_cases/test_case_3/result-new.txt";

        Program program = inputManager.readInst(instFile);
        List<Mem> data = inputManager.readData(dataFile);
        List<Reg> regs = inputManager.readReg(regFile);
        List<Config> configs = inputManager.readConfig(configFile);

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
                                if (fetchStage.isSpecialPurpose(instruction)) {
                                    instruction.setCurrentStage(ICACHE);
                                    instruction.setEndClockCycleForCurrentStage(clockCycle+1);

                                    if (program.getInstructions().get(instruction.getInsIndex() - 2).getCurrentStage().equals(FINISH)) {
                                        lastInstToEndId++;
                                        instruction.setClockCycleIF(clockCycle);
                                        instruction.setStartClockCycleForCurrentStage(0);
                                        instruction.setCurrentStage(FINISH);
                                        iCache.setBusy(false);
                                    }
                                }else {
                                    instruction.setStartClockCycleForCurrentStage(0);
                                    instruction.setClockCycleIF(clockCycle);
                                    iCache.setBusy(false);
                                }
                            }
                        }
                        break;
//                    case IF:
//                        if (ifUsedUp != clockCycle) {
//                            ifUsedUp = clockCycle;
//                            if (!fetchStage.isBusy() && instruction.getStartClockCycleForCurrentStage() == 0) {
//                                instruction.setEndClockCycleForCurrentStage(clockCycle + fetchStage.getClockCycleReq(instruction) - 1);
//                                instruction.setStartClockCycleForCurrentStage(clockCycle);
//                                fetchStage.setBusy(true);
//                            }
//                            if (instruction.getEndClockCycleForCurrentStage() == clockCycle) {
//                                instruction.setCurrentStage(ID);
//                                instruction.setStartClockCycleForCurrentStage(0);
//                                fetchStage.setBusy(false);
//                                instruction.setClockCycleIF(clockCycle);
//                            }
//                        }
//                        break;
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
                            instruction.setEndClockCycleForCurrentStage(clockCycle + dCacheStage.getClockCycleReq(instruction) - 1);
                            if(Arrays.asList("L.D","S.D").contains(instruction.getInstructionName())){
                                dCacheStage.getClockCycleReq(instruction);
                            }
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
                            List<String> output = formatOutput(program.getInstructions());

                            write(output,true,true);

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

    private static void write(List<String> output, boolean writeToFile, boolean writeToConsole) throws IOException {
        BufferedWriter writer = null;
        if(writeToFile)
            writer = new BufferedWriter(new FileWriter(new File(outFile)));
        for (String line : output) {
            if(writeToFile) {
                writer.write(line);
                writer.newLine();
            }
            if(writeToConsole)
                System.out.println(line);
        }

        if(writeToFile)
            writer.close();
    }

    private static List<String> formatOutput(List<Instruction> instructions) {
        String instructionOutputFormatString = "%-4s %-25s  %-4s  %-4s  %-4s  %-4s  %-4s  %-4s  %-4s  %-4s";
        List<String> output = new ArrayList<>();
        output.add(String.format(instructionOutputFormatString,
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
            output.add(String.format(instructionOutputFormatString,
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

        output.add("\nTotal number of access requests for instruction cache: " + ICacheStage.accessCount);
        output.add("\nNumber of instruction cache hits: " + ICacheStage.hitCount);
        output.add("\nTotal number of access requests for data cache: " + DCacheStage.accessCount);
        output.add("\nNumber of data cache hits: " + DCacheStage.hitCount);

        return output;
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
