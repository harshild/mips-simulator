package com.harshild.mips.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@Getter
@RequiredArgsConstructor
public class Config {
    @NonNull
    String property;
    int clockCycle;
    boolean pipelined;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return property.equals(config.property);
    }

    @Override
    public int hashCode() {
        return Objects.hash(property);
    }
}
