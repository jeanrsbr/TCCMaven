/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven;

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import java.util.ArrayList;
import tccmaven.Parametros;

/**
 *
 * @author Jean-NoteI5
 */
public class Indicadores {
    
    Parametros parametros;

    public Indicadores(Parametros parametros) {
        this.parametros = parametros;
    }
    
    public void calculaIndicadores(){
        
        //Obtém as séries temporais
        ArrayList<TimeSeries> timeSeries = parametros.getTimeSeries();
        //Executa o cálculo dos indicadores para as séries temporais
        for (int i = 0; i < timeSeries.size(); i++) {
            calculaIndicadoresSerie(timeSeries.get(i));
        }
    }
    
    //Calcula indicadores da série temporal
    private void calculaIndicadoresSerie(TimeSeries timeSeries){
        
        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        SMAIndicator smaIndicator = new SMAIndicator(closePrice, 5);
        smaIndicator.getValue(0);
        
    }
    
}
