package com.harshild.mips.components;

import com.harshild.mips.in.Reg;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Register {
    private final List<Reg> regs;

    public Register(List<Reg> regs) {
        this.regs = regs;
    }
}
