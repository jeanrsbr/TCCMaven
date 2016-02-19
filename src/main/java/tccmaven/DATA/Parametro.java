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
    private boolean alvo;

    public Parametro(String descricao, double valor, boolean alvo) {
        this.descricao = descricao;
        this.valor = valor;
        this.alvo = alvo;
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

    public boolean isAlvo() {
        return alvo;
    }

    public void setAlvo(boolean alvo) {
        this.alvo = alvo;
    }
}
