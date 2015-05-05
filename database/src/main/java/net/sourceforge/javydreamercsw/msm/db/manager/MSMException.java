package net.sourceforge.javydreamercsw.msm.db.manager;

import java.util.List;
import static java.util.Locale.getDefault;
import java.util.ResourceBundle;
import static java.util.ResourceBundle.getBundle;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class MSMException extends Exception {

    private String msm_message = "";
    private static ResourceBundle rb
            = getBundle(
                    "net.sourceforge.javydreamercsw.msm.MSMMessages", getDefault());

    public MSMException() {
        super();
    }

    public MSMException(String message) {
        super(rb.containsKey(message) ? rb.getString(message) : message);
    }

    public MSMException(List<String> messages) {
        for (String s : messages) {
            msm_message += s + "\n";
        }
    }

    public MSMException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return msm_message.isEmpty() ? super.getLocalizedMessage() : msm_message;
    }
}
