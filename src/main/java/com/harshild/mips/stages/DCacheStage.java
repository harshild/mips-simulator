package com.harshild.mips.stages;

import com.harshild.mips.in.Instruction;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DCacheStage {
    public static int accessCount = 0;
    public static int hitCount = 0;
    static String[][] dCache = new String[2][4];
    static int recentUsed = 0;
    @NonNull
    int clockCycle;
    @NonNull
    int mainMemoryClockCycle;
    private boolean busy = false;

    public int getClockCycleReq(Instruction instruction) {
        int registerIndex = Integer.parseInt(
                instruction.getStringIns()
                        .split("\\(R")[1]
                        .split("\\)")[0]);
        accessCount++;
        if (isAHit(registerIndex)) {
            hitCount++;
            return clockCycle;
        } else {
            addToDCache(registerIndex);
            return penalty();
        }
    }

    private void addToDCache(int registerIndex) {
        int columnStartIndex = ((registerIndex - 1) / 4) * 4;

        int canBeReplaced = recentUsed == 0 ? 1 : 0;
        for (int i = 0; i < 4; i++) {
            dCache[canBeReplaced][i] = String.valueOf(columnStartIndex + i + 1);
        }
        recentUsed = canBeReplaced;

    }

    private int penalty() {
        return 2 * (clockCycle + mainMemoryClockCycle);
    }

    private boolean isAHit(int registerIndex) {
        int columnStartIndex = ((registerIndex - 1) / 4) * 4;

        for (int i = 0; i < 2; i++) {
            if (dCache[i][0] != null && dCache[i][0].equals(String.valueOf(columnStartIndex + 1))) {
                if (dCache[i][registerIndex - columnStartIndex - 1].equals(String.valueOf(registerIndex)))
                    recentUsed = i;
                return true;
            }
        }

        return false;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}
