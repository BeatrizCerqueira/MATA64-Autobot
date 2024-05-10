package autobot.v1.drafts.lucas;

import org.jpl7.Compound;
import org.jpl7.Query;
import org.jpl7.Term;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Prolog2 {
    public static void main(String[] args) {
        checkHasSolution("C:/robocode/Prolog.pl");
        testStr();
        testDouble();
    }

//    private static String queryBuilderWithVars(String ruleName, String... values) {
//        String valuesStr;
//        if (values.length == 0) {
//            valuesStr = "X";
//        } else {
//            valuesStr = String.join(", ", values).replace("null", "X");
//        }
//        return String.format("%s(%s)", ruleName, valuesStr);
//    }
//
//    private static String getOneSolution(String ruleName, String... values) {
//        String query = queryBuilderWithVars(ruleName, values);
//        System.out.print(query + " = ");
//        Query q = new Query(query);
//        return q.hasSolution() ? q.oneSolution().get("X").toString() : null;
//    }
//
//    private static List<String> getAllSolutions(String ruleName, String... values) {
//        List<String> solutions = new ArrayList<>();
//        String query = queryBuilderWithVars(ruleName, values);
//        System.out.print(query + " = ");
//        Query q = new Query(query);
//        for (Map<String, Term> solution : q.allSolutions())
//            solutions.add(solution.get("X").toString());
//        return solutions;
//    }

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

    // ============================= OLD =============================

    // TODO: untested
    static void checkHasSolution(String filepath) {
        if (!Query.hasSolution("consult", toTermArr(filepath))) {
            System.out.println("Consult failed");
        }
    }

    // TODO: untested
    static boolean isEnemyClose(double EnemyDistance, double LimitDistance) {
        // isEnemyClose(EnemyDistance, LimitDistance) :- less(EnemyDistance, LimitDistance)
        return isValid("isEnemyClose", EnemyDistance, LimitDistance);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SafeVarargs
    private static <T> Term[] toTermArr(T... values) {
        List<Term> termsList = new ArrayList<>();
        for (T value : values) {
            if (value instanceof Double) {
                termsList.add(new org.jpl7.Float((Double) value));
            } else if (value instanceof String) {
                termsList.add(new org.jpl7.Atom((String) value));
            }
        }
        return termsList.toArray(new Term[0]);
    }

    @SafeVarargs
    private static <T> void addFact(String ruleName, T... values) {
        Term[] terms = toTermArr(values);
        Term fact = new Compound(ruleName, terms);
        Query q = new Query("assert", fact);
        System.out.println(q);
        q.hasSolution();
    }

    @SafeVarargs
    private static <T> boolean isValid(String ruleName, T... values) {
        Term[] terms = toTermArr(values);
        Query q = new Query(ruleName, terms);
        System.out.print(q + " = ");
        return q.hasSolution();
    }

    @SafeVarargs
    private static <T> String getOneSolution(String ruleName, T... values) {
        Term[] terms = toTermArr(values);
        Query q = new Query(ruleName, terms);
        System.out.print(q + " = ");
        return q.hasSolution() ? q.oneSolution().get("X").toString() : null;
    }

    @SafeVarargs
    private static <T> List<String> getAllSolutions(String ruleName, T... values) {
        List<String> solutions = new ArrayList<>();
        Term[] terms = toTermArr(values);
        Query q = new Query(ruleName, terms);
        System.out.print(q + " = ");
        for (Map<String, Term> solution : q.allSolutions())
            solutions.add(solution.get("X").toString());
        return solutions;
    }

}
