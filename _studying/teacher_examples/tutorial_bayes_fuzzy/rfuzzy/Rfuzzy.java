package autobot._studying.teacher_examples.tutorial_bayes_fuzzy.rfuzzy;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.*;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * Rfuzzy - a robot by (your name here)
 */
public class Rfuzzy extends AdvancedRobot {
    /**
     * run: Rfuzzy's default behavior
     */
    final static String fclFileName = "./fcl/rules.fcl";
    public static final double ROBOT_SIZE = 36;
    private FunctionBlock poderTiro;


    //Instancia o controlador a partir das definições do arquivo FLC
    FIS fis = null;

    @Override
    public void run() {

        setColors(Color.green, Color.red, Color.red); // body,gun,radar
        fis = FIS.load(fclFileName);

        if (fis == null) {
            System.err.println("Erro ao carregar arquivo: '" + fclFileName + "'");
            return;
        }

        poderTiro = fis.getFunctionBlock("poder_tiro");

        while (true) {
            this.setAhead(50);
            this.setTurnLeft(45);
            this.execute();
        }
    }

    //Executado quando o radar do seu robô encontra um adversário.
    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        System.out.println("Teste ");

        if (isAim(event)) {
            poderTiro.setVariable("distanciaDoRobo", event.getDistance());
            poderTiro.setVariable("energiaInimiga", event.getEnergy());
            poderTiro.setVariable("minhaEnergia", getEnergy());

            poderTiro.evaluate();

            double poder = poderTiro.getVariable("poder").getValue();

            setFire(poder);

            System.out.println("Saída - " + poder);

        }

    }

    private boolean isAim(ScannedRobotEvent scannedRobot) {
        return Math.abs((scannedRobot.getBearing() + getHeading() + 360) % 360 - getGunHeading()) <= Math.abs(2 * 180 * Math.atan(ROBOT_SIZE * 0.5d / scannedRobot.getDistance() / Math.PI));
    }

}
