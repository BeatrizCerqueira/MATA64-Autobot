package autobot._studying.bayes._to_test;

import autobot.bayes.InternalBayesNode;
import autobot.bayes.JayesWrapper;
import autobot.bayes.WekaWrapper;
import autobot.bayes.enums.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("CommentedOutCode")
public class AgoraVai {

    private static List<InternalBayesNode> initInternalBayesNodes() {
        List<InternalBayesNode> internalNodes = new ArrayList<>();

        InternalBayesNode enemyDistance = new InternalBayesNode("EnemyDistance", EnemyDistance.class, new ArrayList<>());
        InternalBayesNode enemyVelocity = new InternalBayesNode("EnemyVelocity", EnemyVelocity.class, new ArrayList<>());
        InternalBayesNode enemyAngle = new InternalBayesNode("EnemyAngle", EnemyAngle.class, new ArrayList<>());
        InternalBayesNode enemyHeading = new InternalBayesNode("EnemyHeading", EnemyHeading.class, new ArrayList<>());
        InternalBayesNode myGunToEnemyAngle = new InternalBayesNode("MyGunToEnemyAngle", MyGunToEnemyAngle.class, new ArrayList<>());
        InternalBayesNode firePower = new InternalBayesNode("FirePower", FirePower.class, new ArrayList<>());
        InternalBayesNode hit = new InternalBayesNode("Hit", Hit.class, Arrays.asList("EnemyDistance", "EnemyVelocity", "EnemyAngle", "EnemyHeading", "MyGunToEnemyAngle", "FirePower"));

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
//        List<GenericAttribute> instance1 = Arrays.asList(EnemyDistance.RANGE_0_100, EnemyVelocity.RANGE_0_1, EnemyAngle.RANGE_0_45, EnemyHeading.RANGE_0_45, MyGunToEnemyAngle.RANGE_0_20, FirePower.FP_03, Hit.TRUE);
//        List<GenericAttribute> instance2 = Arrays.asList(EnemyDistance.RANGE_0_100, EnemyVelocity.RANGE_0_1, EnemyAngle.RANGE_0_45, EnemyHeading.RANGE_0_45, MyGunToEnemyAngle.RANGE_0_20, FirePower.FP_03, Hit.TRUE);
//        List<GenericAttribute> instance3 = Arrays.asList(EnemyDistance.RANGE_0_100, EnemyVelocity.RANGE_0_1, EnemyAngle.RANGE_0_45, EnemyHeading.RANGE_0_45, MyGunToEnemyAngle.RANGE_0_20, FirePower.FP_03, Hit.TRUE);
//        List<GenericAttribute> instance4 = Arrays.asList(EnemyDistance.RANGE_0_100, EnemyVelocity.RANGE_0_1, EnemyAngle.RANGE_0_45, EnemyHeading.RANGE_0_45, MyGunToEnemyAngle.RANGE_0_20, FirePower.FP_08, Hit.TRUE);
//        List<GenericAttribute> instance5 = Arrays.asList(EnemyDistance.RANGE_0_100, EnemyVelocity.RANGE_0_1, EnemyAngle.RANGE_0_45, EnemyHeading.RANGE_0_45, MyGunToEnemyAngle.RANGE_0_20, FirePower.FP_08, Hit.TRUE);
//        List<GenericAttribute> instance6 = Arrays.asList(EnemyDistance.RANGE_0_100, EnemyVelocity.RANGE_0_1, EnemyAngle.RANGE_0_45, EnemyHeading.RANGE_0_45, MyGunToEnemyAngle.RANGE_0_20, FirePower.FP_08, Hit.TRUE);
//        List<GenericAttribute> instance7 = Arrays.asList(EnemyDistance.RANGE_900_1000, EnemyVelocity.RANGE_0_1, EnemyAngle.RANGE_0_45, EnemyHeading.RANGE_0_45, MyGunToEnemyAngle.RANGE_0_20, FirePower.FP_08, Hit.TRUE);

//        weka.addInstance(instance1);
//        weka.addInstance(instance2);
//        weka.addInstance(instance3);
//        weka.addInstance(instance4);
//        weka.addInstance(instance5);
//        weka.addInstance(instance6);
//        weka.addInstance(instance7);

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

//        Test1 teste = new Test1(jayes);
//        Test2 teste = new Test2(jayes);
//        teste.run();

//        weka.displayGraph();

        weka.saveDatasetFile();
        weka.loadDatasetFile();

    }
}

// LUCAS:
// DONE: * Salvar e pegar dados entre as rodadas
// DONE: * Como saber se a bala acertou ou não?


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


// TODO ===========================================================================================================
// TODO: Remove SuppressWarnings
// TODO: Tentar rodar no Robocode
// TODO: Aumentar importância de acordo com o nº do round
