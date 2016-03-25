/*
 * TCCMaven
 * CopyRight Rech Informática Ltda. Todos os direitos reservados.
 */
package tccmaven.SVM;

import com.sun.org.apache.bcel.internal.generic.LoadClass;
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
    synchronized public void insereResultado(double iD, ResultadoSVM resultadoSVM) {
        resultados.put(iD, resultadoSVM);
        Log.loga("Inseriu registro no resultado", "RESULTADO");
    }

    //Retorna a quantidade de ocorrências de resultado
    public int getOco() {
        return resultados.size();
    }

    public static ManipuladorResultadosSVM getInstance() {
        if (instance == null) {
            instance = new ManipuladorResultadosSVM();
        }
        return instance;
    }
}
