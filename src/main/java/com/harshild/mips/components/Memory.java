package com.harshild.mips.components;

import com.harshild.mips.in.Mem;
import lombok.Data;

import java.util.List;

@Data
public class Memory {
    private final List<Mem> data;

    public Memory(List<Mem> data) {
        this.data = data;
    }
}
