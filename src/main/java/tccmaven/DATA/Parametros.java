/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.DATA;

import eu.verdelhan.ta4j.TimeSeries;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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
    private HashMap<Date, double[]> parametros;

    //Ativo ao qual se referem os parâmetros
    public Parametros(String ativo, String[] nomeParametros) {
        this.ativo = ativo;
        this.nomeParametros = nomeParametros;
        parametros = new HashMap<>();
    }

    public String getAtivo() {
        return ativo;
    }

    public String[] getNomeParametros() {
        return nomeParametros;
    }

    public HashMap getParametros() {
        return parametros;
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
    public void criaTarget(String nomeTimeSeries) throws ParametrosException {

        //Ordenado as chaves do HashMap
        SortedSet<Date> chaves = new TreeSet<>(parametros.keySet());
        Iterator<Date> iterator = chaves.iterator();

        Date dateProx = null;
        Date dateAtu = null;

        //Varre as chaves
        while (true) {
            //Incrementa a data
            dateAtu = dateProx;

            //Se não possui um próximo elemento
            if (!iterator.hasNext()) {

                //Se não possui data atual (Primeiro registro)
                if (dateAtu == null) {
                    break;
                }
                //Insere o valor alvo (Apenas para não ficar com o último registro sem a variável TARGET
                insereValorTarget(dateAtu, 0d);
                break;
            }

            //Obtém a data do registro atual e do próximo
            dateProx = iterator.next();

            //Se não possui data atual (Primeiro registro)
            if (dateAtu == null) {
                continue;
            }

            //Obtém o valor do parâmetro posterior
            double valor = getValorParametro(parametros.get(dateProx), nomeTimeSeries + "HighPrice");
            //Insere o valor alvo
            insereValorTarget(dateAtu, valor);
        }
    }


    //Balanceia os registros contidos nos parâmetros
    public void balance() {

        //Ordenado as chaves do HashMap
        SortedSet<Date> chaves = new TreeSet<>(parametros.keySet());
        Iterator<Date> iterator = chaves.iterator();


        Date dateAnt = null;
        Date dateAtu = null;

        //Varre as chaves
        while (true) {

            //Se não possui um próximo elemento
            if (!iterator.hasNext()) {
                break;
            }

            //Obtém a data do registro atual e do anterior
            dateAnt = dateAtu;
            dateAtu = iterator.next();


            double[] valorAtual = parametros.get(dateAtu);

            boolean faltaValores = false;

            for (int i = 0; i < valorAtual.length; i++) {
                if (valorAtual[i] == -9999999999d) {
                    faltaValores = true;
                }
            }
            //Se não falta valores
            if (!faltaValores) {
                continue;
            }

            // Se não possui data atual (Primeiro registro)
            if (dateAnt == null) {
                //Se o primeiro registro estiver desbalanceado, não tem de onde copiar, o registro deve ser excluído
                parametros.remove(dateAtu);
                //Inicializa a data atual para forçar um reinicio
                dateAtu = null;
                //Vai para a próxima ocorrência
                continue;
            }

            double[] valorAnt = parametros.get(dateAnt);
            
            //Balanceia os parâmetros e inseri na ocorrência dos parâmetros
            parametros.put(dateAtu, balanceiaDados(valorAnt, valorAtual));

        }
    }

    private double[] balanceiaDados(double[] origem, double[] destino) {

        //Varre o destino procurando valores faltantes
        for (int i = 0; i < destino.length; i++) {
            //Se o destino está inválido
            if (destino[i] == -9999999999d) {
                destino[i] = origem[i];
            }
        }
        return destino;
    }

    
    //Retorna a ocorrência que o parâmetro deve ser inserido
    private int getOcoParametro(String nomeParametro) throws ParametrosException {

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
