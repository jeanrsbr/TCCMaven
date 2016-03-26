/*
 * TCCMaven
 * CopyRight Rech Informática Ltda. Todos os direitos reservados.
 */
package tccmaven.SVM;

import java.util.TreeMap;
import tccmaven.MISC.Log;

/**
 * Descrição da classe.
 */
public class ManipuladorResultadosSVM {

    private static ManipuladorResultadosSVM instance;
    private TreeMap<Double, ResultadoSVM> resultados;

    private ManipuladorResultadosSVM() {
        resultados = new TreeMap<>();
    }

    //Insere o resultado no mapa
    public void putResultado(double iD, ResultadoSVM resultadoSVM) {
        resultados.put(iD, resultadoSVM);        
    }
    
    //Pega o resultado de acordo com a chave
    public ResultadoSVM getResultado(double iD) {
        return resultados.get(iD);
    }
    

    //Retorna a quantidade de ocorrências de resultado
    public int getOco() {
        return resultados.size();
    }

    public static synchronized ManipuladorResultadosSVM getInstance() {
        if (instance == null) {
            instance = new ManipuladorResultadosSVM();
        }
        return instance;
    }
}
