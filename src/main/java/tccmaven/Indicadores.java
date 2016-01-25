/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven;

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.RSIIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import java.util.ArrayList;

/**
 *
 * @author Jean-NoteI5
 */
public class Indicadores {

    Parametros parametros;

    public Indicadores(Parametros parametros) {
        this.parametros = parametros;
    }

    public Parametros getParametros() {
        return parametros;
    }
    

    public void calculaIndicadores() {

        //Obtém as séries temporais
        ArrayList<TimeSeries> timeSeries = parametros.getTimeSeries();
        //Executa o cálculo dos indicadores para as séries temporais
        for (int i = 0; i < timeSeries.size(); i++) {
            calculaIndicadoresSerie(timeSeries.get(i));
        }
    }

    //Calcula indicadores da série temporal
    private void calculaIndicadoresSerie(TimeSeries timeSeries) {

//        TODO: Configurar no PROPRIERTIES os períodos os indicadores
        
        //Calcula média móvel para 5 períodos
        calculaMediaMovelClosePrice(timeSeries, 5);
        //Calcula o índice de força relativa
        calculaIndiceForçaRelativa(timeSeries, 14);
    }
    
    
    
    //Cálcula média móvel para 5 períodos
    private void calculaMediaMovelClosePrice(TimeSeries timeSeries, int periodos) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        SMAIndicator smaIndicator = new SMAIndicator(closePrice, periodos);
        
        //Varre os indicadores obtidos
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "MA5", smaIndicator.getValue(i).toDouble());
        }
    }
    
    //Calcula o índice de força relativa
    private void calculaIndiceForçaRelativa(TimeSeries timeSeries, int periodos){
        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice, periodos);
        //Varre os indicadores obtidos
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "RSI", rsiIndicator.getValue(i).toDouble());
        }
        
    }
}
