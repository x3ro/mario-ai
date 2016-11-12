package de.x3ro.mariosarsa;

import ch.idsia.benchmark.mario.engine.sprites.Mario;

public enum Action {
    JUMP(0),
    LEFT(1),
    RIGHT(2),
    LEFT_JUMP(3),
    RIGHT_JUMP(4),
    LEFT_RUN(5),
    RIGHT_RUN(6),
    LEFT_JUMP_RUN(7),
    RIGHT_JUMP_RUN(8);
    //CROUCH(9);

    public int index;

    Action(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static Action forInt(int v) {
        return Action.values()[v];
    }

    public static boolean[] setKeys(boolean[] keys, Action action) {
        switch (action) {
            // Atomic actions
            case JUMP:
                keys[Mario.KEY_JUMP] = true;
                break;
            case LEFT:
                keys[Mario.KEY_LEFT] = true;
                break;
            case RIGHT:
                keys[Mario.KEY_RIGHT] = true;
                break;
            /*case CROUCH:
                keys[Mario.KEY_DOWN] = true;
                break;*/
            case LEFT_RUN:
                keys[Mario.KEY_LEFT] = true;
                keys[Mario.KEY_SPEED] = true;
                break;
            case RIGHT_RUN:
                keys[Mario.KEY_RIGHT] = true;
                keys[Mario.KEY_SPEED] = true;
                break;

            // Composed actions
            case LEFT_JUMP:
                setKeys(setKeys(keys, LEFT), JUMP);
                break;
            case RIGHT_JUMP:
                setKeys(setKeys(keys, RIGHT), JUMP);
                break;
            case LEFT_JUMP_RUN:
                setKeys(setKeys(keys, LEFT_RUN), JUMP);
                break;
            case RIGHT_JUMP_RUN:
                setKeys(setKeys(keys, RIGHT_RUN), JUMP);
                break;

            default:
                throw new RuntimeException("Unrecognized state...");
        }
        return keys;
    }
}
