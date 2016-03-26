/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven;

import tccmaven.MISC.LeituraProperties;
import tccmaven.ARFF.PARAMETROS.InsereParametrosException;
import tccmaven.ARFF.IMPORT.BaixaArquivoException;
import tccmaven.ARFF.IMPORT.ImportadorException;
import tccmaven.MISC.Log;
import tccmaven.ARFF.GeraArquivoARFFException;
import tccmaven.ARFF.GeraArquivoARFF;
import java.io.IOException;
import tccmaven.ARFF.PARAMETROS.IndicadoresException;
import tccmaven.ARFF.PARAMETROS.NomeParametrosException;
import tccmaven.SVM.SVMAnalisador;
import tccmaven.SVM.SVMAnalisadorException;

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

            //Inicializa o buffer
            Log.iniBuf();

            //Obtém a lista de ativos que devem ser importados
            String[] ativos = LeituraProperties.getInstance().leituraProperties("prop.ativos").split("#");

            //Varre a lista de ativos a serem importados
            for (int i = 0; i < ativos.length; i++) {

                //Obtém os ativos de cada país
                String[] atiPaises = ativos[i].split(";");

                //Criar arquivo ARFF
                Log.loga("Será gerado o arquivo ARFF");
                //Instância a geração de arquivos ARFF
                GeraArquivoARFF geraArquivoARFF = new GeraArquivoARFF(atiPaises[0], atiPaises[1]);
                //Gera o arquivo ARFF com a quantidade de ativos indicada no properties
                String arquivoARFF = geraArquivoARFF.geraArquivo();

                //Executar algoritmo SVM
                SVMAnalisador sVMAnalisador = new SVMAnalisador(arquivoARFF);
                sVMAnalisador.executaAnalise();

            }
        } catch (InsereParametrosException | BaixaArquivoException | ImportadorException | GeraArquivoARFFException | IndicadoresException | NomeParametrosException | SVMAnalisadorException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}