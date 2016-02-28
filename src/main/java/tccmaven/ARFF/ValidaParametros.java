/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.ARFF;

import java.util.ArrayList;
import tccmaven.MISC.Log;

/**
 *
 * @author Jean-NoteI5
 */
public class ValidaParametros {
    private ArrayList<double[]> parametros;

    public ValidaParametros(ArrayList<double[]> manipulaParametros) {
        this.parametros = manipulaParametros;
    }   
    
    //Executa a validação dos parâmetros inseridos
    public boolean validaDados(){
        
        //Varre os parâmetros
        for (int i = 0; i < parametros.size(); i++) {
            //Varre o array de parâmetros
            for (int j = 0; j < parametros.get(i).length; j++) {
                //Varre os valores procurando alguma ocorrência que não foi preenchida
                if (parametros.get(i)[j] == -9999999999d){
                    Log.loga("Nem todas as ocorrências do array foram preenchidas");
                    return false;
                }                
            }
        }
        
        //Se chegou ao final deu certo
        return true;
    }
    
}
