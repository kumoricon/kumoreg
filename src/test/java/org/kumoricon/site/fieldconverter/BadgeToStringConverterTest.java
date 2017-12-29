package org.kumoricon.site.fieldconverter;

import com.vaadin.v7.data.util.converter.Converter;
import org.junit.Test;
import org.kumoricon.model.badge.Badge;

import java.util.Locale;

import static junit.framework.TestCase.assertEquals;

public class BadgeToStringConverterTest {
    private BadgeToStringConverter converter = new BadgeToStringConverter();
    private final Badge badgeWithNullName;
    private final Badge badge;

    public BadgeToStringConverterTest() {
        badgeWithNullName = new Badge();
        badgeWithNullName.setId(1);

        badge = new Badge();
        badge.setId(2);
        badge.setName("Test Badge");
    }

    @Test(expected=Converter.ConversionException.class)
    public void convertToModel() throws Exception {
        converter.convertToModel("Test", Badge.class, Locale.ENGLISH);
    }

    @Test
    public void convertToPresentation() throws Exception {
        assertEquals("Badge with name", "Test Badge", converter.convertToPresentation(badge, String.class, Locale.ENGLISH));
    }

    @Test
    public void convertToPresentationNullName() throws Exception {
        assertEquals("Badge with null name", "Badge ID 1", converter.convertToPresentation(badgeWithNullName, String.class, Locale.ENGLISH));
    }


    @Test
    public void getModelType() throws Exception {
        assertEquals(Badge.class, converter.getModelType());
    }

    @Test
    public void getPresentationType() throws Exception {
        assertEquals(String.class, converter.getPresentationType());
    }

}