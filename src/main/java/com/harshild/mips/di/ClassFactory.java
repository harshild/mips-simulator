package com.harshild.mips.di;

import com.harshild.mips.components.Memory;
import com.harshild.mips.components.Register;
import com.harshild.mips.in.Data;
import com.harshild.mips.in.Reg;
import com.harshild.mips.manager.InputManager;

import java.util.List;

public class ClassFactory {
    private static InputManager inputManager;
    private static Register register;
    private static Memory memory;

    public static InputManager getInputManager() {
        inputManager = inputManager == null ? new InputManager() : inputManager;
        return inputManager;
    }

    public static void initRegister(List<Reg> regs) {
        register = new Register(regs);
    }

    public static void initMemory(List<Data> data) {
        memory = new Memory(data);
    }
}
