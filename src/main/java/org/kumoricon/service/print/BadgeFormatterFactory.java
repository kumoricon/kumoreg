package org.kumoricon.service.print;

import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.service.print.formatter.BadgePrintFormatter;
import org.kumoricon.service.print.formatter.FullBadgePrintFormatter;
import org.kumoricon.service.print.formatter.LiteBadgePrintFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Returns an appropriate BadgeFormatter object based on settings in the configuration file. This is used to
 * easily switch between badge layouts at different events.
 */
@Component
public class BadgeFormatterFactory {
    @Value("${kumoreg.printing.badgeFormat}")
    private String currentFormatter;

    private static final Logger log = LoggerFactory.getLogger(BadgeFormatterFactory.class);

    /**
     * Returns configured BadgeFormatter
     * @param attendees List of Attendees to generate badges for
     * @param xOffset Horizontal print offset in points (1/72 inch)
     * @param yOffset Vertical print offset in points (1/72 inch)
     * @param ageAsOfDate Date to use when calculating adult/youth/child. *Ignored*
     * @return BadgePrintFormatter
     */
    public BadgePrintFormatter getCurrentBadgeFormatter(List<Attendee> attendees, Integer xOffset, Integer yOffset, LocalDate ageAsOfDate) {
        if ("lite".equals(currentFormatter)) {
            return new LiteBadgePrintFormatter(attendees, xOffset, yOffset);
        } else if("full".equals(currentFormatter)) {
            return new FullBadgePrintFormatter(attendees, xOffset, yOffset);
        } else {
            log.warn("Tried to find badge formatter {}, using FullBadgePrintFormatter instead. " +
                    "kumoreg.printing.badgeFormat not configured properly?", currentFormatter);
            return new FullBadgePrintFormatter(attendees, xOffset, yOffset);
        }
    }
}
