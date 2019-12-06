package com.harshild.mips.components;

import com.harshild.mips.in.Reg;
import lombok.Data;

import java.util.List;

@Data
public class RegisterFloat {
    private final List<Reg> regs;

    public RegisterFloat(List<Reg> regs) {
        this.regs = regs;
    }
}
