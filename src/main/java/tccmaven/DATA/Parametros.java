/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.DATA;

import eu.verdelhan.ta4j.TimeSeries;
import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Jean-NoteI5
 */
public class Parametros {

    //Nome do ativo
    private String ativo;
    //Séries temporais do ativo
    private ArrayList<TimeSeries> timeSeries;
    //Parâmetros
    private HashSet<String> nomeParametros;
    //Parâmetros montados a partir das séries temporais
    private HashMap<Date, ArrayList<Parametro>> parametros;
    //Parâmetro alvo
    private HashMap<Date, Double> parTarget;

    //Ativo ao qual se referem os parâmetros
    public Parametros(String ativo) {
        this.ativo = ativo;
        timeSeries = new ArrayList<>();
        nomeParametros = new HashSet<>();
        parametros = new HashMap<>();

    }

    public ArrayList<TimeSeries> getTimeSeries() {
        return timeSeries;
    }

    public String getAtivo() {
        return ativo;
    }

    public HashSet<String> getNomeParametros() {
        return nomeParametros;
    }

    public HashMap<Date, ArrayList<Parametro>> getParametros() {
        return parametros;
    }

    public HashMap<Date, Double> getParTarget() {
        return parTarget;
    }

    public int getNumPar() {
        //Retorna a quantidade de parâmetros adicionados + parâmetro alvo
        return nomeParametros.size() + 1;
    }

    public int getNumReg() {
        //Retorna a quantidade de registros
        return parametros.size();
    }

    //Insere série temporal do ativo
    public void insereSerieTemporal(TimeSeries timeSeries) throws ParametrosException {
        //Se não possui nome
        if (timeSeries.getName().isEmpty()) {
            throw new ParametrosException("A série temporal não possui nome");
        }
        //Obtém o nome da série temporal
        String nome = timeSeries.getName();
        //Varre a série temporal obtida
        for (int i = 0; i < timeSeries.getTickCount(); i++) {
            //Obtém a data da ocorrência
            Date data = timeSeries.getTick(i).getEndTime().toDate();
            //Adiciona o preço de fechamento
            insereValor(nome, data, "ClosePrice", timeSeries.getTick(i).getClosePrice().toDouble());
            //Adiciona o preço de abertura
            insereValor(nome, data, "OpenPrice", timeSeries.getTick(i).getOpenPrice().toDouble());
            //Adiciona o maior preços
            insereValor(nome, data, "HighPrice", timeSeries.getTick(i).getMaxPrice().toDouble());
            //Adiciona o menor preço
            insereValor(nome, data, "LowPrice", timeSeries.getTick(i).getMinPrice().toDouble());
            //Adiciona o volume
            insereValor(nome, data, "Volume", timeSeries.getTick(i).getVolume().toDouble());
        }
        //Adiciona a série temporal ao controle dos parâmetros
        this.timeSeries.add(timeSeries);


    }
    //Insere um indicador técnico do ativo            

    public void insereValor(String nameTimeSeries, Date data, String desIndicador, double valIndicador) {

        //Obtém o array list da data processada
        ArrayList<Parametro> parametro = parametros.get(data);
        //Se ainda não possui parâmetros associados a data
        if (parametro == null) {
            parametro = new ArrayList<>();
        }

        //
        if (Double.isNaN(valIndicador)) {
            valIndicador = 0d;
        }

        //Adiciona o preço de fechamento
        parametro.add(new Parametro(nameTimeSeries + desIndicador, valIndicador));
        insereListaParametro(nameTimeSeries + desIndicador);
        //Adiciona os parâmetros ao HashMap
        parametros.put(data, parametro);

    }

    //Popula variável target
    public void criaTarget(String nomeTimeSeries) throws ParametrosException {

        //TODO: Criar forma de deixar a variável alvo configurável

        //Ordenado as chaves do HashMap
        SortedSet<Date> chaves = new TreeSet<>(parametros.keySet());
        Iterator<Date> iterator = chaves.iterator();

        Date dateProx = null;
        Date dateAtu = null;

        //Varre as chaves
        while (true) {
            //Se não possui um próximo elemento
            if (!iterator.hasNext()) {
                break;
            }

            //Obtém a data do registro atual e do próximo
            dateAtu = dateProx;
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

//Insere parâmetro alvo
    private void insereValorTarget(Date data, double valIndicador) throws ParametrosException {

        if (parTarget.containsKey(data)) {
            throw new ParametrosException("Não foi possível incluir o parâmetro alvo, duplicação de chave");
        }
        parTarget.put(data, valIndicador);

    }

    //Insere o parâmetro na lista de parametros
    private void insereListaParametro(String nomeParametro) {
        //Adiciona nome do parâmetro ao SET
        nomeParametros.add(nomeParametro);
    }

    //Retorna o conteúdo do parâmetro
    public double getValorParametro(ArrayList<Parametro> param, String nomeParametro) throws ParametrosException {

        //Varre os parâmetros existentes
        for (int i = 0; i < param.size(); i++) {
            //Se for parâmetro correto
            if (param.get(i).getDescricao().equals(nomeParametro)) {
                return param.get(i).getValor();
            }
        }
        //Se não encontrou o parâmetro insere excessão
        throw new ParametrosException("Não foi encontrado o nome do parâmetro indicado");
    }
}
