package org.kumoricon.site.fieldconverter;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DateToLocalDateConverterTest {
    private final DateToLocalDateConverter converter = new DateToLocalDateConverter();
    private final LocalDate localDate = LocalDate.of(2016, 1, 1);
    private final Date date = new Date(1451635200000L);             // 1/1/2016 00:00:00 PST

    @Test
    public void convertToModel() {
        assertEquals(localDate, converter.convertToModel(date, LocalDate.class, Locale.ENGLISH));
    }

    @Test
    public void convertToModelNull() {
        assertNull(converter.convertToModel(null, LocalDate.class, Locale.ENGLISH));
    }

    @Test
    public void convertToPresentation() {
        assertEquals(date, converter.convertToPresentation(localDate, Date.class, Locale.ENGLISH));
    }

    @Test
    public void convertToPresentationNull() {
        assertNull(converter.convertToPresentation(null, Date.class, Locale.ENGLISH));
    }

    @Test
    public void getModelType() {
        assertEquals(LocalDate.class, converter.getModelType());
    }

    @Test
    public void getPresentationType() {
        assertEquals(Date.class, converter.getPresentationType());
    }

}