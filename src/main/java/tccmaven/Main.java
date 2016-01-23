/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven;

import java.io.IOException;


/**
 *
 * @author Jean-NoteI5
 */
public class Main {

    public static void main(String[] args) throws IOException {

        try {

            //Obtém a lista de ativos que devem ser importados
            String[] ativos = LeituraProperties.getInstance().leituraProperties("prop.ativos").split("#");

            //Varre a lista de ativos a serem importados
            for (int i = 0; i < ativos.length; i++) {

                //Identifica os ativos em suas diversas bolsas
                String[] ativosBolsas = ativos[i].split(";");

                //Instância os parâmetros com o primeiro ativo
                Parametros parametros = new Parametros(ativosBolsas[0]);
                
                for (int j = 0; j < ativosBolsas.length; j++) {

                    //Baixa arquivo CSV e Converte arquivo para memória
                    Log.loga("Importando o ativo " + ativosBolsas[j]);
                    Importador importador = new Importador(ativosBolsas[j]);
                    parametros.insereSerieTemporal(importador.montaTimeSeries());

                }
                
                //Calcula indicadores
                Log.loga("Serão calculados os indicadores");
                Indicadores indicadores = new Indicadores(parametros);
                indicadores.calculaIndicadores();
                //Atualiza parâmetros com a inclusão dos indicadores
                parametros = indicadores.getParametros();
                
                
                
                //Filtro
                //TODO: Eliminar dados redundantes e etc
                

                //Criar arquivo ARFF
                Log.loga("Será gerado o arquivo ARFF");
                GeraArquivoARFF geraArquivoARFF = new GeraArquivoARFF(null, ativos[i]);
                geraArquivoARFF.geraArquivo();

            }
        } catch (ParametrosException | BaixaArquivoException | ImportadorException | GeraArquivoARFFException ex) {
            System.out.println(ex.getMessage());
        }
    }
}