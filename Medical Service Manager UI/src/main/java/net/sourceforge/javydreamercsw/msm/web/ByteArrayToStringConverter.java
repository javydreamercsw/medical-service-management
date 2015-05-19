package net.sourceforge.javydreamercsw.msm.web;

import com.vaadin.data.util.converter.Converter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * COnverts String to byte[] and viceversa.
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ByteArrayToStringConverter implements Converter<String, byte[]> {

    private static final Logger LOG
            = Logger.getLogger(ByteArrayToStringConverter.class.getName());

    @Override
    public byte[] convertToModel(String value, Class<? extends byte[]> targetType, Locale locale) throws ConversionException {
        try {
            return value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return value.getBytes();
        }
    }

    @Override
    public String convertToPresentation(byte[] value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        return value == null ? "null" : new String(value);
    }

    @Override
    public Class<byte[]> getModelType() {
        return byte[].class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
