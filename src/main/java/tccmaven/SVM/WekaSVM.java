/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.SVM;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import tccmaven.MISC.Log;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.CVParameterSelection;
import weka.classifiers.meta.GridSearch;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Utils;

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
    public void perfomanceAnalysis() throws WekaSVMException {

        try {
            //Monta base completa
            Instances dataSet = buildBase();

            int trainSize = (int) Math.round(dataSet.numInstances() * 70 / 100);
            int testSize = dataSet.numInstances() - trainSize - 1;

            Instances train = new Instances(dataSet, 0, trainSize);
            Instances test = new Instances(dataSet, trainSize, testSize);

            train.setClassIndex(train.numAttributes() - 1);
            test.setClassIndex(test.numAttributes() - 1);




            //Constroi modelo
            LibSVM svm = buildModel(train);





//            CVParameterSelection ps = new CVParameterSelection();
//            ps.setClassifier(svm);
//            ps.setNumFolds(10);  // using 5-fold CV
//            ps.addCVParameter("G 0 10 100");
//            // build and output best options
//            ps.buildClassifier(train);
//            System.out.println(Utils.joinOptions(ps.getBestClassifierOptions()));


            double[] percentualAcerto = new double[test.numInstances()];
            //Percorre o arquivo zerando o parâmetro alvo
            for (int i = 0; i < test.numInstances(); i++) {
                //Obtém o valor real do atributo
                double real = test.instance(i).classValue();
                //Valor predito
                double predict = svm.classifyInstance(test.instance(i));

                Log.loga("Valor real: " + real + " Valor predito: " + predict + " Diferença: " + (real - predict));

                //Ajusta a tabela de percentual de acerto
                percentualAcerto[i] = (predict * 100) / real;
            }

            System.out.println("Desvio padrão: " + desvioPadrao(percentualAcerto));

        } catch (Exception ex) {
            throw new WekaSVMException("Não foi possível executar o algoritmo de predição");
        }




    }

    private LibSVM buildModel(Instances train) throws WekaSVMException {
        try {


            LibSVM svmIni = new LibSVM();
            svmIni.setSVMType(new SelectedTag(LibSVM.SVMTYPE_EPSILON_SVR, LibSVM.TAGS_SVMTYPE));
            svmIni.setCacheSize(100);
            svmIni.setCoef0(0.0);
            svmIni.setCost(1.0);
            svmIni.setDebug(false);
            svmIni.setDegree(3);
            svmIni.setDoNotReplaceMissingValues(false);
            svmIni.setEps(0.001);
            svmIni.setGamma(0.0);
            svmIni.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_RBF, LibSVM.TAGS_KERNELTYPE));
            svmIni.setLoss(0.1);
            svmIni.setNormalize(true);
            svmIni.setNu(0.5);
            svmIni.setProbabilityEstimates(false);
            svmIni.setShrinking(true);
            svmIni.setWeights("");
            svmIni.setDebug(false);

            PrintStream def = new PrintStream(System.out);
            System.setOut(new PrintStream("output_weka.txt"));

            svmIni.buildClassifier(train);

            //
            LibSVM svmFim = GridSearch(svmIni, train);

            
            System.setOut(def);

            return svmFim;
        } catch (Exception ex) {
            throw new WekaSVMException("Não foi possível executar o algoritmo de predição");
        }
    }

    private LibSVM GridSearch(LibSVM svm, Instances train) throws Exception {
        
        GridSearch gridSearch = new GridSearch();
        gridSearch.setClassifier(svm);
        gridSearch.setEvaluation(new SelectedTag(GridSearch.EVALUATION_ACC, GridSearch.TAGS_EVALUATION));

        //evalaute C 1,2,16
        gridSearch.setXProperty("classifier.c");
        gridSearch.setXMin(1);
        gridSearch.setXMax(16);
        gridSearch.setXStep(1);
        gridSearch.setXExpression("I");

        // evaluate gamma s 10^-5, 10^-4,..,10^2.
        gridSearch.setYProperty("classifier.kernel.gamma");
        gridSearch.setYMin(-5);
        gridSearch.setYMax(2);
        gridSearch.setYStep(1);
        gridSearch.setYBase(10);
        gridSearch.setYExpression("pow(BASE,I)");
        
        gridSearch.buildClassifier(train);
        
        return (LibSVM) gridSearch.getBestClassifier();
        
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
