package net.sourceforge.javydreamercsw.msm.db.manager;

import java.util.List;
import static java.util.Locale.getDefault;
import java.util.ResourceBundle;
import static java.util.ResourceBundle.getBundle;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TMException extends Exception {

    private String vm_message = "";
    private static ResourceBundle rb
            = getBundle(
                    "net.sourceforge.javydreamercsw.tm.TMMessages", getDefault());

    public TMException() {
        super();
    }

    public TMException(String message) {
        super(rb.containsKey(message) ? rb.getString(message) : message);
    }

    public TMException(List<String> messages) {
        for (String s : messages) {
            vm_message += s + "\n";
        }
    }

    public TMException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return vm_message.isEmpty() ? super.getLocalizedMessage() : vm_message;
    }
}
