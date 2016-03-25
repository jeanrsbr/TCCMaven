/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tccmaven.MISC;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jean-NoteI5
 */
public class Log {

    private static int log = Integer.parseInt(LeituraProperties.getInstance().leituraProperties("prop.log"));
    private static PrintStream defOut;
    private static PrintStream defErr;

    public static void iniBuf() throws FileNotFoundException {
        defOut = new PrintStream(System.out);
        defErr = new PrintStream(System.err);
    }

    public static void setDef() {
        System.setOut(defOut);
        System.setErr(defErr);
    }

    public static void setDesvio() throws FileNotFoundException {

        System.setOut(new PrintStream("output_weka_out.txt"));
        System.setErr(new PrintStream("output_weka_err.txt"));
    }

    public static void loga(String mensagem) {
        setDef();
        if (log == 1) {
            System.out.println(mensagem);
        }
        try {
            setDesvio();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void loga(String mensagem, String titulo) {
        setDef();
        if (log == 1) {
            System.out.println("[" + String.format("%12s", titulo) + "]:" + mensagem);
        }
        try {
            setDesvio();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
