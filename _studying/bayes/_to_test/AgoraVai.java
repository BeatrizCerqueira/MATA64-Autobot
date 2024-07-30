package autobot._studying.bayes._to_test;

import autobot._studying.bayes.InternalBayesNode;
import autobot._studying.bayes.JayesWrapper;
import autobot._studying.bayes.WekaWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("CommentedOutCode")
public class AgoraVai {

    private static List<InternalBayesNode> initInternalBayesNodes() {
        List<InternalBayesNode> internalNodes = new ArrayList<>();

        InternalBayesNode enemyDistance = new InternalBayesNode("EnemyDistance", autobot._studying.bayes.enums.EnemyDistance.class, new ArrayList<>());
        InternalBayesNode enemyVelocity = new InternalBayesNode("EnemyVelocity", autobot._studying.bayes.enums.EnemyVelocity.class, new ArrayList<>());
        InternalBayesNode enemyAngle = new InternalBayesNode("EnemyAngle", autobot._studying.bayes.enums.EnemyAngle.class, new ArrayList<>());
        InternalBayesNode enemyHeading = new InternalBayesNode("EnemyHeading", autobot._studying.bayes.enums.EnemyHeading.class, new ArrayList<>());
        InternalBayesNode myGunToEnemyAngle = new InternalBayesNode("MyGunToEnemyAngle", autobot._studying.bayes.enums.MyGunToEnemyAngle.class, new ArrayList<>());
        InternalBayesNode firePower = new InternalBayesNode("FirePower", autobot._studying.bayes.enums.FirePower.class, new ArrayList<>());
        InternalBayesNode hit = new InternalBayesNode("Hit", autobot._studying.bayes.enums.Hit.class, Arrays.asList("EnemyDistance", "EnemyVelocity", "EnemyAngle", "EnemyHeading", "MyGunToEnemyAngle", "FirePower"));

        internalNodes.add(enemyDistance);
        internalNodes.add(enemyVelocity);
        internalNodes.add(enemyAngle);
        internalNodes.add(enemyHeading);
        internalNodes.add(myGunToEnemyAngle);
        internalNodes.add(firePower);
        internalNodes.add(hit);

        return internalNodes;
    }

