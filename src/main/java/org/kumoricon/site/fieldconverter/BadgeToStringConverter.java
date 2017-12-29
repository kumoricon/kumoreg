package org.kumoricon.site.fieldconverter;

import com.vaadin.v7.data.util.converter.Converter;
import org.kumoricon.model.badge.Badge;

import java.util.Locale;

public class BadgeToStringConverter implements Converter<String, Badge> {
    @Override
    public Badge convertToModel(String value, Class<? extends Badge> targetType, Locale locale) throws ConversionException {
        throw new ConversionException("String to Badge conversion not implemented");
    }

    @Override
    public String convertToPresentation(Badge badge, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if (badge == null) { return null; }
        if (badge.getName() == null || badge.getName().isEmpty()) {
            return String.format("Badge ID %s", badge.getId());
        } else {
            return badge.getName();
        }
    }

    @Override
    public Class<Badge> getModelType() {
        return Badge.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
