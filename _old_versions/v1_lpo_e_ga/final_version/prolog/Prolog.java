package autobot._old_versions.v1_lpo_e_ga.final_version.prolog;

import autobot._old_versions.v1_lpo_e_ga.final_version.utils.Consts;
import org.jpl7.Compound;
import org.jpl7.Query;
import org.jpl7.Term;
import robocode.Rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Prolog {

    // ============================= Robocode =============================

    public static void loadPrologFile(String filepath) {
        if (!Query.hasSolution("consult", toTermArr(filepath))) {
            System.out.println("Consult failed");
        }
    }

    // isEnemyClose(EnemyDistance, SafeDistance) :- less(EnemyDistance, SafeDistance).
    public static boolean isEnemyClose(double enemyDistance, double safeDistance) {
        return isValid("isEnemyClose", enemyDistance, safeDistance);
    }

    //    isRadarTurnComplete(RadarTurnRemaining) :- equal(RadarTurnRemaining, 0.0).
    public static boolean isRadarTurnComplete(double radarTurnRemaining) {
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
    public static boolean isInXMargin(double x, double xLimit) {
        return isValid("isInXMargin", x, xLimit, Consts.WALL_MARGIN);
    }

    //    isInYMargin(Y, YLimit, Consts_WALL_MARGIN) :- YAbs is abs(Y), less((YLimit - YAbs), Consts_WALL_MARGIN).
    public static boolean isInYMargin(double y, double yLimit) {
        return isValid("isInYMargin", y, yLimit, Consts.WALL_MARGIN);
    }

    //    isGunReady(Heat, Consts_GUN_HEAT_JUST_BEFORE_COOLING) :- less(Heat, Consts_GUN_HEAT_JUST_BEFORE_COOLING).
    public static boolean isGunReady(double heat) {
        return isValid("isGunReady", heat, Consts.GUN_HEAT_JUST_BEFORE_COOLING);
    }

    //    hasEnemyFired(EnergyDecreased, Rules_MIN_BULLET_POWER, Rules_MAX_BULLET_POWER) :-
    //        greaterOrEqual(EnergyDecreased, Rules_MIN_BULLET_POWER),
    //        lessOrEqual(EnergyDecreased, Rules_MAX_BULLET_POWER).
    public static boolean hasEnemyFired(double energyDecreased) {
        return isValid("hasEnemyFired", energyDecreased, Rules.MIN_BULLET_POWER, Rules.MAX_BULLET_POWER);
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
