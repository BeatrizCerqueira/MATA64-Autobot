package autobot.v1.drafts.lucas;

import autobot.v1.drafts.lucas.auxy.Consts;
import org.jpl7.Compound;
import org.jpl7.Query;
import org.jpl7.Term;
import robocode.Rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Prolog {
    public static void main(String[] args) {
//        checkHasSolution("C:/robocode/Prolog.pl");
//        testStr();
//        testDouble();
//        System.out.println("\n=================================");
//        addFact("amigos", "bia", 1.0);
//        System.out.println(isValid("amigos", "bia", 1.0));
    }

    // ============================ TEST ============================

    static void testStr() {
        System.out.println("\n============== STRING ===================");

        System.out.println("\n=================================");
        System.out.println("Adding facts...");

        addFact("amigos", "bia", "lucas");
        addFact("amigos", "bia", "gabriel");

        addFact("feliz", "bia");
        addFact("feliz", "lucas");

        System.out.println("\n=================================");
        System.out.println("Testing isValid...");

        System.out.println(isValid("amigos", "bia", "lucas"));
        System.out.println(isValid("amigos", "bia", "gabriel"));

        System.out.println(isValid("amigos", "lucas", "bia"));
        System.out.println(isValid("amigos", "gabriel", "bia"));

        System.out.println(isValid("amigos", "lucas", "gabriel"));
        System.out.println(isValid("amigos", "gabriel", "lucas"));

        System.out.println(isValid("feliz", "bia"));
        System.out.println(isValid("feliz", "lucas"));
        System.out.println(isValid("feliz", "gabriel"));

        System.out.println("\n=================================");
        System.out.println("\nTesting getOneSolution...");

        System.out.println(getOneSolution("amigos", null, "bia"));
        System.out.println(getOneSolution("amigos", null, "lucas"));
        System.out.println(getOneSolution("amigos", null, "gabriel"));

        System.out.println(getOneSolution("amigos", "bia", null));
        System.out.println(getOneSolution("amigos", "lucas", null));
        System.out.println(getOneSolution("amigos", "gabriel", null));

        System.out.println(getOneSolution("feliz"));

        System.out.println("\n=================================");
        System.out.println("\nTesting getAllSolutions...");

        System.out.println(getAllSolutions("amigos", null, "bia"));
        System.out.println(getAllSolutions("amigos", null, "lucas"));
        System.out.println(getAllSolutions("amigos", null, "gabriel"));

        System.out.println(getAllSolutions("amigos", "bia", null));
        System.out.println(getAllSolutions("amigos", "lucas", null));
        System.out.println(getAllSolutions("amigos", "gabriel", null));

        System.out.println(getAllSolutions("feliz"));
    }

    static void testDouble() {
        System.out.println("\n============== DOUBLE ===================");

        System.out.println("\n=================================");
        System.out.println("Adding facts...");

        addFact("amigos", 1.0, 2.0);
        addFact("amigos", 1.0, 3.0);

        addFact("feliz", 1.0);
        addFact("feliz", 2.0);

        System.out.println("\n=================================");
        System.out.println("Testing isValid...");

        System.out.println(isValid("amigos", 1.0, 2.0));
        System.out.println(isValid("amigos", 1.0, 3.0));

        System.out.println(isValid("amigos", 2.0, 1.0));
        System.out.println(isValid("amigos", 3.0, 1.0));

        System.out.println(isValid("amigos", 2.0, 3.0));
        System.out.println(isValid("amigos", 3.0, 2.0));

        System.out.println(isValid("feliz", 1.0));
        System.out.println(isValid("feliz", 2.0));
        System.out.println(isValid("feliz", 3.0));

        System.out.println("\n=================================");
        System.out.println("\nTesting getOneSolution...");

        System.out.println(getOneSolution("amigos", null, 1.0));
        System.out.println(getOneSolution("amigos", null, 2.0));
        System.out.println(getOneSolution("amigos", null, 3.0));

        System.out.println(getOneSolution("amigos", 1.0, null));
        System.out.println(getOneSolution("amigos", 2.0, null));
        System.out.println(getOneSolution("amigos", 3.0, null));

        System.out.println(getOneSolution("feliz"));

        System.out.println("\n=================================");
        System.out.println("\nTesting getAllSolutions...");

        System.out.println(getAllSolutions("amigos", null, 1.0));
        System.out.println(getAllSolutions("amigos", null, 2.0));
        System.out.println(getAllSolutions("amigos", null, 3.0));

        System.out.println(getAllSolutions("amigos", 1.0, null));
        System.out.println(getAllSolutions("amigos", 2.0, null));
        System.out.println(getAllSolutions("amigos", 3.0, null));

        System.out.println(getAllSolutions("feliz"));
    }

    // ============================= Robocode =============================

    static void loadPrologFile(String filepath) {
        if (!Query.hasSolution("consult", toTermArr(filepath))) {
            System.out.println("Consult failed");
        }
    }

    // isEnemyClose(EnemyDistance, SafeDistance) :- less(EnemyDistance, SafeDistance).
    static boolean isEnemyClose(double enemyDistance, double safeDistance) {
        return isValid("isEnemyClose", enemyDistance, safeDistance);
    }

    //    isRadarTurnComplete(RadarTurnRemaining) :- equal(RadarTurnRemaining, 0.0).
    static boolean isRadarTurnComplete(double radarTurnRemaining) {
        return isValid("isRadarTurnComplete", radarTurnRemaining);
    }

    //    shouldFire(FirePowerToHit, Energy, Rules_MIN_BULLET_POWER, Rules_MAX_BULLET_POWER, Consts_MIN_LIFE_TO_FIRE) :-
    //        greaterOrEqual(FirePowerToHit, Rules_MIN_BULLET_POWER),
    //        lessOrEqual(FirePowerToHit, Rules_MAX_BULLET_POWER),
    //        greater(Energy, Consts_MIN_LIFE_TO_FIRE).
    public static boolean shouldFire(double firePowerNeededToHit, double energy) {
        return isValid("shouldFire",
                firePowerNeededToHit,
                energy,
                Rules.MIN_BULLET_POWER,
                Rules.MAX_BULLET_POWER,
                Consts.MIN_LIFE_TO_FIRE);
    }


    //    isInXMargin(X, XLimit, Consts_WALL_MARGIN) :- XAbs is abs(X), less((XLimit - XAbs), Consts_WALL_MARGIN).
    static boolean isInXMargin(double x, double xLimit) {
        return isValid("isInXMargin", x, xLimit, Consts.WALL_MARGIN);
    }

    //    isInYMargin(Y, YLimit, Consts_WALL_MARGIN) :- YAbs is abs(Y), less((YLimit - YAbs), Consts_WALL_MARGIN).
    static boolean isInYMargin(double y, double yLimit) {
        return isValid("isInYMargin", y, yLimit, Consts.WALL_MARGIN);
    }


    //    isGunReady(Heat) :- Heat < 0.3.
    static boolean isGunReady(double heat) {
        return isValid("isGunReady", heat);
    }

    // hasEnemyFired(EnergyDecreased) :- EnergyDecreased > 0, EnergyDecreased =< 3.
    static boolean hasEnemyFired(double energyDecreased) {
        return isValid("hasEnemyFired", energyDecreased);
    }


    // ============================= Utils =============================

    @SafeVarargs
    private static <T> Term[] toTermArr(T... values) {
        List<Term> termsList = new ArrayList<>();
        if (values.length == 0) {
            termsList.add(new org.jpl7.Variable("X"));
        } else {
            for (T value : values) {
                if (value instanceof Double) {
                    termsList.add(new org.jpl7.Float((Double) value));
                } else if (value instanceof String) {
                    termsList.add(new org.jpl7.Atom((String) value));
                } else if (value == null) {
                    termsList.add(new org.jpl7.Variable("X"));
                }
            }
        }
        return termsList.toArray(new Term[0]);
    }

    @SafeVarargs
    private static <T> void addFact(String ruleName, T... values) {
        Term[] terms = toTermArr(values);
        Term fact = new Compound(ruleName, terms);
        Query q = new Query("assert", fact);
        q.hasSolution();
    }

    @SafeVarargs
    private static <T> boolean isValid(String ruleName, T... values) {
        Term[] terms = toTermArr(values);
        Query q = new Query(ruleName, terms);
        return q.hasSolution();
    }

    @SafeVarargs
    private static <T> String getOneSolution(String ruleName, T... values) {
        Term[] terms = toTermArr(values);
        Query q = new Query(ruleName, terms);
        return q.hasSolution() ? q.oneSolution().get("X").toString() : null;
    }

    @SafeVarargs
    private static <T> List<String> getAllSolutions(String ruleName, T... values) {
        List<String> solutions = new ArrayList<>();
        Term[] terms = toTermArr(values);
        Query q = new Query(ruleName, terms);
        for (Map<String, Term> solution : q.allSolutions())
            solutions.add(solution.get("X").toString());
        return solutions;
    }

}
