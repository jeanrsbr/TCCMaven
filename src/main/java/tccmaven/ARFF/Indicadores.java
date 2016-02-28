/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.ARFF;

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
import tccmaven.MISC.LeituraProperties;

/**
 *
 * @author Jean-NoteI5
 */
public class Indicadores {

    InsereParametros parametros;
    String pais;
    TimeSeries timeSeries;
    private int oco;

    public Indicadores(InsereParametros parametros, TimeSeries timeSeries) {
        this.parametros = parametros;
        this.timeSeries = timeSeries;
    }

    public InsereParametros getParametros() {
        return parametros;
    }

    public void setPaisBrasil() {
        pais = "Bra";
    }

    public void setPaisEstrangeiro() {
        pais = "Est";
    }

    //Calcula indicadores da série temporal
    public void calculaIndicadoresSerie() throws IndicadoresException, InsereParametrosException, NomeParametrosException {

        if (pais.equals("")) {
            throw new IndicadoresException("Não foi informado o código do país para gerar os indicadores");
        }

        calculaSMA(Integer.parseInt(LeituraProperties.getInstance().leituraProperties("ind.SMA")));
        calculaIFR(Integer.parseInt(LeituraProperties.getInstance().leituraProperties("ind.IFR")));
        calculaStochasticOscilatorKD(Integer.parseInt(LeituraProperties.getInstance().leituraProperties("ind.StochasticOscilatorKD")));
        calculaOnBalanceVolume();
        calculaMACD(12, 26);
        calculaPVT();
        calculaWillianR(Integer.parseInt(LeituraProperties.getInstance().leituraProperties("ind.WillianR")));
        calculaROC(Integer.parseInt(LeituraProperties.getInstance().leituraProperties("ind.ROC")));
        calculaMomentum(Integer.parseInt(LeituraProperties.getInstance().leituraProperties("ind.Momentum")));
        calculaBIAS(Integer.parseInt(LeituraProperties.getInstance().leituraProperties("ind.BIAS")));
        calculaADMI(Integer.parseInt(LeituraProperties.getInstance().leituraProperties("ind.ADMI")));

    }

    //Cálcula média móvel para 5 períodos
    private void calculaSMA(int periodo) throws InsereParametrosException, NomeParametrosException {

        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        SMAIndicator smaIndicator = new SMAIndicator(closePrice, periodo);

        //Varre os indicadores obtidos
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), smaIndicator.getValue(i).toDouble(), pais, "MA", periodo);
        }
    }

    //Calcula o índice de força relativa
    private void calculaIFR(int periodo) throws InsereParametrosException, NomeParametrosException {

        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice, periodo);
        //Varre os indicadores obtidos
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), rsiIndicator.getValue(i).toDouble(), pais, "RSI", periodo);
        }

    }

    //Calcula o índice Stochastic Oscilator K & D
    private void calculaStochasticOscilatorKD(int periodo) throws InsereParametrosException, NomeParametrosException {

        StochasticOscillatorKIndicator soki = new StochasticOscillatorKIndicator(timeSeries, periodo);
        StochasticOscillatorDIndicator sodi = new StochasticOscillatorDIndicator(soki);

        //Varre os indicadores obtidos
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), soki.getValue(i).toDouble(), pais, "SOKI", periodo);
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), sodi.getValue(i).toDouble(), pais, "SODI", periodo);
        }

    }

    //Calcula o índice On Balance Volume
    private void calculaOnBalanceVolume() throws InsereParametrosException, NomeParametrosException {

        OnBalanceVolumeIndicator onBalanceVolume = new OnBalanceVolumeIndicator(timeSeries);
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), onBalanceVolume.getValue(i).toDouble(), pais, "OBV");
        }
    }

    //Calcula o índice MACD
    private void calculaMACD(int periodoCurto, int periodoLongo) throws InsereParametrosException, NomeParametrosException {

        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        MACDIndicator mACD = new MACDIndicator(closePrice, periodoCurto, periodoLongo);
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), mACD.getValue(i).toDouble(), pais, "MACD");
        }
    }

    //Calcula o índice Willians %R
    private void calculaWillianR(int periodo) throws InsereParametrosException, NomeParametrosException {
        WilliamsRIndicator williamsRIndicator = new WilliamsRIndicator(timeSeries, periodo);
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), williamsRIndicator.getValue(i).toDouble(), pais, "WR", periodo);
        }
    }

    //Calcula o índice Momentum
    private void calculaMomentum(int periodo) throws InsereParametrosException, NomeParametrosException {

        //Varre a série temporal
        for (int i = 0; i < timeSeries.getTickCount(); i++) {

            //Se estiver no inicio da série temporal
            if (periodo > i) {
                parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), 0d, pais, "Momentum", periodo);
                continue;
            }
            //CLOSE n - CLOSE n-p
            Double momentum = timeSeries.getTick(i).getClosePrice().toDouble() - timeSeries.getTick(i - periodo).getClosePrice().toDouble();
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), momentum, pais, "Momentum", periodo);
        }


    }

    //Calcula o índice Price Rate Of Change
    private void calculaROC(int periodo) throws InsereParametrosException, NomeParametrosException {

        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        ROCIndicator rOCIndicator = new ROCIndicator(closePrice, periodo);

        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), rOCIndicator.getValue(i).toDouble(), pais, "PROC", periodo);
        }
    }

    //Calcula o índice Price Volume Trend
    private void calculaPVT() throws InsereParametrosException, NomeParametrosException {

        Double pVTAnterior = 0d;
        //Varre a série temporal
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            //Se estiver na primeira ocorrência
            if (i == 0) {
                parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), 0d, pais, "PVT");
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

            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), pVT, pais, "PVT");
            //Atualiza pVTAnterior
            pVTAnterior = pVT;
        }
    }

    //Calcula o índice BIAS
    private void calculaBIAS(int periodo) throws InsereParametrosException, NomeParametrosException {

        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(timeSeries);
        SMAIndicator sMAIndicator = new SMAIndicator(closePriceIndicator, periodo);

        //Varre a série temporal
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            Double closePrice = timeSeries.getTick(i).getClosePrice().toDouble();
            Double sMA = sMAIndicator.getValue(i).toDouble();
            Double bIAS = ((closePrice - sMA) / sMA) * 100;
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), bIAS, pais, "BIAS", periodo);
        }
    }

    private void calculaADMI(int periodo) throws InsereParametrosException, NomeParametrosException {

        AverageDirectionalMovementIndicator admi = new AverageDirectionalMovementIndicator(timeSeries, periodo);
        //Varre a série temporal
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), admi.getValue(i).toDouble(), pais, "ADMI", periodo);
        }

    }
}
