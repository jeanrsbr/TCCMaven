/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.SVM;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import tccmaven.MISC.EditaValores;
import tccmaven.MISC.LeituraProperties;
import tccmaven.MISC.Log;
import weka.classifiers.meta.GridSearch;

/**
 *
 * @author Jean-NoteI5
 */
public class SVMAnalisador {

    private int numThreads;
    private String nomArqARFF;

    public SVMAnalisador(String nomArqARFF) {
        this.nomArqARFF = nomArqARFF;
        numThreads = Integer.parseInt(LeituraProperties.getInstance().leituraProperties("thread.svm"));
    }

    public void executaAnalise() throws SVMAnalisadorException, WekaSVMException {

        ArrayList<ParametrosSVM> analise = populaAnalise();

        try {
            //Abre o arquivo CSV de resultados
            File file = new File("teste/resultado_" + nomArqARFF.split(".arff")[0] + ".csv");
            FileOutputStream arquivoGravacao = new FileOutputStream(file);
            OutputStreamWriter strWriter = new OutputStreamWriter(arquivoGravacao);
            BufferedWriter resultado = new BufferedWriter(strWriter);

            //Cabeçalho
            resultado.write("ativo;tam_treino;evaluation;valor_real;valor_predito;diffMod;perc_acerto");
            resultado.newLine();

            //Log.loga("Iniciando exportação com conjunto de " + trainSize + " dias do dia " + dia, "SVM");


            //Instância os objetos para predição
            WekaSVM wekaSVM[] = new WekaSVM[numThreads];


            //Varre as opções de análise
            for (int i = 0; i < analise.size(); i++) {


                wekaSVM[0] = new WekaSVM(nomArqARFF, analise.get(i).getDiaInicial(), analise.get(i).getTamanhoDoConjunto(), analise.get(i).getGridSearchEvaluation());
                wekaSVM[0].perfomanceAnalysis();
                Log.loga("GS EVALUATION: " + wekaSVM[0].getGridSearchEvaluationAlfa() + " COST: " + wekaSVM[0].getCost() + " gamma: " + wekaSVM[0].getGamma(), "SVM");
                
                resultado.write(montaLinha(wekaSVM[0]));
                resultado.newLine();

            }

            resultado.flush();
            resultado.close();
        } catch (IOException ex) {
            throw new SVMAnalisadorException("Não foi possível criar o arquivo de resultado");
        }


    }

    private String montaLinha(WekaSVM wekaSVM) throws WekaSVMException {

        StringBuilder linha = new StringBuilder();
        linha.append(wekaSVM.getNomArqARFF());
        linha.append(";");
        linha.append(wekaSVM.getTrainSize());
        linha.append(";");
        linha.append(wekaSVM.getGridSearchEvaluationAlfa());
        linha.append(";");
        linha.append(EditaValores.edita2DecVirgula(wekaSVM.getReal()));
        linha.append(";");
        linha.append(EditaValores.edita2DecVirgula(wekaSVM.getPredict()));
        linha.append(";");
        linha.append(EditaValores.edita2DecVirgula(wekaSVM.getDiffMod()));
        linha.append(";");
        linha.append(EditaValores.edita2DecVirgula(wekaSVM.getPercentualAcerto()));

        //Retorna a linha montada
        return linha.toString();

    }

    private ArrayList populaAnalise() {


        //Instância o array de análise
        ArrayList<ParametrosSVM> parametrosSVMs = new ArrayList<>();

        //Realiza testes com os últimos 20 dias da amostra, com diversos tamanhos
        for (int i = 2; i < 22; i++) {

            parametrosSVMs.add(new ParametrosSVM(i, 25, GridSearch.EVALUATION_MAE));
            parametrosSVMs.add(new ParametrosSVM(i, 25, GridSearch.EVALUATION_RAE));
            parametrosSVMs.add(new ParametrosSVM(i, 25, GridSearch.EVALUATION_RMSE));
            parametrosSVMs.add(new ParametrosSVM(i, 25, GridSearch.EVALUATION_RRSE));
            parametrosSVMs.add(new ParametrosSVM(i, 25, GridSearch.EVALUATION_COMBINED));

            parametrosSVMs.add(new ParametrosSVM(i, 30, GridSearch.EVALUATION_MAE));
            parametrosSVMs.add(new ParametrosSVM(i, 30, GridSearch.EVALUATION_RAE));
            parametrosSVMs.add(new ParametrosSVM(i, 30, GridSearch.EVALUATION_RMSE));
            parametrosSVMs.add(new ParametrosSVM(i, 30, GridSearch.EVALUATION_RRSE));
            parametrosSVMs.add(new ParametrosSVM(i, 30, GridSearch.EVALUATION_COMBINED));

            parametrosSVMs.add(new ParametrosSVM(i, 35, GridSearch.EVALUATION_MAE));
            parametrosSVMs.add(new ParametrosSVM(i, 35, GridSearch.EVALUATION_RAE));
            parametrosSVMs.add(new ParametrosSVM(i, 35, GridSearch.EVALUATION_RMSE));
            parametrosSVMs.add(new ParametrosSVM(i, 35, GridSearch.EVALUATION_RRSE));
            parametrosSVMs.add(new ParametrosSVM(i, 35, GridSearch.EVALUATION_COMBINED));


            parametrosSVMs.add(new ParametrosSVM(i, 40, GridSearch.EVALUATION_MAE));
            parametrosSVMs.add(new ParametrosSVM(i, 40, GridSearch.EVALUATION_RAE));
            parametrosSVMs.add(new ParametrosSVM(i, 40, GridSearch.EVALUATION_RMSE));
            parametrosSVMs.add(new ParametrosSVM(i, 40, GridSearch.EVALUATION_RRSE));
            parametrosSVMs.add(new ParametrosSVM(i, 40, GridSearch.EVALUATION_COMBINED));


            parametrosSVMs.add(new ParametrosSVM(i, 50, GridSearch.EVALUATION_MAE));
            parametrosSVMs.add(new ParametrosSVM(i, 50, GridSearch.EVALUATION_RAE));
            parametrosSVMs.add(new ParametrosSVM(i, 50, GridSearch.EVALUATION_RMSE));
            parametrosSVMs.add(new ParametrosSVM(i, 50, GridSearch.EVALUATION_RRSE));
            parametrosSVMs.add(new ParametrosSVM(i, 50, GridSearch.EVALUATION_COMBINED));

            parametrosSVMs.add(new ParametrosSVM(i, 70, GridSearch.EVALUATION_MAE));
            parametrosSVMs.add(new ParametrosSVM(i, 70, GridSearch.EVALUATION_RAE));
            parametrosSVMs.add(new ParametrosSVM(i, 70, GridSearch.EVALUATION_RMSE));
            parametrosSVMs.add(new ParametrosSVM(i, 70, GridSearch.EVALUATION_RRSE));
            parametrosSVMs.add(new ParametrosSVM(i, 70, GridSearch.EVALUATION_COMBINED));

        }

        return parametrosSVMs;

    }
}
