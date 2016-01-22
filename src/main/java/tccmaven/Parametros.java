/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven;

import eu.verdelhan.ta4j.TimeSeries;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author Jean-NoteI5
 */
public class Parametros {

    //Nome do ativo
    private String ativo;
    //Séries temporais do ativo
    private ArrayList<TimeSeries> timeSeries;
    //Parâmetros montados a partir das séries temporais
    private Map<Date, ArrayList<Parametro>> parametros;

    //Ativo ao qual se referem os parâmetros
    public Parametros(String ativo) {
        this.ativo = ativo;
    }

    public ArrayList<TimeSeries> getTimeSeries() {
        return timeSeries;
    }    
    
    
    //Monta parâmetros para utilizar na linha do arquivo
    public void montaParametros(){
        
    }
    //Insere série temporal do ativo
    public void insereSerieTemporal(TimeSeries timeSeries){
        this.timeSeries.add(timeSeries);
    }
    //Insere um indicador técnico do ativo            
    public void insereIndicadorTecnico(Date data, String desIndicador, double valIndicador){
        //Declara um arrayList de parametro
        ArrayList<Parametro> aux;
        
        //Obtém o arrayList
        aux = parametros.get(data);
        //Se não possui um array de parâmetro
        if (aux == null){
            aux = new ArrayList<>();
        }
        
        
        
        //parametros.put(data, null)
        
    }
    
}
