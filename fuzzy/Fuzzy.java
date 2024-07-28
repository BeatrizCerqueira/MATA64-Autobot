package autobot.fuzzy;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import java.util.ArrayList;

// https://jfuzzylogic.sourceforge.net/html/manual.html

public class Fuzzy {
    private static FIS fis = null;
    private static FunctionBlock functionBlock;
    private static ArrayList<Variable> variables = new ArrayList<Variable>();
    private static Variable result;

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

    public static void setVariables() {
        for (Variable variable : variables) {
            fis.setVariable(variable.getName(), variable.getValue());
        }
    }

//    public static void setVariables(Variable... variables) {
//        for (Variable variable : variables) {
//            fis.setVariable(variable.getName(), variable.getValue());
//        }
//    }


    // ========== ROBOCODE ===========

    public static void init() {
        loadFile("autobot.fcl");
        setFunctionBlock("escape");
        setDefuzzyVariable("life_risk");
        initVariables("distance", "enemy_energy", "autobot_energy");
    }

    public static void initVariables(String... names) {
        variables.clear();
        for (String name : names) {
            variables.add(new Variable(name));
        }

        getVariablesNames();
    }

    public static void getVariablesNames() {
        System.out.print("Variáveis fuzzy: ");
        for (Variable var : variables) {
            System.out.print(var.getName() + " ");
        }
        System.out.println();
    }


    public static void setFuzzyValues(Double... values) {
        System.out.println(values[0]);
        for (int i = 0; i < variables.size(); i++) {
            variables.get(i).setValue(values[i]);
        }
    }


    public static void setDefuzzyVariable
            (String variableName) {
        result = functionBlock.getVariable(variableName);
    }

    public static double getDefuzzyValue() {
        setVariables();
        fis.evaluate();
        return result.getValue();
    }

    public static void printCharts() {
        JFuzzyChart.get().chart(functionBlock);
        JFuzzyChart.get().chart(result, result.getDefuzzifier(), true);

    }

    public static void main(String[] args) {
        init();
//        initVariables("distance", "enemy_energy", "autobot_energy", "enemy_gun_heat");
        setFuzzyValues(100.0, 15.0, 10.0, 2.0);
        System.out.println(getDefuzzyValue());
        printCharts();
    }

// ============ DEBUGS ===========

//    private static void test_bot() {
//        loadFile("autobot.fcl");
//        setFunctionBlock("escape");
//
//        // Adiciona valores às variáveis
//
//        Variable distance = new Variable("distance");
//        Variable enemy_energy = new Variable("enemy_energy");
//        Variable autobot_energy = new Variable("autobot_energy");
//        Variable enemy_gun_heat = new Variable("enemy_gun_heat");
//
//        distance.setValue(200);
//        enemy_energy.setValue(15);
//        autobot_energy.setValue(10);
//        enemy_gun_heat.setValue(2);
//
//        setVariables(distance, enemy_energy, autobot_energy, enemy_gun_heat);
//
//        printChart();
//
//        evaluate();
//
////        // Resultado e defuzificação
//        Variable escape = getVariable("life_risk");
//        printDefuzzyChart(escape);
//        System.out.println(escape.getValue());
//
//    }

//    private static void test_funcs() {
//        loadFile("tippy.fcl");
//        setFunctionBlock("tipper");
//
//        // printa variáveis de entrada e saída
//        // printChart();
//
//        // Avalia valores específicos
//        Variable service = new Variable("service");
//        Variable food = new Variable("food");
//        service.setValue(10);
//        food.setValue(5);
//        setVariables(service, food);
//
//        evaluate();
//        printChart();
//
//        // Resultado e defuzificação
//        Variable tip = getVariable("tip");
//        printDefuzzyChart(tip);
//
//        System.out.println(tip.getValue());
//        // é possível retornar o termo equivalente ao valor?
//
//    }

}
