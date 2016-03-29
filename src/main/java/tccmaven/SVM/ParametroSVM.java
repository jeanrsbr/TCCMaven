/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.SVM;

import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.GridSearch;

/**
 *
 * @author Jean-NoteI5
 */
public class ParametroSVM {
    
    private int diaInicial; // Dia inicial para treino do conjunto (De trás para frente)
    private int tamanhoDoConjunto; // Tamanho do conjunto de treino
    private int gridSearchEvaluation; //Tipo de avaliação do GridSearch
    private int kernel; //Kernel que será utilizado na SVM
    
    private double gamma;
    private double cost;

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public ParametroSVM(int diaInicial, int tamanhoDoConjunto, int gridSearchEvaluation, int kernel) {
        this.diaInicial = diaInicial;
        this.tamanhoDoConjunto = tamanhoDoConjunto;
        this.gridSearchEvaluation = gridSearchEvaluation;
        this.kernel = kernel;
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

    public int getKernel() {
        return kernel;
    }
    
    
    public String getGridSearchEvaluationAlfa() throws ParametroSVMException{

        switch (gridSearchEvaluation) {
            case GridSearch.EVALUATION_COMBINED:
                return "COMBINED";
            case GridSearch.EVALUATION_MAE:
                return "MAE";
            case GridSearch.EVALUATION_RAE:
                return "RAE";
            case GridSearch.EVALUATION_RMSE:
                return "RMSE";
            case GridSearch.EVALUATION_RRSE:
                return "RRSE";
            default:
                throw new ParametroSVMException("GridSearch com opção não reconhecida");
        }

    }
    
    public String getKernelAlfa() throws ParametroSVMException {
        
        switch (kernel) {
            case LibSVM.KERNELTYPE_RBF:
                return "RBF";
            case LibSVM.KERNELTYPE_POLYNOMIAL:
                return "Polynomial";
            case LibSVM.KERNELTYPE_SIGMOID:
                return "Sigmoid";
            default:
                throw new ParametroSVMException("Kernel com opção não reconhecida");
        }
        
    }
    
}
