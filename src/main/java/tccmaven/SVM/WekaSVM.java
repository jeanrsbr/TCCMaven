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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import tccmaven.MISC.EditaValores;
import tccmaven.MISC.Log;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.GridSearch;
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
    private String nomArqARFF;
    private Instances dataSet;

    public WekaSVM(String arqARFF) throws WekaSVMException {
        dataSet = buildBase(arqARFF);
        nomArqARFF = getName(arqARFF);
    }

    //Realiza a predição da cotação de fechamento do próximo dia
    public void prediction() throws WekaSVMException {

        try {
        } catch (Exception ex) {
            throw new WekaSVMException("Não foi possível executar o algoritmo de predição");
        }

    }

    //Realiza os testes de performance
    public void perfomanceAnalysis() throws WekaSVMException {

        try {
            //Abre o arquivo CSV de resultados
            File file = new File("teste/resultado_" + nomArqARFF.split(".arff")[0] + ".csv");
            FileOutputStream arquivoGravacao = new FileOutputStream(file);
            OutputStreamWriter strWriter = new OutputStreamWriter(arquivoGravacao);
            BufferedWriter resultado = new BufferedWriter(strWriter);

            //Cabeçalho
            resultado.write("ativo;grid_search;tam_treino;evaluation;valor_real;valor_predito;diff;diffMod;perc_acerto");
            resultado.newLine();

            //Realiza testes com os últimos 20 dias da amostra, com diversos tamanhos
            for (int i = 2; i < 12; i++) {

                perfomanceAnalysis(i, 15, resultado);
                perfomanceAnalysis(i, 20, resultado);
                perfomanceAnalysis(i, 30, resultado);
                perfomanceAnalysis(i, 40, resultado);
                perfomanceAnalysis(i, 50, resultado);
                //perfomanceAnalysis(i, 70, resultado);
                //perfomanceAnalysis(i, 120, resultado);

            }

            resultado.flush();
            resultado.close();
        } catch (IOException ex) {
            throw new WekaSVMException("Não foi possível criar o arquivo de resultado");
        }
    }

    //Realiza o teste de performance do modelo construído
    private void perfomanceAnalysis(int dia, int trainSize, BufferedWriter resultado) throws WekaSVMException {

        //Se o SET de treino for maior que o SET disponível
        if ((trainSize + dia) > dataSet.numInstances()) {
            throw new WekaSVMException("Set de treino muito grande");
        }

        Instances train = new Instances(dataSet, dataSet.numInstances() - dia - trainSize, trainSize);
        Instances test = new Instances(dataSet, dataSet.numInstances() - dia, 1);

        train.setClassIndex(train.numAttributes() - 1);
        test.setClassIndex(test.numAttributes() - 1);

        Log.loga("Iniciando exportação com conjunto de " + trainSize + " dias do dia " + dia, "SVM");

        LibSVM svm = null;

//        //Constroi modelo sem Grid Search
//        svm = buildSVM();
//        constroiClassificador(svm, train);
//        Log.loga("COST: " + svm.getCost() + " gamma: " + svm.getGamma(), "SVM");
//        try {
//            resultado.write(testaClasse(test, svm, "Não", trainSize, "Nenhum"));
//            resultado.newLine();
//        } catch (Exception ex) {
//            throw new WekaSVMException("Não foi possível gravar o arquivo de saída");
//        }

//        //Constroi modelo com Grid Search antes com MAE
//        svm = buildSVM();
//        svm = GridSearch(svm, train, new SelectedTag(GridSearch.EVALUATION_MAE, GridSearch.TAGS_EVALUATION));
//        constroiClassificador(svm, train);
//        Log.loga("MAE - COST: " + svm.getCost() + " gamma: " + svm.getGamma(), "SVM");
//        try {
//            resultado.write(testaClasse(test, svm, "Sim", trainSize, "MAE"));
//            resultado.newLine();
//        } catch (Exception ex) {
//            throw new WekaSVMException("Não foi possível gravar o arquivo de saída");
//        }

//        //Constroi modelo com Grid Search antes com RMSE
//        svm = buildSVM();
//        svm = GridSearch(svm, train, new SelectedTag(GridSearch.EVALUATION_RMSE, GridSearch.TAGS_EVALUATION));
//        constroiClassificador(svm, train);
//        Log.loga("RMSE - COST: " + svm.getCost() + " gamma: " + svm.getGamma(), "SVM");
//        try {
//            resultado.write(testaClasse(test, svm, "Sim", trainSize, "RMSE"));
//            resultado.newLine();
//        } catch (Exception ex) {
//            throw new WekaSVMException("Não foi possível gravar o arquivo de saída");
//        }

        //Constroi modelo com Grid Search antes com RRSE (Sempre o mesmo valor do RMSE)
        svm = buildSVM();
        svm = GridSearch(svm, train, new SelectedTag(GridSearch.EVALUATION_RRSE, GridSearch.TAGS_EVALUATION));
        constroiClassificador(svm, train);
        Log.loga("RRSE - COST: " + svm.getCost() + " gamma: " + svm.getGamma(), "SVM");
        try {

            resultado.write(testaClasse(test, svm, "Sim", trainSize, "RRSE"));
            resultado.newLine();
        } catch (Exception ex) {
            throw new WekaSVMException("Não foi possível gravar o arquivo de saída");
        }
//        //Constroi modelo com Grid Search antes com KAPPA (Sempre o mesmo valor)
//        svm = buildSVM();
//        svm = GridSearch(svm, train, new SelectedTag(GridSearch.EVALUATION_KAPPA, GridSearch.TAGS_EVALUATION));
//        constroiClassificador(svm, train);
//        Log.loga("KAPPA - COST: " + svm.getCost() + " gamma: " + svm.getGamma(), "SVM");
//        try {
//            resultado.write(testaClasse(test, svm, "Sim", trainSize, "KAPPA"));
//            resultado.newLine();
//        } catch (Exception ex) {
//            throw new WekaSVMException("Não foi possível gravar o arquivo de saída");
//        }
        //Constroi modelo com Grid Search antes com COMBINED
        svm = buildSVM();
        svm = GridSearch(svm, train, new SelectedTag(GridSearch.EVALUATION_COMBINED, GridSearch.TAGS_EVALUATION));
        constroiClassificador(svm, train);
        Log.loga("COMBINED - COST: " + svm.getCost() + " gamma: " + svm.getGamma(), "SVM");
        try {
            resultado.write(testaClasse(test, svm, "Sim", trainSize, "COMBINED"));
            resultado.newLine();
        } catch (Exception ex) {
            throw new WekaSVMException("Não foi possível gravar o arquivo de saída");
        }

        //Constroi modelo com Grid Search antes com RAE
        svm = buildSVM();
        svm = GridSearch(svm, train, new SelectedTag(GridSearch.EVALUATION_RAE, GridSearch.TAGS_EVALUATION));
        constroiClassificador(svm, train);
        Log.loga("RAE - COST: " + svm.getCost() + " gamma: " + svm.getGamma(), "SVM");
        try {
            resultado.write(testaClasse(test, svm, "Sim", trainSize, "RAE"));
            resultado.newLine();
        } catch (Exception ex) {
            throw new WekaSVMException("Não foi possível gravar o arquivo de saída");
        }

    }

    //Recebe a instância de teste e o modelo
    private String testaClasse(Instances test, LibSVM model, String gridSearch, int trainSize, String evaluation) throws
            Exception {
        //Obtém o valor real do atributo
        double real = test.instance(0).classValue();
        //Valor predito
        double predict = model.classifyInstance(test.instance(0));
        //Diferença entre o real e o predito
        double diff = real - predict;
        //Ajusta a tabela de percentual de acerto
        double percentualAcerto = (predict * 100) / real;

        double diffMod = diff;
        if (diffMod < 0) {
            diffMod = diffMod * -1;
        }

        StringBuilder linha = new StringBuilder();
        linha.append(nomArqARFF);
        linha.append(";");
        linha.append(gridSearch);
        linha.append(";");
        linha.append(trainSize);
        linha.append(";");
        linha.append(evaluation);
        linha.append(";");
        linha.append(EditaValores.edita2DecVirgula(real));
        linha.append(";");
        linha.append(EditaValores.edita2DecVirgula(predict));
        linha.append(";");
        linha.append(EditaValores.edita2DecVirgula(diff));
        linha.append(";");
        linha.append(EditaValores.edita2DecVirgula(diffMod));
        linha.append(";");
        linha.append(EditaValores.edita2DecVirgula(percentualAcerto));

        //Retorna a linha montada
        return linha.toString();

    }

    private LibSVM buildSVM() throws WekaSVMException {
        try {

            PrintStream def = new PrintStream(System.out);
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
