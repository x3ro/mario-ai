package de.x3ro.mariosarsa;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;
//import com.sun.servicetag.SystemEnvironment;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class MyKeyboardAgent extends BasicMarioAIAgent implements Agent, MyAgent, KeyListener
{
    List<boolean[]> history = new ArrayList<boolean[]>();
    private boolean[] Action = null;
    private String Name = "HumanKeyboardAgent";

    /*final*/
    protected byte[][] levelScene;
    /*final */
    protected byte[][] enemies;
    protected byte[][] mergedObservation;

    protected float[] marioFloatPos = null;
    protected float[] enemiesFloatPos = null;

    protected int[] marioState = null;

    protected int marioStatus;
    protected int marioMode;
    protected boolean isMarioOnGround;
    protected boolean isMarioAbleToJump;
    protected boolean isMarioAbleToShoot;
    protected boolean isMarioCarrying;
    protected int getKillsTotal;
    protected int getKillsByFire;
    protected int getKillsByStomp;
    protected int getKillsByShell;
    // values of these variables could be changed during the Agent-Environment interaction.
// Use them to get more detailed or less detailed description of the level.
// for information see documentation for the benchmark <link: marioai.org/marioaibenchmark/zLevels
    int zLevelScene = 2;
    int zLevelEnemies = 0;

    public Environment environment;


    public MyKeyboardAgent()
    {
        super("MyKeyboardAgent");
        this.reset();
    }

    public boolean[] getAction()
    {
        System.out.println("State: " + Integer.toBinaryString(State.stateFromEnvironment(this)));
        return Action;
    }

    public void integrateObservation(Environment environment)
    {
        super.integrateObservation(environment);
        this.environment = environment;

        levelScene = environment.getLevelSceneObservationZ(1);
        enemies = environment.getEnemiesObservationZ(2);
        mergedObservation = environment.getMergedObservationZZ(1, 2);

        /*byte[][] test = environment.getEnemiesObservationZ(1);
        for(int x=0; x<test.length; x++) {
            for(int y=0; y<test[x].length; y++) {
                if(environment.getMarioEgoPos()[0] == x && environment.getMarioEgoPos()[1] == y) {
                    System.out.print("M ");
                } else {
                    System.out.print(test[x][y] != 0 ? "x " : "o ");
                }
            }
            System.out.println("");
        }*/


        this.marioFloatPos = environment.getMarioFloatPos();
        this.enemiesFloatPos = environment.getEnemiesFloatPos();
        this.marioState = environment.getMarioState();

        // It also possible to use direct methods from Environment interface.
        //
        marioStatus = marioState[0];
        marioMode = marioState[1];
        isMarioOnGround = marioState[2] == 1;
        isMarioAbleToJump = marioState[3] == 1;
        isMarioAbleToShoot = marioState[4] == 1;
        isMarioCarrying = marioState[5] == 1;
        getKillsTotal = marioState[6];
        getKillsByFire = marioState[7];
        getKillsByStomp = marioState[8];
        getKillsByShell = marioState[9];
    }

    public void giveIntermediateReward(float intermediateReward)
    {

    }

    public void reset()
    {
        // Just check you keyboard. Especially arrow buttons and 'A' and 'S'!
        Action = new boolean[Environment.numberOfKeys];
    }

    public void setObservationDetails(final int rfWidth, final int rfHeight, final int egoRow, final int egoCol)
    {}

    public boolean[] getAction(Environment observation)
    {
        float[] enemiesPos = observation.getEnemiesFloatPos();

        return Action;
    }

    public String getName() { return Name; }

    public void setName(String name) { Name = name; }


    @Override
    public void keyTyped(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void keyPressed(KeyEvent e)
    {
        toggleKey(e.getKeyCode(), true);
    }

    public void keyReleased(KeyEvent e)
    {
        toggleKey(e.getKeyCode(), false);
    }


    private void toggleKey(int keyCode, boolean isPressed)
    {
        switch (keyCode)
        {
            case KeyEvent.VK_LEFT:
                Action[Mario.KEY_LEFT] = isPressed;
                break;
            case KeyEvent.VK_RIGHT:
                Action[Mario.KEY_RIGHT] = isPressed;
                break;
            case KeyEvent.VK_DOWN:
                Action[Mario.KEY_DOWN] = isPressed;
                break;
            case KeyEvent.VK_UP:
                Action[Mario.KEY_UP] = isPressed;
                break;

            case KeyEvent.VK_S:
                Action[Mario.KEY_JUMP] = isPressed;
                break;
            case KeyEvent.VK_A:
                Action[Mario.KEY_SPEED] = isPressed;
                break;
        }
    }

    public List<boolean[]> getHistory()
    {
        return history;
    }








    @Override
    public int getMarioX() {
        return environment.getMarioEgoPos()[0];
    }

    @Override
    public int getMarioY() {
        return environment.getMarioEgoPos()[1];
    }

    @Override
    public boolean getFieldBlockedRelative(int x, int y) {
        int value;
        if(marioFacesRight()) {
            value = environment.getLevelSceneObservationZ(1)[getMarioY() - y][ getMarioX() + x];
        } else {
            value = environment.getLevelSceneObservationZ(1)[getMarioY() - y][ getMarioX() - x];
        }

        //     Air         Coin
        if(value == 0 || value == 2) {
            return false;
        }

        return true;
    }

    @Override
    public boolean getFieldDangerRelative(int x, int y) {
        int value;
        if(marioFacesRight()) {
            value = environment.getEnemiesObservationZ(1)[getMarioY() - y][ getMarioX() + x];
        } else {
            value = environment.getEnemiesObservationZ(1)[getMarioY() - y][ getMarioX() - x];
        }

        return value != 0;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public boolean marioFacesRight() {
        MarioEnvironment env = (MarioEnvironment) environment;
        return env.getMario().facing == 1;
    }
}
