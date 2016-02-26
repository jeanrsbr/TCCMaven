/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.OUTPUT;

import eu.verdelhan.ta4j.TimeSeries;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;
import tccmaven.DATA.Indicadores;
import tccmaven.MISC.LeituraProperties;
import tccmaven.MISC.Log;
import tccmaven.DATA.Parametro;
import tccmaven.DATA.Parametros;
import tccmaven.DATA.ParametrosException;
import tccmaven.IMPORT.BaixaArquivoException;
import tccmaven.IMPORT.Importador;
import tccmaven.IMPORT.ImportadorException;

/**
 *
 * @author Jean-NoteI5
 */
public class GeraArquivoARFF {

    private final String extARFF = ".arff";
    private String ativoBrasil;
    private String ativoEst;

    public GeraArquivoARFF(String ativoBrasil, String ativoUsa) {
        this.ativoBrasil = ativoBrasil;
        this.ativoEst = ativoUsa;
    }

    //Gera o arquivo ARFF
    public String geraArquivo() throws GeraArquivoARFFException, ImportadorException, ParametrosException, BaixaArquivoException {


        //Obtém a lista de ativos que devem ser importados
        String[] nomeParametros = LeituraProperties.getInstance().leituraProperties("ind.indicadores").split(";");

        //Instância os parâmetros com o primeiro ativo
        Parametros parametros = new Parametros(ativoBrasil, nomeParametros);
        //Instância os indicadores referenciando os parâmetros
        Indicadores indicadores = new Indicadores(parametros);
        
        //Baixa arquivo CSV e Converte arquivo para memória
        Log.loga("Importando o ativo " + ativoBrasil);
        Importador importador = new Importador(ativoBrasil);
        TimeSeries timeseries = importador.montaTimeSeries();
        parametros.insereSerieTemporalBrasil(timeseries);

        //Calcula indicadores
        Log.loga("Serão calculados os indicadores do ativo" + ativoBrasil);
        indicadores.calculaIndicadoresSerie(timeseries);

        //Baixa arquivo CSV e Converte arquivo para memória
        Log.loga("Importando o ativo " + ativoEst);
        importador = new Importador(ativoEst);
        timeseries = importador.montaTimeSeries();
        parametros.insereSerieTemporalEstrangeiro(timeseries);

        //Calcula indicadores
        Log.loga("Serão calculados os indicadores do ativo" + ativoEst);
        indicadores.calculaIndicadoresSerie(timeseries);
        
        //Filtro
        //TODO: Eliminar dados redundantes e etc

        Log.loga("Iniciando ajuste da base de dados");
        //Balanceia os parâmetros (Feriados, dias sem pregão, dias sem movimento)
        parametros.balance();



        Log.loga("Será inserida a variável alvo");
        parametros.criaTarget(nomeTimeSeries[0]);

        //Retorna o nome do arquivo gerado
        return geraArquivo(parametros);
    }

    //Gera arquivo ARFF
    private String geraArquivo(Parametros parametros) throws GeraArquivoARFFException, ParametrosException {


        try {

            String diretorio = LeituraProperties.getInstance().leituraProperties("prop.diretorioARFF");

            File dir = new File(diretorio);
            //Se o diretório existe
            if (!dir.exists()) {
                Log.loga("O diretório " + dir.getAbsolutePath() + " para criação do arquivo ARFF não existe");
                throw new GeraArquivoARFFException("Não foi possível gerar o arquivo ARFF");
            }

            //Abre o arquivo
            File file = new File(diretorio + parametros.getAtivo() + extARFF);
            Log.loga("Arquivo ARFF: " + file.getAbsolutePath());

            FileOutputStream arquivoGravacao = new FileOutputStream(file);
            OutputStreamWriter strWriter = new OutputStreamWriter(arquivoGravacao);
            BufferedWriter writer = new BufferedWriter(strWriter);

            writer.write("% This is a dataset obtained from the YAHOO FINANCES. Here is the included description:");
            writer.newLine();
            writer.write("%");
            writer.newLine();
            writer.write(new String("% The data provided are daily stock prices from #INICIO# through #FIM#, for #ATIVO#.").replaceAll("#INICIO#", LeituraProperties.getInstance().leituraProperties("prop.DataIni")).replaceAll("#FIM#", LeituraProperties.getInstance().leituraProperties("prop.DataFim")).replaceAll("#ATIVO#", parametros.getAtivo()));
            writer.newLine();
            writer.write("%");
            writer.newLine();
            writer.write("% Source: collection of regression datasets by Jean Felipe Hartz (jeanrsbr@gmail.com) at");
            writer.newLine();
            writer.write("% http://real-chart.finance.yahoo.com ");
            writer.newLine();
            writer.write(new String("% Characteristics: #CASES# cases, #ATTRIB# continuous attributes").replaceAll("#CASES#", Integer.toString(parametros.getNumReg())).replaceAll("#ATTRIB#", Integer.toString(parametros.getNumPar())));
            writer.newLine();
            writer.newLine();
            writer.write("@relation stock");
            writer.newLine();
            writer.newLine();


            //Obtém a lista de parâmetros existentes nos parâmetros
            SortedSet<String> nomesParametros = new TreeSet<>(parametros.getNomeParametros());

            for (String nomeParametro : nomesParametros) {
                writer.write("@attribute " + nomeParametro + " numeric");
                writer.newLine();
            }

            writer.write("@attribute " + "Target" + " numeric");
            writer.newLine();

            writer.newLine();
            writer.write("@data");
            writer.newLine();

            //Ordenado as chaves do HashMap
            SortedSet<Date> chaves = new TreeSet<>(parametros.getParametros().keySet());

            //Percorre as chaves do array
            for (Date chave : chaves) {

                StringBuilder linha = new StringBuilder();
                //Parametros existentes na data lida na chave
                ArrayList<Parametro> aux = parametros.getParametros().get(chave);

                //Exporta na mesma ordenação dos parâmetros
                for (String nomeParametro : nomesParametros) {
                    linha.append(editaDouble(parametros.getValorParametro(aux, nomeParametro)));
                    linha.append(",");
                }

                //Parâmetro target
                linha.append(editaDouble(parametros.getParTarget().get(chave)));

                //Exporta a linha para o arquivo
                writer.write(linha.toString());
                writer.newLine();

            }
            //Fecha o arquivo
            writer.close();
            return file.getAbsolutePath();

        } catch (IOException | IllegalArgumentException ex) {
            throw new GeraArquivoARFFException("Ocorreu erro no momento de gerar o arquivo ARFF", ex);
        }

    }
    /*
     * Realiza a formatação do número para evitar que ele seja representado
     * como notação cientifica quando o seu valor for muito grande
     */

    private String editaDouble(Double valor) {
        NumberFormat f = NumberFormat.getInstance();
        f.setGroupingUsed(false);
        f.setMaximumFractionDigits(2);
        return f.format(valor.doubleValue()).replaceAll(",", ".");
    }
}
