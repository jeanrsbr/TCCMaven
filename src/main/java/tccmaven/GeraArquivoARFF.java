/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Jean-NoteI5
 */
public class GeraArquivoARFF {
    
    private Map<Date, Parametros> parametros;
    private String ativo;
    private final String nomeARFF = ".ARFF";
    
    public GeraArquivoARFF(Map<Date, Parametros> linhaArquivo, String ativo) {
        this.parametros = linhaArquivo;
        this.ativo = ativo;
    }

    //Gera arquivo ARFF
    public void geraArquivo() throws GeraArquivoARFFException {
        
        
        try {
            //Abre o arquivo

            File file = new File(LeituraProperties.getInstance().leituraProperties("prop.diretorioARFF") + ativo + nomeARFF);
            Log.loga("Arquivo ARFF: " + file.getAbsolutePath());
            FileOutputStream arquivoGravacao = new FileOutputStream(file);
            OutputStreamWriter strWriter = new OutputStreamWriter(arquivoGravacao);
            BufferedWriter writer = new BufferedWriter(strWriter);
            
            
            writer.write("% This is a dataset obtained from the YAHOO FINANCES. Here is the included description:");
            writer.newLine();
            writer.write("%");
            writer.newLine();
            writer.write(new String("% The data provided are daily stock prices from #INICIO# through #FIM#, for #ATIVO#.").replaceAll("#INICIO#", LeituraProperties.getInstance().leituraProperties("prop.DataIni")).replaceAll("#FIM#", LeituraProperties.getInstance().leituraProperties("prop.DataFim")).replaceAll("#ATIVO#", ativo));
            writer.newLine();
            writer.write("%");
            writer.newLine();
            writer.write("% Source: collection of regression datasets by Jean Felipe Hartz (jeanrsbr@gmail.com) at");
            writer.newLine();
            writer.write("% http://real-chart.finance.yahoo.com ");
            writer.newLine();
            writer.newLine();
            writer.newLine();
            writer.write("@relation stock");
            writer.newLine();
            writer.newLine();

            //Obtém os campos da classe Parâmetros
            Field field[] = Parametros.class.getDeclaredFields();

            //Varre os campos existentes na classe para exportar o cabeçalho
            for (int i = 0; i < field.length; i++) {
                //Ignora o campo de data
                if (field[i].getName().equals("date")) {
                    continue;
                }
                writer.write("@attribute " + field[i].getName() + " numeric");
                writer.newLine();
            }
            
            writer.newLine();
            writer.write("@data");
            writer.newLine();
            writer.newLine();


            //Ordenado as chaves do HashMap
            SortedSet<Date> chaves = new TreeSet<>(parametros.keySet());

            //Percorre as chaves do array
            for (Date chave : chaves) {
                StringBuilder linha = new StringBuilder();
                for (int j = 0; j < field.length; j++) {

                    //Ignora o campo de data
                    if (field[j].getName().equals("date")) {
                        continue;
                    }

                    /* 
                     * Realiza a formatação do número para evitar que ele seja representado 
                     * como notação cientifica quando o seu valor for muito grande
                     */
                    Double edita = field[j].getDouble(parametros.get(chave));
                    
                    
                    
                    NumberFormat f = NumberFormat.getInstance();
                    f.setGroupingUsed(false);
                    f.setMaximumFractionDigits(2);
                    String editado = f.format(edita.doubleValue()).replaceAll(",", ".");
                    linha.append(editado);
                    if (j < field.length - 1) {
                        linha.append(",");
                    }
                    
                }
                writer.write(linha.toString());
                writer.newLine();
                
            }
            //Fecha o arquivo
            writer.close();
            
        } catch (IOException | IllegalArgumentException | IllegalAccessException ex) {
            throw new GeraArquivoARFFException("Ocorreu erro no momento de gerar o arquivo ARFF", ex);
        }
        
    }
}
