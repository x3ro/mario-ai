package de.x3ro.mariosarsa;

import ch.idsia.benchmark.mario.environments.Environment;

public interface MyAgent {

    public int getMarioX();
    public int getMarioY();
    public boolean getFieldBlockedRelative(int x, int y);
    public boolean getFieldDangerRelative(int x, int y);
    public Environment getEnvironment();


}
