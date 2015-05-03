package net.sourceforge.javydreamercsw.msm.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import net.sourceforge.javydreamercsw.msm.db.manager.MSMException;

/**
 * Replaces the MD5 encryption available in MySQL and makes it universal for all
 * databases.
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
public final class MD5 {

    private static String md5val = "";
    private static MessageDigest algorithm = null;

    private MD5() {
    }

    public static String encrypt(String text) throws MSMException {
        try {
            algorithm = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new MSMException(nsae);
        }
        byte[] defaultBytes = text.getBytes();
        algorithm.reset();
        algorithm.update(defaultBytes);
        byte messageDigest[] = algorithm.digest();
        StringBuilder hexString = new StringBuilder();

        for (int i = 0; i < messageDigest.length; i++) {
            String hex = Integer.toHexString(0xFF & messageDigest[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        md5val = hexString.toString();
        return md5val;
    }
}
