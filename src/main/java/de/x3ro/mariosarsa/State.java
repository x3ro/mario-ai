package de.x3ro.mariosarsa;



public enum State {
    BLOCKED(1),
    JUMP_ONTO_LOW(2), // On the next tile, there is a block height 2 or lower (relative to Mario)
    JUMP_ONTO_HIGH(4), // On the next tile, there is a block height between 2 and 4 (relative to Mario)
    BEFORE_GAP(8), // Mario stands before a gap
    CANT_JUMP(16),
    ENEMY_RIGHT(32),
    ENEMY_LEFT(64),
    ENEMY_TOP(128);


    public int index;
    public static int length = -1;

    State(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    /**
     * @return The number of available states
     */
    public static int getLength() {
        if(length == -1) {
            length = State.values().length;
        }
        return length;
    }


    public static int stateFromEnvironment(MyAgent agent) {
        int state = 0;
        state = isBlocked(agent, state);
        state = isJumpOntoLow(agent, state);
        state = isJumpOntoHigh(agent, state);
        state = isBeforeGap(agent, state);
        state = isCantJump(agent, state);
        state = isEnemyRight(agent, state);
        state = isEnemyLeft(agent, state);
        state = isEnemyTop(agent, state);
        return state;
    }





    private static int isBlocked(MyAgent agent, int state) {
        if(agent.getFieldBlockedRelative(1, 0)) {
            return state | BLOCKED.getIndex();
        }
        return state;
    }

    private static int isJumpOntoLow(MyAgent agent, int state) {
        if(agent.getFieldBlockedRelative(1, 0) && !agent.getFieldBlockedRelative(1, 1)) {
            return state | JUMP_ONTO_LOW.getIndex();
        }
        return state;
    }

    private static int isJumpOntoHigh(MyAgent agent, int state) {
        if(
                (agent.getFieldBlockedRelative(1, 1) && !agent.getFieldBlockedRelative(1, 2)) ||
                (agent.getFieldBlockedRelative(1, 2) && !agent.getFieldBlockedRelative(1, 3)) ||
                (agent.getFieldBlockedRelative(1, 3) && !agent.getFieldBlockedRelative(1, 4))
        ) {
            return state | JUMP_ONTO_HIGH.getIndex();
        }
        return state;
    }

    private static int isBeforeGap(MyAgent agent, int state) {
        // If we are not currently jumping and the tile to the bottom-right is air, there is a gap
        if(agent.getFieldBlockedRelative(0, -1) && !agent.getFieldBlockedRelative(1, -1)) {
            return state | BEFORE_GAP.getIndex();
        }
        return state;
    }

    private static int isCantJump(MyAgent agent, int state) {
        // Two above mario, because he is two fields high
        if(agent.getFieldBlockedRelative(0, 2)) {
            return state | CANT_JUMP.getIndex();
        }
        return state;
    }

    private static int isEnemyRight(MyAgent agent, int state) {
        if(agent.getFieldDangerRelative(1, 0)) {
            return state | ENEMY_RIGHT.getIndex();
        }
        return state;
    }

    private static int isEnemyLeft(MyAgent agent, int state) {
        if(agent.getFieldDangerRelative(-1, 0)) {
            return state | ENEMY_LEFT.getIndex();
        }
        return state;
    }

    private static int isEnemyTop(MyAgent agent, int state) {
        if(agent.getFieldDangerRelative(-1, 1)
                || agent.getFieldDangerRelative(0, 1)
                || agent.getFieldDangerRelative(1, 1)) {
            return state | ENEMY_TOP.getIndex();
        }
        return state;
    }

    /*private static int isEnemyClose(MyAgent agent, int state) {
        if(agent.getFieldDangerRelative(1, 0)
                || agent.getFieldDangerRelative(0, 0)
                || agent.getFieldDangerRelative(1, 1)
                || agent.getFieldDangerRelative(0, 1)
                || agent.getFieldDangerRelative(-1, 1)
                || agent.getFieldDangerRelative(-1, 0)) {
            return state | ENEMY_CLOSE.getIndex();
        }
        return state;
    }

    private static int isEnemyFar(MyAgent agent, int state) {
        if(agent.getFieldDangerRelative(-2, 0)
                || agent.getFieldDangerRelative(-2, 1)
                || agent.getFieldDangerRelative(-2, 2)
                || agent.getFieldDangerRelative(-1, 2)
                || agent.getFieldDangerRelative(0, 2)
                || agent.getFieldDangerRelative(1, 2)
                || agent.getFieldDangerRelative(2, 2)
                || agent.getFieldDangerRelative(2, 1)
                || agent.getFieldDangerRelative(2, 0)) {
            return state | ENEMY_FAR.getIndex();
        }
        return state;
    }*/
}
