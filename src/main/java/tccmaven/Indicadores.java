/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven;

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.oscillators.StochasticOscillatorDIndicator;
import eu.verdelhan.ta4j.indicators.oscillators.StochasticOscillatorKIndicator;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.AverageDirectionalMovementIndicator;
import eu.verdelhan.ta4j.indicators.trackers.MACDIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ROCIndicator;
import eu.verdelhan.ta4j.indicators.trackers.RSIIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.WilliamsRIndicator;
import eu.verdelhan.ta4j.indicators.volume.OnBalanceVolumeIndicator;
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

        calculaSMA(timeSeries, Integer.parseInt(LeituraProperties.getInstance().leituraProperties("ind.SMA")));
        calculaIFR(timeSeries, Integer.parseInt(LeituraProperties.getInstance().leituraProperties("ind.IFR")));
        calculaStochasticOscilatorKD(timeSeries, Integer.parseInt(LeituraProperties.getInstance().leituraProperties("ind.StochasticOscilatorKD")));
        calculaOnBalanceVolume(timeSeries);
        calculaMACD(timeSeries, 12, 26);
        calculaPVT(timeSeries);
        //TODO: Achar o período mais comum
        calculaWillianR(timeSeries, Integer.parseInt(LeituraProperties.getInstance().leituraProperties("ind.WillianR")));
        //TODO: Achar o período mais comum
        calculaROC(timeSeries, Integer.parseInt(LeituraProperties.getInstance().leituraProperties("ind.ROC")));
        //TODO: Achar o período mais comum
        calculaMomentum(timeSeries, Integer.parseInt(LeituraProperties.getInstance().leituraProperties("ind.Momentum")));
        //TODO: Achar o período mais comum
        calculaBIAS(timeSeries, Integer.parseInt(LeituraProperties.getInstance().leituraProperties("ind.BIAS")));
        //TODO: Achar o período mais comum
        calculaADMI(timeSeries, Integer.parseInt(LeituraProperties.getInstance().leituraProperties("ind.ADMI")));

    }

    //Cálcula média móvel para 5 períodos
    private void calculaSMA(TimeSeries timeSeries, int periodo) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        SMAIndicator smaIndicator = new SMAIndicator(closePrice, periodo);

        //Varre os indicadores obtidos
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "MA" + periodo, smaIndicator.getValue(i).toDouble());
        }
    }

    //Calcula o índice de força relativa
    private void calculaIFR(TimeSeries timeSeries, int periodo) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice, periodo);
        //Varre os indicadores obtidos
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "RSI" + periodo, rsiIndicator.getValue(i).toDouble());
        }

    }
    //Calcula o índice Stochastic Oscilator K & D

    private void calculaStochasticOscilatorKD(TimeSeries timeSeries, int periodo) {

        StochasticOscillatorKIndicator soki = new StochasticOscillatorKIndicator(timeSeries, periodo);
        StochasticOscillatorDIndicator sodi = new StochasticOscillatorDIndicator(soki);

        //Varre os indicadores obtidos
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "SOKI" + periodo, soki.getValue(i).toDouble());
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "SODI" + periodo, sodi.getValue(i).toDouble());
        }

    }

    //Calcula o índice On Balance Volume
    private void calculaOnBalanceVolume(TimeSeries timeSeries) {
        OnBalanceVolumeIndicator onBalanceVolume = new OnBalanceVolumeIndicator(timeSeries);
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "OBV", onBalanceVolume.getValue(i).toDouble());
        }
    }

    //Calcula o índice MACD
    private void calculaMACD(TimeSeries timeSeries, int periodoCurto, int periodoLongo) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        MACDIndicator mACD = new MACDIndicator(closePrice, periodoCurto, periodoLongo);
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "MACD", mACD.getValue(i).toDouble());
        }
    }

    //Calcula o índice Willians %R
    private void calculaWillianR(TimeSeries timeSeries, int periodo) {

        WilliamsRIndicator williamsRIndicator = new WilliamsRIndicator(timeSeries, periodo);
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "WR" + periodo, williamsRIndicator.getValue(i).toDouble());
        }
    }

    //Calcula o índice Momentum
    private void calculaMomentum(TimeSeries timeSeries, int periodo) {

        //Varre a série temporal
        for (int i = 0; i < timeSeries.getTickCount(); i++) {

            //Se estiver no inicio da série temporal
            if (periodo > i) {
                continue;
            }
            //CLOSE n - CLOSE n-p
            Double momentum = timeSeries.getTick(i).getClosePrice().toDouble() - timeSeries.getTick(i - periodo).getClosePrice().toDouble();
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "Momentum" + periodo, momentum);
        }


    }

    //Calcula o índice Price Rate Of Change
    private void calculaROC(TimeSeries timeSeries, int periodo) {

        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        ROCIndicator rOCIndicator = new ROCIndicator(closePrice, periodo);

        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "PROC" + periodo, rOCIndicator.getValue(i).toDouble());
        }
    }

    //Calcula o índice Price Volume Trend
    private void calculaPVT(TimeSeries timeSeries) {

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
    }

    //Calcula o índice BIAS
    private void calculaBIAS(TimeSeries timeSeries, int periodo) {
        
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(timeSeries);
        SMAIndicator sMAIndicator = new SMAIndicator(closePriceIndicator, periodo);
        
        //Varre a série temporal
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            Double closePrice = timeSeries.getTick(i).getClosePrice().toDouble();
            Double sMA = sMAIndicator.getValue(i).toDouble();
            Double bIAS = ((closePrice - sMA) / sMA) * 100;
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "BIAS" + periodo, bIAS);
        }
    }
    
    
    private void calculaADMI(TimeSeries timeSeries, int periodo) {
        
        AverageDirectionalMovementIndicator admi = new AverageDirectionalMovementIndicator(timeSeries, periodo);
        //Varre a série temporal
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereIndicadorTecnico(timeSeries.getName(), timeSeries.getTick(i).getEndTime().toDate(), "ADMI" + periodo, admi.getValue(i).toDouble());
        }
        
    }
}
