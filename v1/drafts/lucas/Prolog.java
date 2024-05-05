package autobot.v1.drafts.lucas;

import org.jpl7.Query;
import org.jpl7.Term;

import java.util.Arrays;

public class Prolog {
    public static void main(String[] args) {
        // For testing purposes only
        checkHasSolution("C:/robocode/Prolog.pl");
        System.out.println("Teste");
        System.out.println(isEnemyClose(30, 20));
    }

    static void checkHasSolution(String filepath) {
        String goal = String.format("consult('%s').", filepath);
        if (!Query.hasSolution(goal)) {
            System.out.println("Consult failed");
        }
    }

    // # Battle
    static void setBattle(double width, double height) {
        addFact("width", width);
        addFact("height", height);
    }

    static String getBattleWidth() {
        return getOneSolution("width");
    }

    static String getBattleHeight() {
        return getOneSolution("height");
    }

    // # Conditions
    static boolean isEnemyClose(double EnemyDistance, double LimitDistance) {
        // isEnemyClose(EnemyDistance, LimitDistance) :- less(EnemyDistance, LimitDistance)

        Term[] terms = new Term[]{
                new org.jpl7.Float(EnemyDistance),
                new org.jpl7.Float(LimitDistance)
        };

        return hasSolution("isEnemyClose", terms);
    }

    
    // # Utils
    private static boolean hasSolution(String ruleName, Term[] terms) {
        Query q = new Query(ruleName, terms);
        return q.hasSolution();
    }

    private static boolean hasSolution(String ruleName, String value) {
        String query = String.format("%s(%s)", ruleName, value);
        Query q = new Query(query);
        return q.hasSolution();
    }

    private static void addFact(String ruleName, double value) {
        String query = String.format("assert(%s(%f))", ruleName, value);
        Query q = new Query(query);
        q.hasSolution();
    }

    private static String getOneSolution(String ruleName) {
        Query q = new Query(ruleName + "(X)");
        return q.oneSolution().get("X").toString();
    }

    private static void getAllSolutions(String ruleName) {
        Query q = new Query(ruleName + "(X)");
        System.out.println(Arrays.toString(q.allSolutions()));
    }
}
