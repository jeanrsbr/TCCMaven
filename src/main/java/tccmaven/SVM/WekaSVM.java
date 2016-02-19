/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.SVM;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import libsvm.svm;
import tccmaven.MISC.Log;
import weka.classifiers.Classifier;
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
    
    private String arqARFF;

    public WekaSVM(String arqARFF) throws WekaSVMException {
        this.arqARFF = arqARFF;
    }

    //Realiza a predição da cotação de fechamento do próximo dia
    public void prediction() throws WekaSVMException {


        try {

        } catch (Exception ex) {
            throw new WekaSVMException("Não foi possível executar o algoritmo de predição");
        }

    }

    //Realiza o teste de performance do modelo construído
    public double perfomanceAnalysis() throws WekaSVMException {


        
        //Treino 70:30
        
        
        //TODO: Descobrir um jeito de silenciar a geração dos dados do WEKA
        //TODO: Colocar a variável alvo no final do arquivo

        try {
            //Monta base completa
            Instances dataSet = buildBase();
            
            int trainSize = (int) Math.round(dataSet.numInstances() * 70 / 100);
            int testSize = dataSet.numInstances() - trainSize;
            
            Instances train = new Instances(dataSet, 0, trainSize);
            Instances test = new Instances(dataSet, trainSize, testSize);
            
            train.setClassIndex(train.numAttributes() - 1);
            test.setClassIndex(test.numAttributes() - 1);
            //Constroi modelo
            LibSVM svm = buildModel(train);            
            
            double[] percentualAcerto = new double[test.numInstances()];
            //Percorre o arquivo zerando o parâmetro alvo
            for (int i = 0; i < test.numInstances(); i++) {
                //Obtém o valor real do atributo
                double real = test.instance(i).classValue();
                //Valor predito
                double predict = svm.classifyInstance(test.instance(i));
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
            
            PrintStream ori = new PrintStream(System.out);
            System.setOut(new PrintStream("output_weka.txt"));
            
            LibSVM svm = new LibSVM();
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
            svm.setDebug(false);

            svm.buildClassifier(train);
            
            System.setOut(ori);
            
            return svm;
        } catch (Exception ex) {
            throw new WekaSVMException("Não foi possível executar o algoritmo de predição");
        }
    }

    //Monta a base de dados
    private Instances buildBase() throws WekaSVMException {
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
