/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.SVM;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import tccmaven.MISC.Log;
import weka.classifiers.functions.LibSVM;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

/**
 *
 * @author Jean-NoteI5
 */
public class WekaSVM {

    //Valor de fechamento predito pelo algoritmo
    private double valorFechamentoPredito = 0;
    //Diferença entre o valor do dia atual e o valor predito para amanhã
    private double difValorPredito = 0;
    //Percentual de diferença entre o dia atual e o predito
    private double percentualValorPredito = 0;
    //Instância da base de treinamento
    private Instances train = null;
    //Instância do modelo de regressão
    private LibSVM svm = null;

    public WekaSVM(String arqARFF) throws WekaSVMException {
        train = buildBase(arqARFF);
        svm = buildModel(train);
    }

    //Realiza a predição da cotação de fechamento do próximo dia
    public void prediction() throws WekaSVMException {


        try {

            /* ----------------- Realiza predição ------------------- */
            Instance prediction = new Instance(17);
            prediction.setDataset(train);

            //TODO: Setar os parâmetros da predição



            double pred;
            pred = svm.classifyInstance(prediction);
            System.out.println(pred);
        } catch (Exception ex) {
            throw new WekaSVMException("Não foi possível executar o algoritmo de predição");
        }

    }

    //Realiza o teste de performance do modelo construído
    public double perfomanceAnalysis() throws WekaSVMException {

        double[] percentualAcerto = new double[20];

        try {

            //Projeta os últimos 20 resultados
            for (int i = 0; i < 20; i++) {
                //Obtém a instância real contida no arquivo
                Instance prediction = (Instance) train.instance(train.numInstances() - 1 - i).copy();
                //Obtém o valor real do atributo
                double real = prediction.value(train.numAttributes() - 1);
                //Zera o atributado alvo
                prediction.setValue(train.numAttributes() - 1, 0d);
                //Adiciona a predição ao DATASET 
                prediction.setDataset(train);
                //Valor predito
                double predict = svm.classifyInstance(prediction);
                //Ajusta a tabela de percentual de acerto
                percentualAcerto[i] = (predict * 100) / real;
            }
            Log.loga("Desvio padrão: " + desvioPadrao(percentualAcerto));
            return desvioPadrao(percentualAcerto);

        } catch (Exception ex) {
            throw new WekaSVMException("Não foi possível executar o algoritmo de predição");
        }




    }

    private LibSVM buildModel(Instances train) throws WekaSVMException {
        try {
            svm = new LibSVM();
            svm.setSVMType(new SelectedTag(LibSVM.SVMTYPE_EPSILON_SVR, LibSVM.TAGS_SVMTYPE));
            svm.setCacheSize(100);
            svm.setCoef0(0.0);
            svm.setCost(1.0);
            svm.setDebug(false);
            svm.setDegree(3);
            svm.setDoNotReplaceMissingValues(false);
            svm.setEps(0.001);
            svm.setGamma(0.0);
            svm.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_RBF, LibSVM.TAGS_KERNELTYPE));
            svm.setLoss(0.1);
            svm.setNormalize(true);
            svm.setNu(0.5);
            svm.setProbabilityEstimates(false);
            svm.setShrinking(true);
            svm.setWeights("");
            svm.buildClassifier(train);

//            Evaluation evaluation = new Evaluation(train);
//            
//            evaluation.crossValidateModel(svm, train, 10, new Random(1));
//            
//            System.out.println(evaluation.toSummaryString());
            return svm;
        } catch (Exception ex) {
            throw new WekaSVMException("Não foi possível executar o algoritmo de predição");
        }
    }

    //Monta a base de dados
    private Instances buildBase(String arqARFF) throws WekaSVMException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(arqARFF));
            Instances train = new Instances(reader);
            //Define o atributo de classe (Atributo que será predito)
            train.setClassIndex(train.numAttributes() - 1);
            reader.close();
            return train;
        } catch (IOException ex) {
            throw new WekaSVMException("Não foi possível executar o algoritmo de predição");
        }
    }

    public double getValorFechamentoPredito() {
        return valorFechamentoPredito;
    }

    public double getDifValorPredito() {
        return difValorPredito;
    }

    public double getPercentualValorPredito() {
        return percentualValorPredito;
    }
    //Calcula o desvio padrão

    private double desvioPadrao(double[] valores) {

        double media = 0;

        for (int i = 0; i < valores.length; i++) {
            media += valores[i];
        }
        //Calcula a média dos valores
        media = media / valores.length;

        double variancia = 0;

        for (int i = 0; i < valores.length; i++) {
            variancia += Math.pow(valores[i] - media, 2);
        }
        //Calcula a variância dos valores
        variancia = variancia / valores.length;

        //Retorna o desvio padrão
        return Math.sqrt(variancia);
    }
}
