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

    private Instances dataSet;
    private ParametroSVM parametrosSVM;
    private int iD; //Id de identificação dos parâmetros e resultados
    //Parâmetros ótimos da SVM
    //private double cost;
    //private double gamma;
    //Parâmetros da predição

    public WekaSVM(String arqARFF, ParametroSVM parametrosSVM, int iD) throws WekaSVMException {
        dataSet = buildBase(arqARFF);
        this.parametrosSVM = parametrosSVM;
        this.iD = iD;
    }

    //Realiza o teste de performance do modelo construído
    public void perfomanceAnalysis() throws WekaSVMException, ParametroSVMException {

        //Se o SET de treino for maior que o SET disponível
        if ((parametrosSVM.getTamanhoDoConjunto() + parametrosSVM.getDiaInicial()) > dataSet.numInstances()) {
            throw new WekaSVMException("Set de treino muito grande");
        }

        Instances train = new Instances(dataSet, dataSet.numInstances() - parametrosSVM.getDiaInicial() - parametrosSVM.
                getTamanhoDoConjunto(), parametrosSVM.getTamanhoDoConjunto());
        Instances test = new Instances(dataSet, dataSet.numInstances() - parametrosSVM.getDiaInicial(), 1);

        train.setClassIndex(train.numAttributes() - 1);
        test.setClassIndex(test.numAttributes() - 1);

        LibSVM svm = buildSVM();
        svm
                = GridSearch(svm, train, new SelectedTag(parametrosSVM.getGridSearchEvaluation(), GridSearch.TAGS_EVALUATION));
        constroiClassificador(svm, train);

        Log.loga("EVALUATION:" + parametrosSVM.getGridSearchEvaluationAlfa() + " COST:" + svm.getCost() + " gamma:" + svm.getGamma(), "SVM");

        //cost = svm.getCost();
        //gamma = svm.getGamma();
        ResultadoSVM resultadoSVM = new ResultadoSVM();

        double real = test.instance(0).classValue();
        double predict = 0;
        try {
            predict = svm.classifyInstance(test.instance(0));
        } catch (Exception ex) {
            Logger.getLogger(WekaSVM.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Valor real
        resultadoSVM.setReal(real);
        //Valor predito
        resultadoSVM.setPredict(predict);
        //Percentual de acerto
        resultadoSVM.setPercentualAcerto(real, predict);
        //Diferença em módulo
        resultadoSVM.setDiffMod(real, predict);

        //Insere os parâmetros no manipulador de resultado
        ManipuladorResultadosSVM.getInstance().putResultado(iD, resultadoSVM);

    }

    private LibSVM buildSVM() throws WekaSVMException {
        try {

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

            return svm;
        } catch (Exception ex) {
            throw new WekaSVMException("Não foi possível executar o algoritmo de predição");
        }
    }

    private void constroiClassificador(LibSVM svm, Instances train) throws WekaSVMException {

        try {
            svm.buildClassifier(train);
        } catch (Exception ex) {
            Log.loga("Excecão");
        }

    }

    private LibSVM GridSearch(LibSVM svm, Instances train, SelectedTag selectedTag) throws WekaSVMException {

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

    public Instances getDataSet() {
        return dataSet;
    }

    

    @Override
    public void run() {
        try {
            perfomanceAnalysis();

        } catch (WekaSVMException ex) {
            Logger.getLogger(WekaSVM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParametroSVMException ex) {
            Logger.getLogger(WekaSVM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
