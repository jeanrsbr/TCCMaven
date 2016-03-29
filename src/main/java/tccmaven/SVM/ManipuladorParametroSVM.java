/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.SVM;

import java.util.ArrayList;
import tccmaven.MISC.LeituraProperties;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.GridSearch;

/**
 *
 * @author Jean-NoteI5
 */
public class ManipuladorParametroSVM {

    private ArrayList<ParametroSVM> parametroSVM;
    private int diasConjuntoTeste;
    private int tamConjuntoIni;
    private int tamConjuntoFin;

    public ManipuladorParametroSVM() {
        this.diasConjuntoTeste = Integer.parseInt(LeituraProperties.getInstance().leituraProperties("svm.diasconjuntoteste"));
        this.parametroSVM = new ArrayList<>();
        this.tamConjuntoIni = Integer.parseInt(LeituraProperties.getInstance().leituraProperties("svm.tamconjuntoini"));
        this.tamConjuntoFin = Integer.parseInt(LeituraProperties.getInstance().leituraProperties("svm.tamconjuntofin"));
    }

    public ArrayList<ParametroSVM> getParametroSVM() {
        return parametroSVM;
    }
    public void populaAnalise() {
        //Começa em dois para descartar o último dia do conjunto onde não sabemos a cotação de amanhã e 
        //não temos como comparar com o valor predito
        for (int i = 2; i < diasConjuntoTeste + 2; i++) {
            populaTamanhoDoConjunto(i);
        }
    }

    private void populaTamanhoDoConjunto(int diaInicial) {
        //Varre os conjuntos possíveis
        for (int i = tamConjuntoIni; i <= tamConjuntoFin; i = i + 10) {
            populaGridSearchEvaluation(diaInicial, i);
        }
    }

    private void populaGridSearchEvaluation(int diaInicial, int tamanhoDoConjunto) {
        populaKernel(diaInicial, tamanhoDoConjunto, GridSearch.EVALUATION_MAE);
        populaKernel(diaInicial, tamanhoDoConjunto, GridSearch.EVALUATION_RAE);
        populaKernel(diaInicial, tamanhoDoConjunto, GridSearch.EVALUATION_RMSE);
        populaKernel(diaInicial, tamanhoDoConjunto, GridSearch.EVALUATION_RRSE);
        populaKernel(diaInicial, tamanhoDoConjunto, GridSearch.EVALUATION_COMBINED);

    }
    
    private void populaKernel(int diaInicial, int tamanhoDoConjunto, int gridSearchEvaluation){
        gravaParametro(diaInicial, tamanhoDoConjunto, gridSearchEvaluation, LibSVM.KERNELTYPE_RBF);
        gravaParametro(diaInicial, tamanhoDoConjunto, gridSearchEvaluation, LibSVM.KERNELTYPE_SIGMOID);
    }

    private void gravaParametro(int diaInicial, int tamanhoDoConjunto, int gridSearchEvaluation, int kernel) {
        parametroSVM.add(new ParametroSVM(diaInicial, tamanhoDoConjunto, gridSearchEvaluation, kernel));
    }
}
