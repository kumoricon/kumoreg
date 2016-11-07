package org.kumoricon.site.report.export.data;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class BaseTSVExportTest {
    @Test
    public void formatBoolean() throws Exception {
        assertEquals(Boolean.TRUE.toString() + "\t", BaseTSVExport.format(Boolean.TRUE));
        assertEquals(Boolean.FALSE.toString() + "\t", BaseTSVExport.format(Boolean.FALSE));
        assertEquals("\t", BaseTSVExport.format((Boolean)null));
    }

    @Test
    public void formatString() throws Exception {
        assertEquals("TEST\t", BaseTSVExport.format("TEST"));
        assertEquals("test\t", BaseTSVExport.format("test"));
        assertEquals("\t", BaseTSVExport.format((String)null));
    }

    @Test
    public void formatStringTrimsInput() throws Exception {
        assertEquals("test\t", BaseTSVExport.format(" test "));
    }

    @Test
    public void formatStringRemovesTabsInInput() throws Exception {
        assertEquals("test\t", BaseTSVExport.format("\tte\tst\t"));
    }

    @Test
    public void formatLocalDate() throws Exception {
        LocalDate date = LocalDate.now();
        assertEquals(date.toString() + "\t", BaseTSVExport.format(date));
        assertEquals("\t", BaseTSVExport.format((LocalDate)null));
    }

    @Test
    public void formatDate() throws Exception {
        Date date = new Date();
        assertEquals(date.toString() + "\t", BaseTSVExport.format(date));
        assertEquals("\t", BaseTSVExport.format((Date)null));
    }

    @Test
    public void formatBigDecimal() throws Exception {
        assertEquals("10\t", BaseTSVExport.format(BigDecimal.TEN));
        assertEquals("\t", BaseTSVExport.format((BigDecimal) null));
    }

}