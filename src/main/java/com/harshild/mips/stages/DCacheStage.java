package com.harshild.mips.stages;

import com.harshild.mips.di.ClassFactory;
import com.harshild.mips.in.Instruction;
import com.harshild.mips.in.Reg;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.harshild.mips.AppConstants.ICACHE_BLOCK_SIZE;

@RequiredArgsConstructor
public class DCacheStage {
    private boolean busy = false;
    static String[][] dCache = new String[2][4];
    public static int accessCount = 0;
    public static int hitCount = 0;
    @NonNull
    int clockCycle;

    @NonNull
    int mainMemoryClockCycle;

    public int getClockCycleReq(Instruction instruction) {
        int registerIndex = Integer.parseInt(
                instruction.getString_ins()
                        .split("\\(R")[1]
                        .split("\\)")[0]);
        accessCount ++;
        if(isAHit(registerIndex)){
            hitCount ++;
            return clockCycle ;
        }else{
            addToDCache(registerIndex);
            return penalty();
        }
    }

    private void addToDCache(int registerIndex) {
        int row = (registerIndex / 4) % 2;
        int columnStartIndex = (registerIndex / 4 )*4;

        List<Instruction> instructions = ClassFactory.getProgram().getInstructions();
        for (int i = 0; i < 4; i++) {
            dCache[row][i] = String.valueOf(columnStartIndex + i);
        }

    }

    private int penalty() {
        return 2 * (clockCycle + mainMemoryClockCycle);
    }

    private boolean isAHit(int registerIndex) {
        int row = (registerIndex / 4) % 2;
        int column = (registerIndex / 4 ) + registerIndex % 4;

        return (dCache[row][column] != null && dCache[row][column].equalsIgnoreCase(String.valueOf(registerIndex)));
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}
