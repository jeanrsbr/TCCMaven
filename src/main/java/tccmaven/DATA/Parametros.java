/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.DATA;

import eu.verdelhan.ta4j.TimeSeries;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

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
    

    //Monta parâmetros para utilizar na linha do arquivo
    public void montaParametros() {
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
            insereIndicadorTecnico(nome, data, "ClosePrice", timeSeries.getTick(i).getClosePrice().toDouble());
            //Adiciona o preço de abertura
            insereIndicadorTecnico(nome, data, "OpenPrice", timeSeries.getTick(i).getOpenPrice().toDouble());
            //Adiciona o maior preços
            insereIndicadorTecnico(nome, data, "HighPrice", timeSeries.getTick(i).getMaxPrice().toDouble());
            //Adiciona o menor preço
            insereIndicadorTecnico(nome, data, "LowPrice", timeSeries.getTick(i).getMinPrice().toDouble());
            //Adiciona o volume
            insereIndicadorTecnico(nome, data, "Volume", timeSeries.getTick(i).getVolume().toDouble());
        }
        //Adiciona a série temporal ao controle dos parâmetros
        this.timeSeries.add(timeSeries);


    }
    //Insere um indicador técnico do ativo            

    public void insereIndicadorTecnico(String nameTimeSeries, Date data, String desIndicador, double valIndicador) {

        //Obtém o array list da data processada
        ArrayList<Parametro> parametro = parametros.get(data);
        //Se ainda não possui parâmetros associados a data
        if (parametro == null) {
            parametro = new ArrayList<>();
        }
        
        //
        if (Double.isNaN(valIndicador)){
            valIndicador = 0d;
        }
            
        
        //Adiciona o preço de fechamento
        parametro.add(new Parametro(nameTimeSeries + desIndicador, valIndicador));
        insereListaParametro(nameTimeSeries + desIndicador);
        //Adiciona os parâmetros ao HashMap
        parametros.put(data, parametro);


    }

    //Insere o parâmetro na lista de parametros
    private void insereListaParametro(String nomeParametro) {
        //Adiciona nome do parâmetro ao SET
        nomeParametros.add(nomeParametro);
    }
}
