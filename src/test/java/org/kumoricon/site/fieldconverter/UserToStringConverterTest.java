package org.kumoricon.site.fieldconverter;

import com.vaadin.v7.data.util.converter.Converter;
import junit.framework.TestCase;
import org.junit.Test;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserFactory;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class UserToStringConverterTest {
    private final UserToStringConverter converter = new UserToStringConverter();
    private final User user = UserFactory.newUser("Test", "User");
    private final User userWithNullName = UserFactory.newUser();

    public UserToStringConverterTest() {
        userWithNullName.setId(1);
        user.setId(2);
    }

    @Test(expected=Converter.ConversionException.class)
    public void convertToModel() throws Exception {
        converter.convertToModel("Test", User.class, Locale.ENGLISH);
    }

    @Test
    public void convertToPresentation() throws Exception {
        TestCase.assertEquals("User with name", "tuser",
                converter.convertToPresentation(user, String.class, Locale.ENGLISH));
    }

    @Test
    public void convertToPresentationNullName() throws Exception {
        TestCase.assertEquals("User with name", "1",
                converter.convertToPresentation(userWithNullName, String.class, Locale.ENGLISH));
    }

    @Test
    public void getModelType() throws Exception {
        assertEquals(User.class, converter.getModelType());
    }

    @Test
    public void getPresentationType() throws Exception {
        assertEquals(String.class, converter.getPresentationType());
    }

}