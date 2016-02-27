/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.DATA;

import eu.verdelhan.ta4j.TimeSeries;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

/**
 *
 * @author Jean-NoteI5
 */
public class Parametros {

    //Nome do ativo
    private String ativo;
    //Lista de parâmetros a serem exportados
    String[] nomeParametros;
    //Parâmetros montados a partir das séries temporais HASHMAP
    private TreeMap<Date, double[]> parametros;

    //Ativo ao qual se referem os parâmetros
    public Parametros(String ativo, String[] nomeParametros) {
        this.ativo = ativo;
        this.nomeParametros = nomeParametros;
        //Ajusta o último parâmetro para possuir o nome fixo de TARGET
        this.nomeParametros[nomeParametros.length - 1] = "Target";
        parametros = new TreeMap<>();
    }

    public String getAtivo() {
        return ativo;
    }

    public String[] getNomeParametros() {
        return nomeParametros;
    }

    public ArrayList getParametros() {
        return new ArrayList<>(parametros.values());
    }

    public int getNumPar() {
        //Retorna a quantidade de parâmetros adicionados
        return nomeParametros.length;
    }

    public int getNumReg() {
        //Retorna a quantidade de registros
        return parametros.size();
    }

    public void insereSerieTemporalBrasil(TimeSeries timeSeries) throws ParametrosException {
        insereSerieTemporal(timeSeries, "Bra");
    }

    public void insereSerieTemporalEstrangeiro(TimeSeries timeSeries) throws ParametrosException {
        insereSerieTemporal(timeSeries, "Est");
    }

    //Insere série temporal do ativo
    private void insereSerieTemporal(TimeSeries timeSeries, String pais) throws ParametrosException {

        //Verifica a ocorrência dos parâmetros
        int closePrice = getOcoParametro(montaNomeParametro(pais, "ClosePrice"));
        int OpenPrice = getOcoParametro(montaNomeParametro(pais, "OpenPrice"));
        int HighPrice = getOcoParametro(montaNomeParametro(pais, "HighPrice"));
        int LowPrice = getOcoParametro(montaNomeParametro(pais, "LowPrice"));
        int Volume = getOcoParametro(montaNomeParametro(pais, "Volume"));

        //Varre a série temporal obtida
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            //Obtém a data da ocorrência
            Date data = timeSeries.getTick(i).getEndTime().toDate();
            //Adiciona o preço de fechamento
            insereValor(data, timeSeries.getTick(i).getClosePrice().toDouble(), closePrice);
            //Adiciona o preço de abertura
            insereValor(data, timeSeries.getTick(i).getOpenPrice().toDouble(), OpenPrice);
            //Adiciona o maior preços
            insereValor(data, timeSeries.getTick(i).getMaxPrice().toDouble(), HighPrice);
            //Adiciona o menor preço
            insereValor(data, timeSeries.getTick(i).getMinPrice().toDouble(), LowPrice);
            //Adiciona o volume
            insereValor(data, timeSeries.getTick(i).getVolume().toDouble(), Volume);
        }

    }

    //Insere um valor do ativo
    public void insereValor(Date data, double valIndicador, int oco) {

        //Obtém os valores da data processada
        double[] valores = parametros.get(data);

        //Se ainda não possui parâmetros associados a data
        if (valores == null) {
            valores = new double[nomeParametros.length];

            //TODO: GAMBIARRA
            for (int i = 0; i < valores.length; i++) {
                valores[i] = -9999999999d;

            }
        }

        //Se o parâmetro possui valor inválido
        if (Double.isNaN(valIndicador)) {
            valIndicador = 0d;
        }
        //Atualiza o valor da ocorrência
        valores[oco] = valIndicador;
        //Devolve a ocorrência para o array
        parametros.put(data, valores);
    }

    //Popula variável target
    public void criaTarget(String nomeParametro) throws ParametrosException {
        //Encontra a ocorrência do parâmetro
        int oco = getOcoParametro(nomeParametro);
        //Converte o TreeMap para um ArrayList
        ArrayList<double[]> lista = new ArrayList<>(parametros.values());
        //Varre as ocorrências da lista (Não processa a última ocorrência)
        for (int i = 0; i < lista.size() - 1; i++) {
            //Obtém o array de parâmetros da ocorrência atual
            double[] par = lista.get(i);
            //Atribuir a última ocorrência dos parâmetros os valor do parâmetro na ocorrência posterior
            par[par.length - 1] = lista.get(i + 1)[oco];
            //Devolve o valor a lista
            lista.set(i, par);
        }
    }

    //Balanceia os registros contidos nos parâmetros
    public void balance() {

        //Converte o TreeMap para um ArrayList
        ArrayList<double[]> lista = new ArrayList<>(parametros.values());

        //Varre os registros do ArrayList
        for (int i = 0; i < lista.size(); i++) {

            //Obtém o array de parâmetros presente na lista
            double[] par = lista.get(i);

            //Varre os parâmetros
            for (int j = 0; j < par.length; j++) {
                //Se estiver com conteúdo inválido pega o conteúdo da ocorrência anterior
                if (par[j] == -9999999999d) {
                    //Se for a primeira ocorrência
                    if (i == 0) {
                        //Apenas zera
                        par[j] = 0d;
                    } else {
                        //Inicializa com o conteúdo da ocorrência anterior
                        par[j] = lista.get(i - 1)[j];
                    }
                }
            }
            lista.set(i, par);
        }
    }

    //Retorna a ocorrência que o parâmetro deve ser inserido
    public int getOcoParametro(String nomeParametro) throws ParametrosException {

        for (int i = 0; i < nomeParametros.length; i++) {

            if (nomeParametros[i].equals(nomeParametro)) {
                return i;
            }
        }
        throw new ParametrosException("Não foi encontrada a ocorrência do parâmetro indicado");
    }

    //Monta a literal de nome do parâmetro
    public String montaNomeParametro(String pais, String parametro, int periodo) {
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumIntegerDigits(2);
        f.setMinimumIntegerDigits(2);
        return pais + "_" + parametro + "_" + f.format(periodo);
    }

    //Monta a literal de nome do parâmetro
    public String montaNomeParametro(String pais, String parametro) {
        return pais + "_" + parametro;
    }
}
