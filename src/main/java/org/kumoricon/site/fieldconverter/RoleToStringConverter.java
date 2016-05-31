package org.kumoricon.site.fieldconverter;

import com.vaadin.data.util.converter.Converter;
import org.kumoricon.model.role.Role;

import java.util.Locale;

public class RoleToStringConverter implements Converter<String, Role> {
    @Override
    public Role convertToModel(String value, Class<? extends Role> targetType, Locale locale) throws ConversionException {
        throw new ConversionException("String to Role conversion not implemented");
    }

    @Override
    public String convertToPresentation(Role role, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if (role == null) { return null; }
        if (role.getName() == null || role.getName().isEmpty()) {
            return String.format("Role ID %s", role.getId());
        } else {
            return role.getName();
        }
    }

    @Override
    public Class<Role> getModelType() {
        return Role.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
