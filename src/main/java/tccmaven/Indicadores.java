/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven;

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.oscillators.StochasticOscillatorDIndicator;
import eu.verdelhan.ta4j.indicators.oscillators.StochasticOscillatorKIndicator;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.simple.PriceVariationIndicator;
import eu.verdelhan.ta4j.indicators.simple.VolumeIndicator;
import eu.verdelhan.ta4j.indicators.trackers.MACDIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ROCIndicator;
import eu.verdelhan.ta4j.indicators.trackers.RSIIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.WilliamsRIndicator;
import eu.verdelhan.ta4j.indicators.volume.OnBalanceVolumeIndicator;
import java.util.ArrayList;
import javax.swing.text.Position;

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


        calculaMediaMovel(timeSeries, 5);
        calculaIndiceForçaRelativa(timeSeries, 14);
        calculaIndiceStochasticOscilatorKD(timeSeries, 14);
        calculaIndiceOnBalanceVolume(timeSeries);
        calculaIndiceMACD(timeSeries, 12, 26);
        calculaIndicePVT(timeSeries);
        //TODO: Achar o período mais comum
        calculaIndiceWR(timeSeries, 14);
        //TODO: Achar o período mais comum
        calculaIndiceROC(timeSeries, 14);
        //TODO: Achar o período mais comum
        calculaIndiceMomentum(timeSeries, 14);
        
    }

    //Cálcula média móvel para 5 períodos
    private void calculaMediaMovel(TimeSeries timeSeries, int periodos) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        SMAIndicator smaIndicator = new SMAIndicator(closePrice, periodos);

        //Varre os indicadores obtidos
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "MA5", smaIndicator.getValue(i).toDouble());
        }
    }

    //Calcula o índice de força relativa
    private void calculaIndiceForçaRelativa(TimeSeries timeSeries, int periodos) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice, periodos);
        //Varre os indicadores obtidos
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "RSI", rsiIndicator.getValue(i).toDouble());
        }

    }
    //Calcula o índice Stochastic Oscilator K & D

    private void calculaIndiceStochasticOscilatorKD(TimeSeries timeSeries, int periodos) {

        StochasticOscillatorKIndicator soki = new StochasticOscillatorKIndicator(timeSeries, periodos);
        StochasticOscillatorDIndicator sodi = new StochasticOscillatorDIndicator(soki);

        //Varre os indicadores obtidos
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "SOKI", soki.getValue(i).toDouble());
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "SODI", sodi.getValue(i).toDouble());
        }

    }

    //Calcula o índice On Balance Volume
    private void calculaIndiceOnBalanceVolume(TimeSeries timeSeries) {
        OnBalanceVolumeIndicator onBalanceVolume = new OnBalanceVolumeIndicator(timeSeries);
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "OBV", onBalanceVolume.getValue(i).toDouble());
        }
    }

    //Calcula o índice MACD
    private void calculaIndiceMACD(TimeSeries timeSeries, int periodoCurto, int periodoLongo) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        MACDIndicator mACD = new MACDIndicator(closePrice, periodoCurto, periodoLongo);
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "MACD", mACD.getValue(i).toDouble());
        }
    }

    //Calcula o índice Willians %R
    private void calculaIndiceWR(TimeSeries timeSeries, int periodo) {

        WilliamsRIndicator williamsRIndicator = new WilliamsRIndicator(timeSeries, periodo);
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "WR", williamsRIndicator.getValue(i).toDouble());
        }
    }

    //Calcula o índice Momentum
    private void calculaIndiceMomentum(TimeSeries timeSeries, int periodo) {

        //Varre a série temporal
        for (int i = 0; i < timeSeries.getTickCount(); i++) {

            //Se estiver no inicio da série temporal
            if (periodo > i) {
                continue;
            }
            //CLOSE n - CLOSE n-p
            Double momentum = timeSeries.getTick(i).getClosePrice().toDouble() - timeSeries.getTick(i - periodo).getClosePrice().toDouble();
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "Momentum", momentum);
        }


    }

    //Calcula o índice Price Rate Of Change
    private void calculaIndiceROC(TimeSeries timeSeries, int periodo) {

        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        ROCIndicator rOCIndicator = new ROCIndicator(closePrice, periodo);

        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "PROC", rOCIndicator.getValue(i).toDouble());
        }
    }

    //Calcula o índice Price Volume Trend
    private void calculaIndicePVT(TimeSeries timeSeries) {

        Double pVTAnterior = 0d;
        //Varre a série temporal
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            //Se estiver na primeira ocorrência
            if (i == 0) {
                continue;
            }
            //Preço de fechamento atual
            Double closePrice = timeSeries.getTick(i).getClosePrice().toDouble();
            //Preço de fechamento anterior
            Double closePriceAnt = timeSeries.getTick(i - 1).getClosePrice().toDouble();
            //Volume atual
            Double volume = timeSeries.getTick(i).getVolume().toDouble();
            //VPTprev + Volume x ((CloseN - CloseN-1) / CloseN-1) 
            Double pVT = pVTAnterior + volume * ((closePrice - closePriceAnt) / closePriceAnt);

            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "PVT", pVT);
            //Atualiza pVTAnterior
            pVTAnterior = pVT;
        }

        
        //Calcula o índice BIAS
        
    }
}
