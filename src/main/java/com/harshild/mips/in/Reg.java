package com.harshild.mips.in;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Reg {
    boolean busy;
    int value;
}
