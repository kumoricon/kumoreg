package org.kumoricon.model.badge;

public class BadgeFactory {
    /**
     * Create a Badge object with age ranges for adult/youth/child/5 and under.
     * @param name Badge Name
     * @param dayText Text to print in color stripe (Weekend/Friday/Exhibitor/etc)
     * @param adultCost Cost of adult badge
     * @param youthCost Cost of youth badge
     * @param childCost Cost of child badge
     * @return Badge
     */
    public static Badge createBadge(String name, String dayText,
                                    double adultCost, double youthCost, double childCost) {
        Badge b = new Badge();
        b.setName(name);
        b.setDayText(dayText);
        b.addAgeRange("Adult", 18, 255, adultCost, "#323E99", "Adult");
        b.addAgeRange("Youth", 13, 17, youthCost, "#FFFF00", "Youth");
        b.addAgeRange("Child", 6, 12, childCost, "#CC202A", "Child");
        b.addAgeRange("5 and under", 0, 5, 0.00, "#CC202A", "Child");
        return b;
    }

    /**
     * Create a Badge object with age ranges for adult/youth/child/5 and under. Overrides the color of the
     * color stripe on ALL the age ranges and sets them to the given color
     * @param name Badge Name
     * @param dayText Text to print in color stripe (Weekend/Friday/Exhibitor/etc)
     * @param adultCost Cost of adult badge
     * @param youthCost Cost of youth badge
     * @param childCost Cost of child badge
     * @param adultStripeColor Hex color code for adult age ranges, with leading # (ex: #F03EA3)
     * @return Badge
     */
    public static Badge createBadge(String name, String dayText,
                                    double adultCost, double youthCost, double childCost, String adultStripeColor) {
        Badge b = new Badge();
        b.setName(name);
        b.setDayText(dayText);
        b.addAgeRange("Adult", 18, 255, adultCost, adultStripeColor, "Adult");
        b.addAgeRange("Youth", 13, 17, youthCost, "#FFFF00", "Youth");
        b.addAgeRange("Child", 6, 12, childCost, "#CC202A", "Child");
        b.addAgeRange("5 and under", 0, 5, 0.00, "#CC202A", "Child");
        return b;
    }


    /**
     * Creates a Badge object with default age ranges
     * @return Badge
     */
    public static Badge createEmptyBadge() {
        Badge b = new Badge();
        b.setName("");
        b.setDayText("");
        b.addAgeRange("Adult", 18, 255, 0.00, "#323E99", "Adult");
        b.addAgeRange("Youth", 13, 17, 0.00, "#FFFF00", "Youth");
        b.addAgeRange("Child", 6, 12, 0.00, "#CC202A", "Child");
        b.addAgeRange("5 and under", 0, 5, 0.00, "#CC202A", "Child");
        return b;
    }
}
