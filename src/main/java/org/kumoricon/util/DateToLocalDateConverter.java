package org.kumoricon.util;

import com.vaadin.data.util.converter.Converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class DateToLocalDateConverter implements Converter<Date, LocalDate> {

    @Override
    public LocalDate convertToModel(Date value, Class<? extends LocalDate> targetType, Locale locale) throws Converter.ConversionException {
        if (value == null) {
            return null;
        }
        return LocalDate.from(Instant.ofEpochMilli(value.getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
    }

    @Override
    public Date convertToPresentation(LocalDate value, Class<? extends Date> targetType, Locale locale)
            throws Converter.ConversionException {
        if (value == null) {
            return null;
        }
        return Date.from(value.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

    }

    @Override
    public Class<LocalDate> getModelType() {
        return LocalDate.class;
    }

    @Override
    public Class<Date> getPresentationType() {
        return Date.class;
    }
}
