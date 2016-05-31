package org.kumoricon.site.fieldconverter;

import com.vaadin.data.util.converter.Converter;
import org.kumoricon.model.user.User;

import java.util.Locale;

public class UserToStringConverter implements Converter<String, User> {
    @Override
    public User convertToModel(String value, Class<? extends User> targetType, Locale locale) throws ConversionException {
        throw new ConversionException("Not implemented");
    }

    @Override
    public String convertToPresentation(User user, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if (user == null) { return null; }
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return user.getId().toString();
        } else {
            return user.getUsername();
        }
    }

    @Override
    public Class<User> getModelType() {
        return User.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
