package com.harshild.mips.manager;

import com.harshild.mips.exception.ConfigurationReadErrorException;

import com.harshild.mips.in.Config;
import com.harshild.mips.in.Data;
import com.harshild.mips.in.Instruction;
import com.harshild.mips.in.Reg;

import java.util.List;

public class InputManager {
    public List<Data> readData(String filePath) throws ConfigurationReadErrorException{
        return null;
    }

    public List<Reg> readReg(String filePath) throws ConfigurationReadErrorException{
        return null;
    }

    public List<Config> readConfig(String filePath) throws ConfigurationReadErrorException{
        return null;
    }

    public List<Instruction> readInst(String filePath) throws ConfigurationReadErrorException{
        return null;
    }
}
