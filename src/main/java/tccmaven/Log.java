/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven;

/**
 *
 * @author Jean-NoteI5
 */
public class Log {

    private static int log = Integer.parseInt(LeituraProperties.getInstance().leituraProperties("prop.log"));

    public static void loga(String mensagem) {
        if (log == 1) {
            System.out.println(mensagem);
        }
    }

}
