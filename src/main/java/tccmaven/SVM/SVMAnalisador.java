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

    public void executaAnalise() throws SVMAnalisadorException {

        try {
            ArrayList<ParametroSVM> analise = populaAnalise();

            Log.loga(String.valueOf(Thread.activeCount()));

            int count = 0;

            //Varre as opções de análise
            for (int i = 0; i < analise.size(); i++) {

                new Thread(new WekaSVM(nomArqARFF, analise.get(i), i)).start();
                count++;

                while (true) {
                    if (ManipuladorResultadosSVM.getInstance().getOco() > count - numThreads) {
                        break;
                    } else {
                        //Espera 1 segundo para conferir novamente
                        Thread.sleep(1000);
                    }
                }
            }

            //Se processou todas as threads
            while (true) {
                if (Thread.activeCount() == 1) {
                    break;
                } else {
                    //Espera 1 segundo para conferir novamente
                    Thread.sleep(1000);
                }
            }

        } catch (WekaSVMException | InterruptedException ex) {
            throw new SVMAnalisadorException("Não foi possível executar a predição");
        }

    }

    private void criaCSV(ArrayList<ParametroSVM> analise) throws SVMAnalisadorException, WekaSVMException,
            ParametroSVMException {

        try {
            //Abre o arquivo CSV de resultados
            File file = new File("teste/resultado_" + nomArqARFF.split(".arff")[0] + ".csv");
            FileOutputStream arquivoGravacao = new FileOutputStream(file);
            OutputStreamWriter strWriter = new OutputStreamWriter(arquivoGravacao);
            BufferedWriter resultado = new BufferedWriter(strWriter);

            //Cabeçalho
            resultado.write("ativo;tam_treino;evaluation;valor_real;valor_predito;diffMod;perc_acerto");
            resultado.newLine();

            Log.loga("Iniciando exportação do arquivo CSV", "SVM");
            //Varre as opções de análise
            for (int i = 0; i < analise.size(); i++) {
                resultado.write(montaLinha(analise.get(i), ManipuladorResultadosSVM.getInstance().getResultado(i)));
                resultado.newLine();
            }
            resultado.flush();
            resultado.close();
        } catch (IOException ex) {
            throw new SVMAnalisadorException("Não foi possível criar o arquivo de resultado");
        }

    }

    private String montaLinha(ParametroSVM parametroSVM, ResultadoSVM resultadoSVM) throws WekaSVMException,
            ParametroSVMException {

        StringBuilder linha = new StringBuilder();
        linha.append(getName());
        linha.append(";");
        linha.append(parametroSVM.getTamanhoDoConjunto());
        linha.append(";");
        linha.append(parametroSVM.getGridSearchEvaluationAlfa());
        linha.append(";");
        linha.append(EditaValores.edita2DecVirgula(resultadoSVM.getReal()));
        linha.append(";");
        linha.append(EditaValores.edita2DecVirgula(resultadoSVM.getPredict()));
        linha.append(";");
        linha.append(EditaValores.edita2DecVirgula(resultadoSVM.getDiffMod()));
        linha.append(";");
        linha.append(EditaValores.edita2DecVirgula(resultadoSVM.getPercentualAcerto()));

        //Retorna a linha montada
        return linha.toString();

    }

    //Retorna o nome do arquivo
    private String getName() {
        File file = new File(nomArqARFF);
        return file.getName();
    }

    private ArrayList populaAnalise() {

        //Instância o array de análise
        ArrayList<ParametroSVM> parametrosSVMs = new ArrayList<>();

        //Realiza testes com os últimos 20 dias da amostra, com diversos tamanhos
        for (int i = 2; i < 22; i++) {

//            parametrosSVMs.add(new ParametroSVM(i, 25, GridSearch.EVALUATION_MAE));
//            parametrosSVMs.add(new ParametroSVM(i, 25, GridSearch.EVALUATION_RAE));
//            parametrosSVMs.add(new ParametroSVM(i, 25, GridSearch.EVALUATION_RMSE));
//            parametrosSVMs.add(new ParametroSVM(i, 25, GridSearch.EVALUATION_RRSE));
//            parametrosSVMs.add(new ParametroSVM(i, 25, GridSearch.EVALUATION_COMBINED));
//
//            parametrosSVMs.add(new ParametroSVM(i, 30, GridSearch.EVALUATION_MAE));
//            parametrosSVMs.add(new ParametroSVM(i, 30, GridSearch.EVALUATION_RAE));
//            parametrosSVMs.add(new ParametroSVM(i, 30, GridSearch.EVALUATION_RMSE));
//            parametrosSVMs.add(new ParametroSVM(i, 30, GridSearch.EVALUATION_RRSE));
//            parametrosSVMs.add(new ParametroSVM(i, 30, GridSearch.EVALUATION_COMBINED));
//
//            parametrosSVMs.add(new ParametroSVM(i, 35, GridSearch.EVALUATION_MAE));
//            parametrosSVMs.add(new ParametroSVM(i, 35, GridSearch.EVALUATION_RAE));
//            parametrosSVMs.add(new ParametroSVM(i, 35, GridSearch.EVALUATION_RMSE));
//            parametrosSVMs.add(new ParametroSVM(i, 35, GridSearch.EVALUATION_RRSE));
//            parametrosSVMs.add(new ParametroSVM(i, 35, GridSearch.EVALUATION_COMBINED));
//
//            parametrosSVMs.add(new ParametroSVM(i, 40, GridSearch.EVALUATION_MAE));
//            parametrosSVMs.add(new ParametroSVM(i, 40, GridSearch.EVALUATION_RAE));
//            parametrosSVMs.add(new ParametroSVM(i, 40, GridSearch.EVALUATION_RMSE));
//            parametrosSVMs.add(new ParametroSVM(i, 40, GridSearch.EVALUATION_RRSE));
//            parametrosSVMs.add(new ParametroSVM(i, 40, GridSearch.EVALUATION_COMBINED));
//
//            parametrosSVMs.add(new ParametroSVM(i, 50, GridSearch.EVALUATION_MAE));
//            parametrosSVMs.add(new ParametroSVM(i, 50, GridSearch.EVALUATION_RAE));
//            parametrosSVMs.add(new ParametroSVM(i, 50, GridSearch.EVALUATION_RMSE));
//            parametrosSVMs.add(new ParametroSVM(i, 50, GridSearch.EVALUATION_RRSE));
//            parametrosSVMs.add(new ParametroSVM(i, 50, GridSearch.EVALUATION_COMBINED));
            parametrosSVMs.add(new ParametroSVM(i, 70, GridSearch.EVALUATION_MAE));
//            parametrosSVMs.add(new ParametroSVM(i, 70, GridSearch.EVALUATION_RAE));
//            parametrosSVMs.add(new ParametroSVM(i, 70, GridSearch.EVALUATION_RMSE));
//            parametrosSVMs.add(new ParametroSVM(i, 70, GridSearch.EVALUATION_RRSE));
//            parametrosSVMs.add(new ParametroSVM(i, 70, GridSearch.EVALUATION_COMBINED));

        }

        return parametrosSVMs;

    }
}
