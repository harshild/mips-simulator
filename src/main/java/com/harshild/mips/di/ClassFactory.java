package com.harshild.mips.di;

import com.harshild.mips.components.Memory;
import com.harshild.mips.components.RegisterFloat;
import com.harshild.mips.components.RegisterInteger;
import com.harshild.mips.in.Config;
import com.harshild.mips.in.Mem;
import com.harshild.mips.in.Program;
import com.harshild.mips.in.Reg;
import com.harshild.mips.manager.InputManager;
import com.harshild.mips.stages.*;
import com.harshild.mips.stages.execution.ExecutionManger;

import java.util.ArrayList;
import java.util.List;

public class ClassFactory {
    private static InputManager inputManager;
    private static RegisterInteger registerInteger;
    private static RegisterFloat registerFloat;
    private static Memory memory;
    private static Program program;
    private static DCacheStage dCacheStage;
    private static ExecutionManger executionManger;
    private static DecodeStage decodeStage;

    public static DecodeStage getDecodeStage() {
        return decodeStage;
    }

    public static InputManager getInputManager() {
        inputManager = inputManager == null ? new InputManager() : inputManager;
        return inputManager;
    }

    public static void initRegister(List<Reg> regs) {
        registerInteger = new RegisterInteger(regs);

        List<Reg> reg_f = new ArrayList();
        for (int i = 0; i < regs.size(); i++) {
            reg_f.add(i, new Reg(false, 0));
        }
        registerFloat = new RegisterFloat(reg_f);
    }

    public static RegisterInteger getRegisterInteger() {
        return registerInteger;
    }

    public static RegisterFloat getRegisterFloat() {
        return registerFloat;
    }

    public static Memory getMemory() {
        return memory;
    }

    public static void initMemory(List<Mem> data) {
        memory = new Memory(data);
    }


    public static Program getProgram() {
        program = program == null ? new Program() : program;
        return program;
    }

    public static FetchStage initFetchStage(List<Config> configs) {
        return new FetchStage(configs);
    }

    public static DecodeStage initDecodeStage(List<Config> configs) {
        decodeStage = new DecodeStage(configs);
        return decodeStage;
    }

    public static ExecutionStage initExecutionStage(List<Config> configs) {
        return new ExecutionStage(configs);
    }

    public static WriteBackStage initWriteBackStage(List<Config> configs) {
        return new WriteBackStage(configs);
    }


    public static ICacheStage initICacheStage(List<Config> configs) {
        int clockCycle = configs.get(configs.indexOf(new Config("I-Cache"))).getClockCycle();
        int mainMemoryClockCycle = configs.get(configs.indexOf(new Config("Main memory"))).getClockCycle();
        return new ICacheStage(clockCycle, mainMemoryClockCycle);
    }

    public static DCacheStage initDCacheStage(List<Config> configs) {
        int clockCycle = configs.get(configs.indexOf(new Config("D-Cache"))).getClockCycle();
        int mainMemoryClockCycle = configs.get(configs.indexOf(new Config("Main memory"))).getClockCycle();
        dCacheStage = new DCacheStage(clockCycle, mainMemoryClockCycle);
        return dCacheStage;
    }

    public static DCacheStage getDCacheStage() {
        return dCacheStage;
    }

    public static ExecutionManger initExecutionManger(List<Config> configs) {
        executionManger = new ExecutionManger(configs);
        return executionManger;

    }

    public static ExecutionManger getExecutionManger() {
        return executionManger;

    }
}
