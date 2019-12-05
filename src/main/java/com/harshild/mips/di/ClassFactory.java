package com.harshild.mips.di;

import com.harshild.mips.components.Memory;
import com.harshild.mips.components.Register;
import com.harshild.mips.in.Config;
import com.harshild.mips.in.Mem;
import com.harshild.mips.in.Program;
import com.harshild.mips.in.Reg;
import com.harshild.mips.manager.InputManager;
import com.harshild.mips.stages.*;

import java.util.List;

public class ClassFactory {
    private static InputManager inputManager;
    private static Register register;
    private static Memory memory;
    private static Program program;

    public static InputManager getInputManager() {
        inputManager = inputManager == null ? new InputManager() : inputManager;
        return inputManager;
    }

    public static void initRegister(List<Reg> regs) {
        register = new Register(regs);
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
        return new DecodeStage(configs);
    }

    public static ExecutionStage initExecutionStage(List<Config> configs) {
        return new ExecutionStage(configs);
    }

    public static WriteBackStage initWriteBackStage(List<Config> configs) {
        return new WriteBackStage(configs);
    }


    public static ICacheStage initICacheStage(List<Config> configs) {
        int clockCycle = configs.get(configs.indexOf(new Config("I-Cache"))).getClockCycle();
        return new ICacheStage(clockCycle);
    }

    public static DCacheStage initDCacheStage(List<Config> configs) {
        int clockCycle = configs.get(configs.indexOf(new Config("D-Cache"))).getClockCycle();
        return new DCacheStage(clockCycle);
    }
}
