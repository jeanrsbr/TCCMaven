/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven;

import tccmaven.MISC.LeituraProperties;
import tccmaven.DATA.ParametrosException;
import tccmaven.IMPORT.BaixaArquivoException;
import tccmaven.IMPORT.ImportadorException;
import tccmaven.MISC.Log;
import tccmaven.OUTPUT.GeraArquivoARFFException;
import tccmaven.OUTPUT.GeraArquivoARFF;
import java.io.IOException;
import tccmaven.SVM.WekaSVM;
import tccmaven.SVM.WekaSVMException;

/**
 *
 * @author Jean-NoteI5
 */
public class Main {

    public static void main(String[] args) throws IOException {

        try {
            //Se não foi informada a data inicial
            if (LeituraProperties.getInstance().leituraProperties("prop.DataIni").equals("")) {
                System.out.println("É obrigatório informar a data inicial para importação");
                return;
            }

            //Se não foi informada a data final
            if (LeituraProperties.getInstance().leituraProperties("prop.DataFim").equals("")) {
                System.out.println("É obrigatório informar a data inicial para importação");
                return;
            }

            //Quantidade de ativos a serem importados
            int qtdativos = Integer.parseInt(LeituraProperties.getInstance().leituraProperties("prop.qtdativos"));
            
            
            
            //Obtém a lista de ativos que devem ser importados
            String[] ativos = LeituraProperties.getInstance().leituraProperties("prop.ativos").split("#");

            //Varre a lista de ativos a serem importados
            for (int i = 0; i < ativos.length; i++) {

                //Criar arquivo ARFF
                Log.loga("Será gerado o arquivo ARFF");
                //Instância a geração de arquivos ARFF
                GeraArquivoARFF geraArquivoARFF = new GeraArquivoARFF(ativos[i].split(";"));              
                //Gera o arquivo ARFF com a quantidade de ativos indicada no properties
                String arquivoARFF = geraArquivoARFF.geraArquivo(qtdativos);

                //Executar algoritmo SVM
                WekaSVM wekaSVM = new WekaSVM(arquivoARFF);
                double resultado = wekaSVM.perfomanceAnalysis();

                System.out.println("Desvio padrão: " + resultado);

            }
        } catch (ParametrosException | BaixaArquivoException | ImportadorException | GeraArquivoARFFException | WekaSVMException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}