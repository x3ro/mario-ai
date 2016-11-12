package de.x3ro.mariosarsa;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;

import javax.swing.*;

public final class Main {

    static JTextArea field;

    public static void main(String[] args) {

        JFrame fenster = new JFrame("Logger");
        fenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        field = new JTextArea();
        JScrollPane scroll = new JScrollPane(field);


        fenster.getContentPane().add(scroll);
        fenster.setSize(300, 200);
        fenster.setVisible(true);



        final String argsString = "-vis on -zm 0 -vw 640 -vh 480";
        final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
        marioAIOptions.setFPS(100);
        marioAIOptions.setViewWidth(640);
        marioAIOptions.setViewHeight(480);
        //marioAIOptions.setEnemies("off");


        //final Environment environment = new MarioEnvironment();
        final Agent agent = new MyAgentImpl();
        //final Agent agent = new MyKeyboardAgent();

        //final Agent agent = new HumanKeyboardAgent();
        marioAIOptions.setAgent(agent);

        //final Agent agent = marioAIOptions.getAgent();
        //final Agent a = AgentsPool.loadAgent("ch.idsia.controllers.agents.controllers.ForwardJumpingAgent");
        final BasicTask basicTask = new BasicTask(marioAIOptions);

        for (int i = 0; i < 10; ++i) {
            int seed = 0;
            do {
                //marioAIOptions.setLevelDifficulty(i);
                //marioAIOptions.setLevelRandSeed(seed++);
                basicTask.setOptionsAndReset(marioAIOptions);

                basicTask.runSingleEpisode(1);

                //basicTask.doEpisodes(1,true,1);
                System.out.println(basicTask.getEnvironment().getEvaluationInfoAsString());
            } while (basicTask.getEnvironment().getEvaluationInfo().marioStatus != Environment.MARIO_STATUS_WIN);
        }

        System.exit(0);
    }

    synchronized static void log(String s) {
        int start = field.getSelectionStart();
        int end = field.getSelectionEnd();

        field.setText(field.getText() + "\n" + s);

        field.setSelectionStart(start);
        field.setSelectionEnd(end);
    }

}
