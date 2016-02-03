/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.SVM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.SelectedTag;

/**
 *
 * @author Jean-NoteI5
 */
public class WekaSVM {

    public void execSVM() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/ARFF/BBDC4.SA.arff"));
            Instances train = new Instances(reader);
            train.setClassIndex(train.numAttributes() - 1);

            reader.close();

            LibSVM svm = new LibSVM();



            svm.setSVMType(new SelectedTag(LibSVM.SVMTYPE_EPSILON_SVR, LibSVM.TAGS_SVMTYPE));
            svm.setCacheSize(100);
            svm.setCoef0(0.0);
            svm.setCost(1.0);
            svm.setDebug(false);
            svm.setDegree(3);
            svm.setDoNotReplaceMissingValues(false);
            svm.setEps(0.001);
            svm.setGamma(0.0);
            svm.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_RBF, LibSVM.TAGS_KERNELTYPE));
            svm.setLoss(0.1);
            svm.setNormalize(true);
            svm.setNu(0.5);
            svm.setProbabilityEstimates(false);
            svm.setShrinking(true);
            svm.setWeights(null);


            svm.buildClassifier(train);


            Evaluation evaluation = new Evaluation(train);
            
            System.out.println(evaluation.toSummaryString());


        } catch (FileNotFoundException ex) {
            Logger.getLogger(WekaSVM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WekaSVM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(WekaSVM.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}
