package autobot.fuzzy;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

// https://jfuzzylogic.sourceforge.net/html/manual.html

public class Fuzzy {
    private static FIS fis = null;
    private static FunctionBlock functionBlock;

    public static void loadFile(String filename) {
        String filePath = "./autobot/fuzzy/" + filename;
        fis = FIS.load(filePath);

        if (fis == null) {
            System.err.println("Can't load file: '" + filePath + "'");
        }
    }

    public static void setFunctionBlock(String blockName) {
        functionBlock = fis.getFunctionBlock(blockName);
    }

    public static void printChart() {
        JFuzzyChart.get().chart(functionBlock);
    }

    public static void setVariables(Variable... variables) {
        for (Variable variable : variables) {
            fis.setVariable(variable.getName(), variable.getValue());
        }
    }

    public static void evaluate() {
        fis.evaluate();
    }

    public static Variable getVariable(String variableName) {
        return functionBlock.getVariable(variableName);
    }

    public static void printDefuzzyChart(Variable var) {
        // Show output variable's chart
        JFuzzyChart.get().chart(var, var.getDefuzzifier(), true);
    }

    // ============ DEBUGS ===========
    public static void main(String[] args) {
        test_rules();
    }

    private static void test_rules() {
        loadFile("autobot.fcl");
        setFunctionBlock("escape");

        // Adiciona valores às variáveis

//        Variable distance = new Variable("distance");
//        Variable enemy_energy = new Variable("enemy_energy");
//        Variable our_energy = new Variable("our_energy");
//        Variable enemy_gun_heat = new Variable("enemy_gun_heat");
//
//        distance.setValue(100);
//        enemy_energy.setValue(50);
//        our_energy.setValue(50);
//        enemy_gun_heat.setValue(1);
//
//        setVariables(distance, enemy_energy, our_energy, enemy_gun_heat);
//
        printChart();
//
//        evaluate();
//
//        // Resultado e defuzificação
//        Variable escape = getVariable("should_i_stay_or_should_i_go");
//        printDefuzzyChart(escape);
    }

    private static void test_bot() {
        loadFile("autobot.fcl");
        setFunctionBlock("should_i_stay_or_should_i_go");

        // Adiciona valores às variáveis

        Variable distance = new Variable("distance");
        Variable enemy_energy = new Variable("enemy_energy");
        Variable our_energy = new Variable("our_energy");
        Variable enemy_gun_heat = new Variable("enemy_gun_heat");

        distance.setValue(100);
        enemy_energy.setValue(50);
        our_energy.setValue(50);
        enemy_gun_heat.setValue(1);

        setVariables(distance, enemy_energy, our_energy, enemy_gun_heat);

        printChart();

        evaluate();

        // Resultado e defuzificação
        Variable escape = getVariable("should_i_stay_or_should_i_go");
        printDefuzzyChart(escape);
    }

    private static void test_funcs() {
        loadFile("tippy.fcl");
        setFunctionBlock("tipper");

        // printa variáveis de entrada e saída
        // printChart();

        // Avalia valores específicos
        Variable service = new Variable("service");
        Variable food = new Variable("food");
        service.setValue(10);
        food.setValue(5);
        setVariables(service, food);

        evaluate();
        printChart();

        // Resultado e defuzificação
        Variable tip = getVariable("tip");
        printDefuzzyChart(tip);

        System.out.println(tip.getValue());
        // é possível retornar o termo equivalente ao valor?

    }

}
