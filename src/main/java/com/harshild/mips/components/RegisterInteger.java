package com.harshild.mips.components;

import com.harshild.mips.in.Reg;
import lombok.Data;

import java.util.List;

@Data
public class RegisterInteger {
    private final List<Reg> regs;

    public RegisterInteger(List<Reg> regs) {
        this.regs = regs;
    }
}
