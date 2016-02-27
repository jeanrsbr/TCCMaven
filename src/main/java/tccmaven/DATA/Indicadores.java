/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.DATA;

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

    Parametros parametros;
    String pais;
    TimeSeries timeSeries;

    public Indicadores(Parametros parametros, TimeSeries timeSeries) {
        this.parametros = parametros;
        this.timeSeries = timeSeries;
    }

    public Parametros getParametros() {
        return parametros;
    }

    public void setPaisBrasil() {
        pais = "Bra";
    }

    public void setPaisEstrangeiro() {
        pais = "Est";
    }

    //Calcula indicadores da série temporal
    public void calculaIndicadoresSerie() throws IndicadoresException, ParametrosException {

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
    private void calculaSMA(int periodo) throws ParametrosException {

        String nomeParametro = parametros.montaNomeParametro(pais, "MA", periodo);
        int oco = parametros.getOcoParametro(nomeParametro);

        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        SMAIndicator smaIndicator = new SMAIndicator(closePrice, periodo);

        //Varre os indicadores obtidos
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), smaIndicator.getValue(i).toDouble(), oco);
        }
    }

    //Calcula o índice de força relativa
    private void calculaIFR(int periodo) throws ParametrosException {

        String nomeParametro = parametros.montaNomeParametro(pais, "RSI", periodo);
        int oco = parametros.getOcoParametro(nomeParametro);

        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice, periodo);
        //Varre os indicadores obtidos
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), rsiIndicator.getValue(i).toDouble(), oco);
        }

    }

    //Calcula o índice Stochastic Oscilator K & D
    private void calculaStochasticOscilatorKD(int periodo) throws ParametrosException {

        String nomeParametroSOKI = parametros.montaNomeParametro(pais, "SOKI", periodo);
        String nomeParametroSODI = parametros.montaNomeParametro(pais, "SODI", periodo);
        int ocoSOKI = parametros.getOcoParametro(nomeParametroSOKI);
        int ocoSODI = parametros.getOcoParametro(nomeParametroSODI);

        StochasticOscillatorKIndicator soki = new StochasticOscillatorKIndicator(timeSeries, periodo);
        StochasticOscillatorDIndicator sodi = new StochasticOscillatorDIndicator(soki);

        //Varre os indicadores obtidos
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), soki.getValue(i).toDouble(), ocoSOKI);
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), sodi.getValue(i).toDouble(), ocoSODI);
        }

    }

    //Calcula o índice On Balance Volume
    private void calculaOnBalanceVolume() throws ParametrosException {

        String nomeParametro = parametros.montaNomeParametro(pais, "OBV");
        int oco = parametros.getOcoParametro(nomeParametro);

        OnBalanceVolumeIndicator onBalanceVolume = new OnBalanceVolumeIndicator(timeSeries);
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), onBalanceVolume.getValue(i).toDouble(), oco);
        }
    }

    //Calcula o índice MACD
    private void calculaMACD(int periodoCurto, int periodoLongo) throws ParametrosException {

        String nomeParametro = parametros.montaNomeParametro(pais, "MACD");
        int oco = parametros.getOcoParametro(nomeParametro);

        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        MACDIndicator mACD = new MACDIndicator(closePrice, periodoCurto, periodoLongo);
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), mACD.getValue(i).toDouble(), oco);
        }
    }

    //Calcula o índice Willians %R
    private void calculaWillianR(int periodo) throws ParametrosException {
        String nomeParametro = parametros.montaNomeParametro(pais, "WR", periodo);
        int oco = parametros.getOcoParametro(nomeParametro);

        WilliamsRIndicator williamsRIndicator = new WilliamsRIndicator(timeSeries, periodo);
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), williamsRIndicator.getValue(i).toDouble(), oco);
        }
    }

    //Calcula o índice Momentum
    private void calculaMomentum(int periodo) throws ParametrosException {
        String nomeParametro = parametros.montaNomeParametro(pais, "Momentum", periodo);
        int oco = parametros.getOcoParametro(nomeParametro);

        //Varre a série temporal
        for (int i = 0; i < timeSeries.getTickCount(); i++) {

            //Se estiver no inicio da série temporal
            if (periodo > i) {
                parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), 0d, oco);
                continue;
            }
            //CLOSE n - CLOSE n-p
            Double momentum = timeSeries.getTick(i).getClosePrice().toDouble() - timeSeries.getTick(i - periodo).getClosePrice().toDouble();
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), momentum, oco);
        }


    }

    //Calcula o índice Price Rate Of Change
    private void calculaROC(int periodo) throws ParametrosException {

        String nomeParametro = parametros.montaNomeParametro(pais, "PROC", periodo);
        int oco = parametros.getOcoParametro(nomeParametro);

        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        ROCIndicator rOCIndicator = new ROCIndicator(closePrice, periodo);

        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), rOCIndicator.getValue(i).toDouble(), oco);
        }
    }

    //Calcula o índice Price Volume Trend
    private void calculaPVT() throws ParametrosException {

        String nomeParametro = parametros.montaNomeParametro(pais, "PVT");
        int oco = parametros.getOcoParametro(nomeParametro);


        Double pVTAnterior = 0d;
        //Varre a série temporal
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            //Se estiver na primeira ocorrência
            if (i == 0) {
                parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), 0d, oco);
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

            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), pVT, oco);
            //Atualiza pVTAnterior
            pVTAnterior = pVT;
        }
    }

    //Calcula o índice BIAS
    private void calculaBIAS(int periodo) throws ParametrosException {

        String nomeParametro = parametros.montaNomeParametro(pais, "PVT", periodo);
        int oco = parametros.getOcoParametro(nomeParametro);

        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(timeSeries);
        SMAIndicator sMAIndicator = new SMAIndicator(closePriceIndicator, periodo);

        //Varre a série temporal
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            Double closePrice = timeSeries.getTick(i).getClosePrice().toDouble();
            Double sMA = sMAIndicator.getValue(i).toDouble();
            Double bIAS = ((closePrice - sMA) / sMA) * 100;
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), bIAS, oco);
        }
    }

    private void calculaADMI(int periodo) throws ParametrosException {

        String nomeParametro = parametros.montaNomeParametro(pais, "ADMI", periodo);
        int oco = parametros.getOcoParametro(nomeParametro);

        AverageDirectionalMovementIndicator admi = new AverageDirectionalMovementIndicator(timeSeries, periodo);
        //Varre a série temporal
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            parametros.insereValor(timeSeries.getTick(i).getEndTime().toDate(), admi.getValue(i).toDouble(), oco);
        }

    }
}
