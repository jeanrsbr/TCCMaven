/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.ARFF;

import java.text.NumberFormat;

/**
 *
 * @author Jean-NoteI5
 */
public class NomeParametros {

    private String[] nomeParametros;
    private String[] nomeParametrosOriginal;

    public NomeParametros(String[] nomeParametros) {
        nomeParametrosOriginal = nomeParametros.clone();
        this.nomeParametros = nomeParametros.clone();
        //Ajusta o último parâmetro para possuir o nome fixo de TARGET
        this.nomeParametros[nomeParametros.length - 1] = "Target";
    }

    public String[] getNomeParametros() {
        return nomeParametros;
    }

    //Retorna a ocorrência do parâmetro Target
    public int getOcoTarget() throws NomeParametrosException {
        return getOcoParametro(nomeParametrosOriginal[nomeParametros.length - 1]);
    }

    //Retorna o número de parâmetros
    public int getNumPar() {
        //Retorna a quantidade de parâmetros adicionados
        return nomeParametros.length;
    }

    //Retorna a ocorrência que o parâmetro deve ser inserido
    public int getOcoParametro(String nomeParametro) throws NomeParametrosException {

        for (int i = 0; i < nomeParametros.length; i++) {

            if (nomeParametros[i].equals(nomeParametro)) {
                return i;
            }
        }
        throw new NomeParametrosException("Não foi encontrada a ocorrência do parâmetro indicado");
    }

    //Monta a literal de nome do parâmetro
    public String montaNomeParametro(String pais, String parametro, int periodo) {
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumIntegerDigits(2);
        f.setMinimumIntegerDigits(2);
        return pais + "_" + parametro + "_" + f.format(periodo);
    }

    //Monta a literal de nome do parâmetro
    public String montaNomeParametro(String pais, String parametro) {
        return pais + "_" + parametro;
    }
}
