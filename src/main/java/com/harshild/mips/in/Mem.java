package com.harshild.mips.in;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@AllArgsConstructor
@Data
public class Mem {
    int address;
    int data;
    boolean busy = false;

    public Mem(int address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mem mem = (Mem) o;
        return address == mem.address;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}
