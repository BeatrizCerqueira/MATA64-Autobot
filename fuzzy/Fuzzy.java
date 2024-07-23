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
        test_funcs();
    }

    private static void test_funcs() {
        loadFile("tippy.fcl");
        setFunctionBlock("tipper");

        // printa variáveis de entrada e saída
        // printChart();

        // Avalia valores específicos
        Variable service = new Variable("service");
        Variable food = new Variable("food");
        service.setValue(3);
        food.setValue(7);
        setVariables(service, food);

        evaluate();
        printChart();  //TODO: Printar valores resultante da faixa no gráfico

        // Resultado e defuzificação
        Variable tip = getVariable("tip");
        printDefuzzyChart(tip);

        System.out.println(tip.getValue());
        // é possível retornar o termo equivalente ao valor?

    }

    private static void test_tipper() {
        // Load from 'FCL' file
//        String fileName = "./autobot/fuzzy/rules.fcl";
        String fileName = "./autobot/fuzzy/tippy.fcl";
        FIS fis = FIS.load(fileName, true);

        // Error while loading?
        if (fis == null) {
            System.err.println("Can't load file: '" + fileName + "'");
            return;
        }

        // Success
        FunctionBlock functionBlock = fis.getFunctionBlock("tipper");

        // Show
        JFuzzyChart.get().chart(functionBlock);

        // Set inputs
        // TODO: testar com valores diferentes
        fis.setVariable("service", 3);
        fis.setVariable("food", 7);

        // Evaluate
        fis.evaluate();

        // Show output variable's chart
        Variable tip = functionBlock.getVariable("tip");
        JFuzzyChart.get().chart(tip, tip.getDefuzzifier(), true);

        // Print ruleSet
        System.out.println(fis);
    }

}
