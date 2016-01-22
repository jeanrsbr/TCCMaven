/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven;

/**
 *
 * @author Jean-NoteI5
 */
public class Parametro {
    
    private String Descrição;
    private double valor;

    public Parametro(String Descrição, double valor) {
        this.Descrição = Descrição;
        this.valor = valor;
    }

    public String getDescrição() {
        return Descrição;
    }

    public void setDescrição(String Descrição) {
        this.Descrição = Descrição;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
