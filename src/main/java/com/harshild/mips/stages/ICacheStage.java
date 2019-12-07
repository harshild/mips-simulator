package com.harshild.mips.stages;

import com.harshild.mips.di.ClassFactory;
import com.harshild.mips.in.Instruction;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.harshild.mips.AppConstants.ICACHE_BLOCK_SIZE;

@NoArgsConstructor
@RequiredArgsConstructor
public class ICacheStage {
    public static int accessCount;
    public static int hitCount;
    static String[][] iCache = new String[ICACHE_BLOCK_SIZE][ICACHE_BLOCK_SIZE];
    boolean busy = false;
    @NonNull
    int clockCycle;
    @NonNull
    int mainMemoryClockCycle;

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public int getClockCycleReq(Instruction instruction) {
        accessCount++;
        if (isAHit(instruction)) {
            hitCount++;
            return clockCycle;
        } else {
            addToICache(instruction);
            return penalty();
        }
    }

    private void addToICache(Instruction instruction) {
        int insIndex = instruction.getInsIndex();
        int index = (insIndex / ICACHE_BLOCK_SIZE) % ICACHE_BLOCK_SIZE;
        List<Instruction> instructions = ClassFactory.getProgram().getInstructions();
        for (int i = 0; i < ICACHE_BLOCK_SIZE; i++) {
            if (instructions.size() > insIndex + i) {
                iCache[index][i] = instructions.get(insIndex + i).getStringIns();
            }
        }

    }

    private int penalty() {
        return 2 * (clockCycle + mainMemoryClockCycle);
    }

    private boolean isAHit(Instruction instruction) {
        int insIndex = instruction.getInsIndex();
        int index = (insIndex / ICACHE_BLOCK_SIZE) % ICACHE_BLOCK_SIZE;
        List<Instruction> instructions = ClassFactory.getProgram().getInstructions();


        for (int i = 0; i < ICACHE_BLOCK_SIZE; i++) {
            for (int j = 0; j < ICACHE_BLOCK_SIZE; j++) {
                if (iCache[i][j] != null && iCache[i][j].equalsIgnoreCase(instruction.getStringIns())) {
                    for (int k = 0; k < ICACHE_BLOCK_SIZE; k++) {
                        if (iCache[i][k] != null && instructions.size() > instruction.getInsIndex() - j + k && !iCache[i][k].equals(instructions.get(instruction.getInsIndex() - j + k).getStringIns())) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
