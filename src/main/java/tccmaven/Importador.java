/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven;

import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author Jean-NoteI5
 */
public class Importador {

    private String ativo;

    public Importador(String ativo) {
        this.ativo = ativo;
    }

    //Monta a série temporal
    public TimeSeries montaTimeSeries() throws ImportadorException, BaixaArquivoException {

        try {

            //Baixa arquivo CSV
            BaixaArquivo baixaArquivo = new BaixaArquivo(ativo);
            BufferedReader br = baixaArquivo.downloadArquivo();

            
            List<Tick> ticks = new ArrayList<Tick>();

            //Descarta a primeira linha
            br.readLine();

            //Varre o arquivo
            while (true) {
                String linha = br.readLine();

                if (linha == null) {
                    break;
                }
                String[] line = linha.split(",");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                //Adiciona o tick
                ticks.add(new Tick(new DateTime(formatter.parse(line[0])), Double.parseDouble(line[1]), Double.parseDouble(line[2]), Double.parseDouble(line[3]), Double.parseDouble(line[4]), Double.parseDouble(line[5])));
            }

            return new TimeSeries(ativo, ticks);
        } catch (IOException | ParseException ex) {
            throw new ImportadorException("Ocorreu um erro no momento de importar o arquivo CSV", ex);
        }
    }
}
