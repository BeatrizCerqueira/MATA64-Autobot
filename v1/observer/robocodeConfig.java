package autobot.v1.observer;

import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.IChromosome;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.MutationOperator;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

// without GA
public class robocodeConfig extends FitnessFunction {
    public static final int NUMBER_OF_ROUNDS = 3;
    public static int robotScore, enemyScore;


    public static void main(String[] args) throws Exception {
        new robocodeConfig().run(); // run main
    }

    public void run() throws Exception {

        Configuration conf = new DefaultConfiguration(); // setup GA with default config
        conf.addGeneticOperator(new MutationOperator(conf, 100)); // add new crossover opp 1/10% rate to the GA
        conf.setPreservFittestIndividual(true); // use elitsim
        conf.setFitnessFunction(this); // Set fitness function to conf

        buildRobot(); //pass best solution to build
//        runBattle();

        System.exit(0); // clean exit
    }

    private void buildRobot() {
        int i = 0;
        create();     // create robot - func in createRobot.java
    }

    public static void create() {

        System.out.println("Criou o robo");
        compile(); // now compile it
        System.out.println("aqui compilou");
    }

    public static void compile() {
        System.out.println("entrou no compile()");
        System.out.println(System.getProperty("java.home"));

//        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        // robots/custom/JoselitoBot.java
        // D:\Documentos\UFCG\IA\robocode-jgap-template\robots\custom
        // String fileToCompile = "robots/custom/JoselitoBot.java"; // which file to compile * rhyming :) *

        // C:\robocode\robots\autobot\v1\drafts\lucas\Autobot.java
        // C:\robocode\robots\autobot\Autobot.java

        String fileToCompile = "autobot/Autobot.java"; // which file to compile * rhyming :) *
//        String fileToCompile = "autobot/v1/drafts/lucas/Autobot.java"; // which file to compile * rhyming :) *
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        System.out.println("vai rodar o run()?");
        compiler.run(null, null, null, fileToCompile); // run compile
        System.out.println("rodou!");
    }

    @Override
    protected double evaluate(IChromosome chromosome) {
//        int fitness, numberOfRounds = NUMBER_OF_ROUNDS;
//
//        buildRobot(); // build robot
//
//        RobocodeEngine engine = new RobocodeEngine(); // create robocode engine
//        engine.addBattleListener(new battleObserver()); // add battle listener to engine
//        engine.setVisible(false); // show battle in GUI ?
//
//        BattlefieldSpecification battlefield = new BattlefieldSpecification(800, 600); // battle field size
//        RobotSpecification[] selectedRobots = engine.getLocalRepository("sample.RamFire,sample.Walls,sample.Crazy"); // which sample bots to take to battle
//        BattleSpecification battleSpec = new BattleSpecification(numberOfRounds, battlefield, selectedRobots);
//
//        engine.runBattle(battleSpec, true); // run battle - wait till the battle is over
//        engine.close(); // clean up engine
//
//        fitness = robotScore; // set fitness score
//
//        return fitness > 0 ? fitness : 0; // return fitness score if it's over 0
        return 0;
    }

    public boolean battleResults(String name, int score) {
        String same = "autobot.Autobot*"; // enter robot name here with folder prefix

        //get results of battle
        if (name.equals(same)) {
            robotScore = score;
            return true;
        } else {
            enemyScore = score;
            return false;
        }
    }

    public void sortScore(int roboScore, int enemScore) {
        robotScore = roboScore;
        enemyScore = enemScore;
    }


}
