package com.harshild.mips;

import com.harshild.mips.in.Config;
import com.harshild.mips.in.Data;
import com.harshild.mips.in.Instruction;
import com.harshild.mips.di.ClassFactory;
import com.harshild.mips.exception.ConfigurationReadErrorException;
import com.harshild.mips.in.Reg;
import com.harshild.mips.manager.InputManager;

import java.util.List;

public class MipsSimulator {

    public static void main(String[] args) throws ConfigurationReadErrorException {
        InputManager inputManager = ClassFactory.getInputManager();

        List<Instruction> instructions = inputManager.readInst("inst.txt");
        List<Data> data = inputManager.readData("data.txt");
        List<Reg> regs = inputManager.readReg("reg.txt");
        List<Config> configs = inputManager.readConfig("config.txt");

        initializeRegisters(regs);
        initializeMemory(data);

        int clockCycle = 1;
        while(true){

            clockCycle ++;
        }
    }

    private static void initializeMemory(List<Data> data) {
        ClassFactory.initMemory(data);
    }

    private static void initializeRegisters(List<Reg> regs) {
        ClassFactory.initRegister(regs);
    }
}
