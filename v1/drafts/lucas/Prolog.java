package autobot.v1.drafts.lucas;

import org.jpl7.Query;
import org.jpl7.Term;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Prolog {
    public static void main(String[] args) {
        // test();
    }

    private static String queryBuilder(String ruleName, String... values) {
        String valuesStr = String.join(", ", values);
        return String.format("%s(%s)", ruleName, valuesStr);
    }

    private static String queryBuilderWithVars(String ruleName, String... values) {
        String valuesStr;
        if (values.length == 0) {
            valuesStr = "X";
        } else {
            valuesStr = String.join(", ", values).replace("null", "X");
        }
        return String.format("%s(%s)", ruleName, valuesStr);
    }

    private static void addFact(String ruleName, String... values) {
        String fact = queryBuilder(ruleName, values);
        String query = String.format("assert(%s)", fact);
        System.out.println(query);
        Query q = new Query(query);
        q.hasSolution();
    }

    private static boolean isValid(String ruleName, String... values) {
        String query = queryBuilder(ruleName, values);
        System.out.print(query + " = ");
        Query q = new Query(query);
        return q.hasSolution();
    }

    private static String getOneSolution(String ruleName, String... values) {
        String query = queryBuilderWithVars(ruleName, values);
        System.out.print(query + " = ");
        Query q = new Query(query);
        return q.hasSolution() ? q.oneSolution().get("X").toString() : null;
    }

    private static List<String> getAllSolutions(String ruleName, String... values) {
        List<String> solutions = new ArrayList<>();
        String query = queryBuilderWithVars(ruleName, values);
        System.out.print(query + " = ");
        Query q = new Query(query);
        for (Map<String, Term> solution : q.allSolutions())
            solutions.add(solution.get("X").toString());
        return solutions;
    }

    // ============================ TEST ============================

    static void test() {
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


    // ============================= OLD =============================

    static void checkHasSolution(String filepath) {
        String goal = String.format("consult('%s').", filepath);
        if (!Query.hasSolution(goal)) {
            System.out.println("Consult failed");
        }
    }

    static boolean isEnemyClose(double EnemyDistance, double LimitDistance) {
        // isEnemyClose(EnemyDistance, LimitDistance) :- less(EnemyDistance, LimitDistance)
        Term[] terms = new Term[]{
                new org.jpl7.Float(EnemyDistance),
                new org.jpl7.Float(LimitDistance)
        };
        return hasSolution("isEnemyClose", terms);
    }

    private static boolean hasSolution(String ruleName, Term[] terms) {
        Query q = new Query(ruleName, terms);
        return q.hasSolution();
    }

}
