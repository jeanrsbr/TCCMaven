/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.SVM;

/**
 *
 * @author Jean-NoteI5
 */
public class ParametrosSVM {
    
    private int diaInicial; // Dia inicial para treino do conjunto (De trás para frente)
    private int tamanhoDoConjunto; // Tamanho do conjunto de treino
    private int gridSearchEvaluation; //Tipo de avaliação do GridSearch

    public ParametrosSVM(int diaInicial, int tamanhoDoConjunto, int gridSearchEvaluation) {
        this.diaInicial = diaInicial;
        this.tamanhoDoConjunto = tamanhoDoConjunto;
        this.gridSearchEvaluation = gridSearchEvaluation;
    }    

    public int getDiaInicial() {
        return diaInicial;
    }

    public int getTamanhoDoConjunto() {
        return tamanhoDoConjunto;
    }

    public int getGridSearchEvaluation() {
        return gridSearchEvaluation;
    }
    
    
    
}
