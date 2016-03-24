/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//            CVParameterSelection ps = new CVParameterSelection();
//            ps.setClassifier(svm);
//            ps.setNumFolds(10);  // using 5-fold CV
//            ps.addCVParameter("G 0 10 100");
//            // build and output best options
//            ps.buildClassifier(train);
//            System.out.println(Utils.joinOptions(ps.getBestClassifierOptions()));
package tccmaven.SVM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import tccmaven.MISC.Log;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.GridSearch;
import weka.core.Instances;
import weka.core.SelectedTag;

/**
 *
 * @author Jean-NoteI5
 */
public class WekaSVM implements Runnable {

    private String nomArqARFF;
    private Instances dataSet;
    //Parâmetros de entrada
    private int dia;
    private int trainSize;
    private int gridSearchEvaluation;
    //Parâmetros ótimos da SVM
    private double cost;
    private double gamma;
    //Parâmetros da predição
    private double real;
    private double predict;
    private double percentualAcerto;
    private double diffMod;

    public WekaSVM(String arqARFF, int dia, int trainSize, int gridSearchEvaluation) throws WekaSVMException {
        dataSet = buildBase(arqARFF);
        nomArqARFF = getName(arqARFF);
        this.dia = dia;
        this.trainSize = trainSize;
        this.gridSearchEvaluation = gridSearchEvaluation;

    }

    //Realiza o teste de performance do modelo construído
    public void perfomanceAnalysis() throws WekaSVMException {

        //Se o SET de treino for maior que o SET disponível
        if ((trainSize + dia) > dataSet.numInstances()) {
            throw new WekaSVMException("Set de treino muito grande");
        }

        Instances train = new Instances(dataSet, dataSet.numInstances() - dia - trainSize, trainSize);
        Instances test = new Instances(dataSet, dataSet.numInstances() - dia, 1);

        train.setClassIndex(train.numAttributes() - 1);
        test.setClassIndex(test.numAttributes() - 1);

        LibSVM svm = buildSVM();
        svm = GridSearch(svm, train, new SelectedTag(gridSearchEvaluation, GridSearch.TAGS_EVALUATION));
        constroiClassificador(svm, train);


        cost = svm.getCost();
        gamma = svm.getGamma();

        //Obtém o valor real do atributo
        real = test.instance(0).classValue();
        try {
            //Valor predito
            predict = svm.classifyInstance(test.instance(0));
        } catch (Exception ex) {
            Logger.getLogger(WekaSVM.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Ajusta a tabela de percentual de acerto
        percentualAcerto = (predict * 100) / real;

        //Diferença em módulo
        diffMod = real - predict;
        if (diffMod < 0) {
            diffMod = diffMod * -1;
        }
    }

    private LibSVM buildSVM() throws WekaSVMException {
        try {

            PrintStream def = new PrintStream(System.out);
            System.setOut(new PrintStream("output_weka.txt"));

            LibSVM svm = new LibSVM();
            svm.setSVMType(new SelectedTag(LibSVM.SVMTYPE_NU_SVR, LibSVM.TAGS_SVMTYPE));
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

            System.setOut(def);

            return svm;
        } catch (Exception ex) {
            throw new WekaSVMException("Não foi possível executar o algoritmo de predição");
        }
    }

    private void constroiClassificador(LibSVM svm, Instances train) throws WekaSVMException {

        try {
            PrintStream def = new PrintStream(System.out);
            System.setOut(new PrintStream("output_weka.txt"));
            svm.buildClassifier(train);
            System.setOut(def);
        } catch (Exception ex) {
            Log.loga("Excecão");
        }

    }

    private LibSVM GridSearch(LibSVM svm, Instances train, SelectedTag selectedTag) throws WekaSVMException {

        PrintStream def = new PrintStream(System.out);
        try {
            System.setOut(new PrintStream("output_weka.txt"));
        } catch (Exception ex) {
            throw new WekaSVMException("Não foi possível direcionar a saída para outro caminho");
        }

        GridSearch gridSearch = new GridSearch();
        gridSearch.setClassifier(svm);
        gridSearch.setEvaluation(selectedTag);

        //evalaute C 12^-5, 2^-4,..,2^2.
        gridSearch.setXProperty("classifier.cost");
        gridSearch.setXMin(-10);
        gridSearch.setXMax(20);
        gridSearch.setXStep(0.5);
        gridSearch.setXBase(2);
        gridSearch.setXExpression("pow(BASE,I)");

        // evaluate gamma s 2^-5, 2^-4,..,2^2.
        gridSearch.setYProperty("classifier.gamma");
        gridSearch.setYMin(-20);
        gridSearch.setYMax(5);
        gridSearch.setYStep(0.5);
        gridSearch.setYBase(2);
        gridSearch.setYExpression("pow(BASE,I)");

        try {
            gridSearch.buildClassifier(train);
        } catch (Exception ex) {
            Log.loga("Excecão");
        }

        System.setOut(def);
        return (LibSVM) gridSearch.getBestClassifier();

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

    //Retorna o nome do arquivo
    private String getName(String arqARFF) {
        File file = new File(arqARFF);
        return file.getName();
    }

    public double getCost() {
        return cost;
    }

    public double getGamma() {
        return gamma;
    }

    public double getReal() {
        return real;
    }

    public double getPredict() {
        return predict;
    }

    public double getPercentualAcerto() {
        return percentualAcerto;
    }

    public double getDiffMod() {
        return diffMod;
    }

    public String getNomArqARFF() {
        return nomArqARFF;
    }

    public Instances getDataSet() {
        return dataSet;
    }

    public int getDia() {
        return dia;
    }

    public int getTrainSize() {
        return trainSize;
    }

    public int getGridSearchEvaluation() {
        return gridSearchEvaluation;
    }

    public String getGridSearchEvaluationAlfa() throws WekaSVMException {

        switch (gridSearchEvaluation) {
            case GridSearch.EVALUATION_COMBINED:
                return "COMBINED";
            case GridSearch.EVALUATION_MAE:
                return "MAE";
            case GridSearch.EVALUATION_RAE:
                return "RAE";
            case GridSearch.EVALUATION_RMSE:
                return "RMSE";
            case GridSearch.EVALUATION_RRSE:
                return "RRSE";
            default:
                throw new WekaSVMException("GridSearch com opção não reconhecida");
        }

    }
    
    
    
    
    @Override
    public void run() {
        try {
            perfomanceAnalysis();
        } catch (WekaSVMException ex) {
            Logger.getLogger(WekaSVM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
