package com.harshild.mips.in;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Mem {
    int address;
    int data;
    boolean busy =false;
}
