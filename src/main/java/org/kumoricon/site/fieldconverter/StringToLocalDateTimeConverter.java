package org.kumoricon.site.fieldconverter;

import com.vaadin.data.util.converter.Converter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");


    @Override
    public LocalDateTime convertToModel(String value, Class<? extends LocalDateTime> targetType, Locale locale) throws ConversionException {
        throw new ConversionException("String to LocalDateTime conversion not implemented");
    }

    @Override
    public String convertToPresentation(LocalDateTime value, Class<? extends String> targetType, Locale locale)
            throws ConversionException {
        if (value == null) {
            return null;
        }
        return value.format(DATE_TIME_FORMATTER);
    }

    @Override
    public Class<LocalDateTime> getModelType() {
        return LocalDateTime.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
