/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.ARFF.PARAMETROS;

import java.util.ArrayList;
import tccmaven.MISC.Log;

/**
 *
 * @author Jean-NoteI5
 */
public class ValidaParametros {

    private ArrayList<double[]> parametros;
    NomeParametros nomeParametros;

    public ValidaParametros(ArrayList<double[]> manipulaParametros, NomeParametros nomeParametros) {
        this.parametros = manipulaParametros;
        this.nomeParametros = nomeParametros;
    }

    //Executa a validação dos parâmetros inseridos
    public boolean validaDados() throws NomeParametrosException {


        Log.loga("Valida ocorrências não preenchidas", "VALIDAÇÃO");
        //Se possui ocorrência não preenchida
        if (!validaOcoNaoPreenchida()) {
            return false;
        }

        Log.loga("Valida consistência do preço de fechamento, máximo ou mínimo", "VALIDAÇÃO");
        //Se os preços de fechamento, máxima ou mínima estão inválidos
        if (!validaCloseHighLow()) {
            return false;
        }

        //Se chegou ao final deu certo
        return true;
    }

    //Valida se alguma ocorrência não foi preenchida
    private boolean validaOcoNaoPreenchida() {

        for (double[] parametro : parametros) {
            //Varre o array de parâmetros
            for (int j = 0; j < parametro.length; j++) {
                //Varre os valores procurando alguma ocorrência que não foi preenchida
                if (parametro[j] == -9999999999d) {
                    Log.loga("Nem todas as ocorrências do array foram preenchidas");
                    return false;
                }
            }
        }

        return true;
    }

    //Valida se o preços de fechamento, máxima ou mínima estão consistentes
    private boolean validaCloseHighLow() throws NomeParametrosException {

        int closeBra = nomeParametros.getOcoParametro(nomeParametros.montaNomeParametro("Bra", "ClosePrice"));
        int closeEst = nomeParametros.getOcoParametro(nomeParametros.montaNomeParametro("Est", "ClosePrice"));

        int highBra = nomeParametros.getOcoParametro(nomeParametros.montaNomeParametro("Bra", "HighPrice"));
        int highEst = nomeParametros.getOcoParametro(nomeParametros.montaNomeParametro("Est", "HighPrice"));

        int lowBra = nomeParametros.getOcoParametro(nomeParametros.montaNomeParametro("Bra", "LowPrice"));
        int lowEst = nomeParametros.getOcoParametro(nomeParametros.montaNomeParametro("Est", "LowPrice"));

        for (double[] parametro : parametros) {
            //Valor de fechamento maior que o valor máximo
            if (parametro[closeBra] > parametro[highBra]) {
                Log.loga("Valor de fechamento Brasil maior que o valor máximo");
                return false;
            }
            //Valor de fechamento maior que o valor máximo
            if (parametro[closeEst] > parametro[highEst]) {
                Log.loga("Valor de fechamento Estrangeiro maior que o valor máximo");
                return false;
            }
            //Valor de fechamento menor que o valor mínimo
            if (parametro[closeBra] < parametro[lowBra]) {
                Log.loga("Valor de fechamento Brasil menor que o valor mínimo");
                return false;
            }
            //Valor de fechamento menor que o valor mínimo
            if (parametro[closeEst] < parametro[lowEst]) {
                Log.loga("Valor de fechamento Estrangeiro menor que o valor mínimo");
                return false;
            }
            //Valor máximo menor que o valor mínimo
            if (parametro[highBra] < parametro[lowBra]) {
                Log.loga("Valor máximo Brasil menor que o valor mínimo");
                return false;
            }
            //Valor máximo menor que o valor mínimo
            if (parametro[highEst] < parametro[lowEst]) {
                Log.loga("Valor máximo Estrangeiro menor que o valor mínimo");
                return false;
            }
        }

        return true;
    }

}