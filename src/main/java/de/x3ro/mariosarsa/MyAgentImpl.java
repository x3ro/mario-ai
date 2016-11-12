package de.x3ro.mariosarsa;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;


public class MyAgentImpl extends BasicMarioAIAgent implements Agent, MyAgent, KeyListener {


    /**
     * The amount of actions that is stored in the Sarsa Queue. The reward for the current
     * action will propagate through the elements in queue, with decaying significance (see
     * Sarsa Lambda algorithm for more information).
     */
    static final int SarsaLambdaLookback = 1;



    Environment environment;

    Action lastAction = null;
    int lastState = -1;

    // The mode is an index of the Mario.MODES array, "small", "Large" and "FIRE".
    // If the mode decreases (i.e. an enemy made mario smaller) there is a penalty.
    int lastMode;

    int noMoveCounter = 1;
    float lastPos = 0;
    int lastKilled = 0;

    int num_states;
    int ticks = -1;

    List<Double> distancePerTick;
    List<Integer> survivedTicks;
    int lastSurvivedTicks = 0;

    SarsaQueue<Tuple<Integer, Action>> lastActions;



    double xProgress = 0;


    public double[][] knowledge;

    public MyAgentImpl() {
        super("MyAgentImpl");

        distancePerTick = new ArrayList<Double>();
        survivedTicks = new ArrayList<Integer>();
        lastActions = new SarsaQueue<Tuple<Integer, Action>>(SarsaLambdaLookback);

        num_states = (int) Math.pow(2, State.getLength());
        System.out.println(num_states);
        knowledge = new double[num_states][Action.values().length];
        for (int i = 0; i < num_states; i++) {
            for (int j = 0; j < Action.values().length; j++) {

                knowledge[i][j] = 1.0;

            }
        }

        reset();
    }

    public void reset() {
        action = new boolean[Environment.numberOfKeys];
    }

    @Override
    public void integrateObservation(Environment environment) {
        super.integrateObservation(environment);
        this.environment = environment;
    }






    public boolean[] getAction() {
        int current = State.stateFromEnvironment(this);

        ticks++;
        lastSurvivedTicks++;
        if(ticks%3 != 0 && lastState == current) {
            return action;
        }

        if(ticks%30 == 0) {

            double currX = environment.getMarioFloatPos()[0];
            double progress = currX - xProgress;
            Main.log(""+currX+";");
            xProgress = currX;

        }

        reset();

        double[] qValues = knowledge[current];

        double max = Double.NEGATIVE_INFINITY;
        Action nextAction = null;
        for (int i = 0; i < Action.values().length; i++) {
            if (qValues[i] > max) {
                max = qValues[i];
                nextAction = Action.forInt(i);
            }
        }

        //System.out.println("next tick - max value " + max + " and nextAction = " + nextAction + " and current state " + Integer.toBinaryString(current));


        // Policy update
        if (lastAction != null) {
            double reward = 0;

            //System.out.println("Current State: " + Integer.toBinaryString(current) + " Next Action: " + nextAction);

            double xDiff = environment.getMarioFloatPos()[0] - lastPos;
            distancePerTick.add(xDiff);

            reward += xDiff;

            reward -= 1;

            //reward += (environment.getKillsTotal() - lastKilled) * 1000;



            // Penalty for getting hit by an enemy
            if(lastMode > environment.getMarioMode()) {
                //lastState |= State.ENEMY_CLOSE.getIndex();
                //System.out.println("Hit-penalty given for state " + Integer.toBinaryString(lastState) + " with last action" + lastAction);
                reward -= 50;
            }

            // Penalty for dying
            if(environment.getMarioStatus() == Mario.STATUS_DEAD) {
                //lastState |= State.ENEMY_CLOSE.getIndex();
                //System.out.println("death-penalty given for state " + Integer.toBinaryString(lastState) + " with last action" + lastAction);
                survivedTicks.add(lastSurvivedTicks);
                lastSurvivedTicks = 0;
                reward -= 50;
            }

            double alpha = 0.5;
            double gamma = 0.8;

            List<Tuple<Integer, Action>> actions = lastActions.maxLast(SarsaLambdaLookback);
            actions.add(new Tuple(current, nextAction));

            for(int i=1; i<actions.size(); i++) {
                Tuple<Integer, Action> last = actions.get(i-1);
                Tuple<Integer, Action> curr = actions.get(i);

                // Q(s, a)
                double q_s_a = knowledge[last.x][last.y.getIndex()];
                // Q(s', a')
                double q_s__a__ = knowledge[curr.x][curr.y.getIndex()];

                double lambda = Math.pow(0.8, i);
                knowledge[last.x][last.y.getIndex()] =
                        q_s_a + lambda * alpha * (reward + gamma * q_s__a__ - q_s_a);
            }





        }


        lastAction = nextAction;
        lastState = current;
        lastPos = environment.getMarioFloatPos()[0];
        lastMode = environment.getMarioMode();
        lastKilled = environment.getKillsTotal();

        lastActions.push(new Tuple(current, nextAction));

        return Action.setKeys(action, nextAction);
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

    @Override
    public void keyTyped(KeyEvent e) {

    }

    boolean bla = false;

    @Override
    synchronized public void keyPressed(KeyEvent e) {


        if(e.getKeyChar() != 'p') {
            return;
        }

        if(bla) {
            return;
        }
        bla = true;

        boolean first = true;
        System.out.println("size: " + survivedTicks.size());
        for(int d : survivedTicks) {
            System.out.print(d+"\\n");
        }
        //System.out.print(" }");

        bla = false;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
