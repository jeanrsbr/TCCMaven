/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.OUTPUT;

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
import tccmaven.MISC.LeituraProperties;
import tccmaven.MISC.Log;
import tccmaven.DATA.Parametro;
import tccmaven.DATA.Parametros;

/**
 *
 * @author Jean-NoteI5
 */
public class GeraArquivoARFF {

    private Parametros parametros;
    private final String extARFF = ".arff";

    public GeraArquivoARFF(Parametros parametros) {
        this.parametros = parametros;
    }

    //Gera arquivo ARFF
    public String geraArquivo() throws GeraArquivoARFFException {


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

            //Obtém a lista de parâmetros existentes nos parâmetros
            SortedSet<String> nomesParametros = new TreeSet<>(parametros.getNomeParametros());
            //Ordenado as chaves do HashMap
            SortedSet<Date> chaves = new TreeSet<>(parametros.getParametros().keySet());



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
            writer.write(new String("% Characteristics: #CASES# cases, #ATTRIB# continuous attributes").replaceAll("#CASES#", Integer.toString(chaves.size())).replaceAll("#ATTRIB#", Integer.toString(nomesParametros.size())));
            writer.newLine();
            writer.newLine();
            writer.write("@relation stock");
            writer.newLine();
            writer.newLine();

            for (String nomeParametro : nomesParametros) {
                writer.write("@attribute " + nomeParametro + " numeric");
                writer.newLine();
            }

            writer.newLine();
            writer.write("@data");
            writer.newLine();

            //Percorre as chaves do array
            for (Date chave : chaves) {
                StringBuilder linha = new StringBuilder();
                //Parametros existentes na data lida na chave
                ArrayList<Parametro> aux = parametros.getParametros().get(chave);

                //Exporta na mesma ordenação dos parâmetros
                for (String nomeParametro : nomesParametros) {
                    //Varre os parâmetros existentes
                    for (int i = 0; i < aux.size(); i++) {
                        //Se for parâmetro correto
                        if (aux.get(i).getDescricao().equals(nomeParametro)) {
                            /* 
                             * Realiza a formatação do número para evitar que ele seja representado 
                             * como notação cientifica quando o seu valor for muito grande
                             */
                            Double edita = aux.get(i).getValor();
                            NumberFormat f = NumberFormat.getInstance();
                            f.setGroupingUsed(false);
                            f.setMaximumFractionDigits(2);
                            String editado = f.format(edita.doubleValue()).replaceAll(",", ".");
                            linha.append(editado);
                            linha.append(",");
                        }
                    }
                }
                //Remove a última vírgula da literal
                linha.delete(linha.length() - 1, linha.length());
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
}
