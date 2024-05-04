package autobot.v1.drafts.lucas;

import org.jpl7.Query;
import org.jpl7.Term;

public class Prolog {

    static String filename = "Prolog.pl"; // C:/robocode/Prolog.pl

    static void checkSolution() {
        String consultCmd = String.format("consult('%s').", filename);
        if (!Query.hasSolution(consultCmd)) {
            System.out.println("Consult failed");
        }
    }

    static boolean isEnemyClose(double EnemyDistance, double LimitDistance) {
        // isEnemyClose(EnemyDistance, LimitDistance) :- less(EnemyDistance, LimitDistance)

        Query q = new Query("isEnemyClose", new Term[]{
                new org.jpl7.Float(EnemyDistance),
                new org.jpl7.Float(LimitDistance)
        });

        return q.hasSolution();
    }


}
