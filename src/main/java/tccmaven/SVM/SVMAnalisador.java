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
import java.util.logging.Level;
import java.util.logging.Logger;
import tccmaven.MISC.LeituraProperties;
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

    public void executaAnalise() throws SVMAnalisadorException {

        try {
            ArrayList<ParametroSVM> analise = populaAnalise();

            int count = 0;

            //Varre as opções de análise
            for (int i = 0; i < analise.size(); i++) {
                count++;
                new Thread(new WekaSVM(nomArqARFF, analise.get(i), count)).start();


                while (true) {
                    if (ManipuladorResultadosSVM.getInstance().getOco() > count - numThreads) {
                        break;
                    } else {
                        //Espera 1 segundo para conferir novamente
                        Thread.sleep(1000);
                    }

                }
            }

            //Aguarda 10 segundos, para que todas as threads sejam finalizadas
            Thread.sleep(10000);

        } catch (WekaSVMException | InterruptedException ex) {
            throw new SVMAnalisadorException("Não foi possível executar a predição");
        }

//        try {
//            //Abre o arquivo CSV de resultados
//            File file = new File("teste/resultado_" + nomArqARFF.split(".arff")[0] + ".csv");
//            FileOutputStream arquivoGravacao = new FileOutputStream(file);
//            OutputStreamWriter strWriter = new OutputStreamWriter(arquivoGravacao);
//            BufferedWriter resultado = new BufferedWriter(strWriter);
//
//            //Cabeçalho
//            resultado.write("ativo;tam_treino;evaluation;valor_real;valor_predito;diffMod;perc_acerto");
//            resultado.newLine();
//
//            //Log.loga("Iniciando exportação com conjunto de " + trainSize + " dias do dia " + dia, "SVM");
//            //Varre as opções de análise
//            for (int i = 0; i < analise.size(); i++) {
//
//                new Thread(new WekaSVM(nomArqARFF, analise.get(i), i));
//
////
////                resultado.write(montaLinha(wekaSVM[0]));
////                resultado.newLine();
//            }
//
//            resultado.flush();
//            resultado.close();
//        } catch (IOException ex) {
//            throw new SVMAnalisadorException("Não foi possível criar o arquivo de resultado");
//        }

    }

//    private String montaLinha(WekaSVM wekaSVM) throws WekaSVMException {
//
//        StringBuilder linha = new StringBuilder();
//        linha.append(wekaSVM.getNomArqARFF());
//        linha.append(";");
//        linha.append(wekaSVM.getTrainSize());
//        linha.append(";");
//        linha.append(wekaSVM.getGridSearchEvaluationAlfa());
//        linha.append(";");
//        linha.append(EditaValores.edita2DecVirgula(wekaSVM.getReal()));
//        linha.append(";");
//        linha.append(EditaValores.edita2DecVirgula(wekaSVM.getPredict()));
//        linha.append(";");
//        linha.append(EditaValores.edita2DecVirgula(wekaSVM.getDiffMod()));
//        linha.append(";");
//        linha.append(EditaValores.edita2DecVirgula(wekaSVM.getPercentualAcerto()));
//
//        //Retorna a linha montada
//        return linha.toString();
//
//    }
    private ArrayList populaAnalise() {

        //Instância o array de análise
        ArrayList<ParametroSVM> parametrosSVMs = new ArrayList<>();

        //Realiza testes com os últimos 20 dias da amostra, com diversos tamanhos
        for (int i = 2; i < 22; i++) {

            parametrosSVMs.add(new ParametroSVM(i, 25, GridSearch.EVALUATION_MAE));
            parametrosSVMs.add(new ParametroSVM(i, 25, GridSearch.EVALUATION_RAE));
            parametrosSVMs.add(new ParametroSVM(i, 25, GridSearch.EVALUATION_RMSE));
            parametrosSVMs.add(new ParametroSVM(i, 25, GridSearch.EVALUATION_RRSE));
            parametrosSVMs.add(new ParametroSVM(i, 25, GridSearch.EVALUATION_COMBINED));

            parametrosSVMs.add(new ParametroSVM(i, 30, GridSearch.EVALUATION_MAE));
            parametrosSVMs.add(new ParametroSVM(i, 30, GridSearch.EVALUATION_RAE));
            parametrosSVMs.add(new ParametroSVM(i, 30, GridSearch.EVALUATION_RMSE));
            parametrosSVMs.add(new ParametroSVM(i, 30, GridSearch.EVALUATION_RRSE));
            parametrosSVMs.add(new ParametroSVM(i, 30, GridSearch.EVALUATION_COMBINED));

            parametrosSVMs.add(new ParametroSVM(i, 35, GridSearch.EVALUATION_MAE));
            parametrosSVMs.add(new ParametroSVM(i, 35, GridSearch.EVALUATION_RAE));
            parametrosSVMs.add(new ParametroSVM(i, 35, GridSearch.EVALUATION_RMSE));
            parametrosSVMs.add(new ParametroSVM(i, 35, GridSearch.EVALUATION_RRSE));
            parametrosSVMs.add(new ParametroSVM(i, 35, GridSearch.EVALUATION_COMBINED));

            parametrosSVMs.add(new ParametroSVM(i, 40, GridSearch.EVALUATION_MAE));
            parametrosSVMs.add(new ParametroSVM(i, 40, GridSearch.EVALUATION_RAE));
            parametrosSVMs.add(new ParametroSVM(i, 40, GridSearch.EVALUATION_RMSE));
            parametrosSVMs.add(new ParametroSVM(i, 40, GridSearch.EVALUATION_RRSE));
            parametrosSVMs.add(new ParametroSVM(i, 40, GridSearch.EVALUATION_COMBINED));

            parametrosSVMs.add(new ParametroSVM(i, 50, GridSearch.EVALUATION_MAE));
            parametrosSVMs.add(new ParametroSVM(i, 50, GridSearch.EVALUATION_RAE));
            parametrosSVMs.add(new ParametroSVM(i, 50, GridSearch.EVALUATION_RMSE));
            parametrosSVMs.add(new ParametroSVM(i, 50, GridSearch.EVALUATION_RRSE));
            parametrosSVMs.add(new ParametroSVM(i, 50, GridSearch.EVALUATION_COMBINED));

            parametrosSVMs.add(new ParametroSVM(i, 70, GridSearch.EVALUATION_MAE));
            parametrosSVMs.add(new ParametroSVM(i, 70, GridSearch.EVALUATION_RAE));
            parametrosSVMs.add(new ParametroSVM(i, 70, GridSearch.EVALUATION_RMSE));
            parametrosSVMs.add(new ParametroSVM(i, 70, GridSearch.EVALUATION_RRSE));
            parametrosSVMs.add(new ParametroSVM(i, 70, GridSearch.EVALUATION_COMBINED));

        }

        return parametrosSVMs;

    }
}
