/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.DATA;

/**
 *
 * @author Jean-NoteI5
 */
public class Parametro {

    private String descricao;
    private double valor;

    public Parametro(String descricao, double valor) {
        this.descricao = descricao;
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

}