    private static void addSomeInstances(WekaWrapper weka, JayesWrapper jayes) throws Exception {
        weka.addInstance(autobot._studying.bayes.enums.EnemyDistance.RANGE_0_100, autobot._studying.bayes.enums.EnemyVelocity.RANGE_0_1, autobot._studying.bayes.enums.EnemyAngle.RANGE_0_45, autobot._studying.bayes.enums.EnemyHeading.RANGE_0_45, autobot._studying.bayes.enums.MyGunToEnemyAngle.RANGE_0_20, autobot._studying.bayes.enums.FirePower.FP_03, autobot._studying.bayes.enums.Hit.TRUE);
        weka.addInstance(autobot._studying.bayes.enums.EnemyDistance.RANGE_0_100, autobot._studying.bayes.enums.EnemyVelocity.RANGE_0_1, autobot._studying.bayes.enums.EnemyAngle.RANGE_0_45, autobot._studying.bayes.enums.EnemyHeading.RANGE_0_45, autobot._studying.bayes.enums.MyGunToEnemyAngle.RANGE_0_20, autobot._studying.bayes.enums.FirePower.FP_03, autobot._studying.bayes.enums.Hit.TRUE);
        weka.addInstance(autobot._studying.bayes.enums.EnemyDistance.RANGE_0_100, autobot._studying.bayes.enums.EnemyVelocity.RANGE_0_1, autobot._studying.bayes.enums.EnemyAngle.RANGE_0_45, autobot._studying.bayes.enums.EnemyHeading.RANGE_0_45, autobot._studying.bayes.enums.MyGunToEnemyAngle.RANGE_0_20, autobot._studying.bayes.enums.FirePower.FP_03, autobot._studying.bayes.enums.Hit.TRUE);
        weka.addInstance(autobot._studying.bayes.enums.EnemyDistance.RANGE_0_100, autobot._studying.bayes.enums.EnemyVelocity.RANGE_0_1, autobot._studying.bayes.enums.EnemyAngle.RANGE_0_45, autobot._studying.bayes.enums.EnemyHeading.RANGE_0_45, autobot._studying.bayes.enums.MyGunToEnemyAngle.RANGE_0_20, autobot._studying.bayes.enums.FirePower.FP_08, autobot._studying.bayes.enums.Hit.TRUE);
        weka.addInstance(autobot._studying.bayes.enums.EnemyDistance.RANGE_0_100, autobot._studying.bayes.enums.EnemyVelocity.RANGE_0_1, autobot._studying.bayes.enums.EnemyAngle.RANGE_0_45, autobot._studying.bayes.enums.EnemyHeading.RANGE_0_45, autobot._studying.bayes.enums.MyGunToEnemyAngle.RANGE_0_20, autobot._studying.bayes.enums.FirePower.FP_08, autobot._studying.bayes.enums.Hit.TRUE);
        weka.addInstance(autobot._studying.bayes.enums.EnemyDistance.RANGE_0_100, autobot._studying.bayes.enums.EnemyVelocity.RANGE_0_1, autobot._studying.bayes.enums.EnemyAngle.RANGE_0_45, autobot._studying.bayes.enums.EnemyHeading.RANGE_0_45, autobot._studying.bayes.enums.MyGunToEnemyAngle.RANGE_0_20, autobot._studying.bayes.enums.FirePower.FP_08, autobot._studying.bayes.enums.Hit.TRUE);

        weka.addInstance(autobot._studying.bayes.enums.EnemyDistance.RANGE_100_200, autobot._studying.bayes.enums.EnemyVelocity.RANGE_0_1, autobot._studying.bayes.enums.EnemyAngle.RANGE_0_45, autobot._studying.bayes.enums.EnemyHeading.RANGE_0_45, autobot._studying.bayes.enums.MyGunToEnemyAngle.RANGE_0_20, autobot._studying.bayes.enums.FirePower.FP_08, autobot._studying.bayes.enums.Hit.TRUE);

        weka.calcNewDistributions();
        jayes.setNewProbabilities();
    }

    @SuppressWarnings("unused")
    private static void printAll(String tip, WekaWrapper weka, JayesWrapper jayes) {
        System.out.println("\n\n\n========================================= " + tip + " =========================================");
        System.out.println("========================================= " + tip + " =========================================");
        System.out.println("========================================= " + tip + " =========================================");
        weka.printAll();
        jayes.printAll();
    }

    public static void main(String[] args) throws Exception {

        List<InternalBayesNode> internalNodes = initInternalBayesNodes();

        WekaWrapper weka = new WekaWrapper(internalNodes);
        JayesWrapper jayes = new JayesWrapper(internalNodes);
//        printAll("Initial network", weka, jayes);

        addSomeInstances(weka, jayes);
//        printAll("After changes", weka, jayes);

//        Test1 test1 = new Test1(jayes);
//        test1.run();

        Test2 test2 = new Test2(jayes);
        test2.run();

//        weka.displayGraph();

    }
}

// LUCAS:
// TODO: * Salvar e pegar dados entre as rodadas
// TODO: * Como saber se a bala acertou ou não?


// PARA APRESENTAR:
// TODO: * Entender melhor o caso do 0


// MELHORIA DE CÓDIGO
// TODO: * Arquivos de classes


// FUNCIONALIDADES NECESSÁRIAS:
// TODO: * Ao atirar, pega os dados, calcula prob, escolhe, adiciona nas instâncias com o resultado
// TODO:    - Classificador entre valores do enum
// TODO:    - Atualizar o dataset com o resultado
// TODO: * Cassar com a lógica existente de tiro


// FUNCIONALIDADES DESEJÁVEIS:
// * TODO: Bayes para ver pra onde girar a arma baseado em posição, velocidade e direção do inimigo


// CANCELADO (Se tem energia, atira. Quem tem limite é município!)
// TODO: * Valor mínimo de believe para atirar
