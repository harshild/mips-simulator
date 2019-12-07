package com.harshild.mips.manager;

import com.harshild.mips.AppConstants;
import com.harshild.mips.di.ClassFactory;
import com.harshild.mips.exception.ConfigurationReadErrorException;
import com.harshild.mips.in.*;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class InputManager {
    public static Instruction parseInstLine(String line, String lineRaw, int instIndex) throws Exception {
        line = StringUtils.normalizeSpace(line);
        String[] tokens = line.trim().split("[\\s]", 2);
        String opcode = tokens[0].trim();
        Instruction instruction = new Instruction();
        instruction.setInstructionName(opcode);
        instruction.setStringIns(line);
        instruction.setInsIndex(instIndex);
        instruction.setCurrentStage(AppConstants.ICACHE);
        instruction.setRawStringIns(lineRaw);
        return instruction;
    }

    public List<Mem> readData(String filePath) throws ConfigurationReadErrorException {
        List<Mem> memList = new ArrayList<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(filePath)));

            String line = null;

            int address = 0x100;
            while ((line = reader.readLine()) != null) {
                try {
                    if (!line.trim().equals("")) {
                        Mem mem = new Mem(address, Integer.parseInt(line.trim(), 2), false);
                        memList.add(mem);
                    }
                    address++;
                }catch (Exception e){
                    throw new ConfigurationReadErrorException(getErrorString(filePath, line));
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConfigurationReadErrorException(e.getMessage());
        }
        return memList;
    }

    public List<Reg> readReg(String filePath) throws ConfigurationReadErrorException {
        List<Reg> regs = new ArrayList<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(filePath)));

            String line = null;

            while ((line = reader.readLine()) != null) {
                try {
                    if (!line.trim().equals("")) {
                        Reg reg = new Reg(false, Integer.parseInt(line.trim(), 2));
                        regs.add(reg);
                    }
                }catch (Exception e){
                    throw new ConfigurationReadErrorException(getErrorString(filePath, line));
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConfigurationReadErrorException(e.getMessage());
        }
        return regs;
    }

    public List<Config> readConfig(String filePath) throws ConfigurationReadErrorException {
        List<Config> configList = new ArrayList<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(filePath)));

            String line = null;

            int address = 0x100;
            while ((line = reader.readLine()) != null) {
                try {
                    if (!line.trim().equals("")) {
                        String key = line.split(":")[0].trim();
                        String value = line.split(":")[1].trim();
                        Config config = new Config(key,
                                Integer.parseInt(value.split(",")[0].trim()),
                                value.split(",").length == 2 && value.split(",")[0].trim().toLowerCase().equals("yes"));
                        configList.add(config);
                    }
                    address++;
                }catch (Exception e){
                    throw new ConfigurationReadErrorException(getErrorString(filePath, line));
                }
            }
            reader.close();
        } catch (Exception e) {
            throw new ConfigurationReadErrorException(e.getMessage());
        }
        return configList;
    }

    private String getErrorString(String filePath, String line) {
        return "Issue in line "+line+" of file "+filePath;
    }

    public Program readInst(String filePath) throws ConfigurationReadErrorException {
        Program program = ClassFactory.getProgram();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(filePath)));

            String line = null;
            int instIndex = 0;
            Label currentLabel = null;

            while ((line = reader.readLine()) != null) {
                try {
                    line = line.trim();
                    String lineRaw = line;

                    if (line.contains(":")) {
                        currentLabel = new Label();
                        String[] split = line.split(":");
                        currentLabel.setName(split[0].trim());
                        currentLabel.setStartInstructionIndex(instIndex);
                        line = split[1].trim();
                    }

                    if ((line.startsWith("BNE") || line.startsWith("BEQ"))
                            && currentLabel != null) {
                        currentLabel.setEndInstructionIndex(instIndex);
                        program.addLabel(currentLabel);
                    }
                    program.addInstruction(parseInstLine(line, lineRaw, instIndex));
                    instIndex++;
                }catch (Exception e){
                    throw new ConfigurationReadErrorException(getErrorString(filePath, line));
                }
            }
            reader.close();
        } catch (Exception e) {
            throw new ConfigurationReadErrorException(e.getMessage());
        }
        return program;
    }

}
