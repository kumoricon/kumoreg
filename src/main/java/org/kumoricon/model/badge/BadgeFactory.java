package org.kumoricon.model.badge;

public class BadgeFactory {
    public static final Badge badgeFactory(String name, String dayText,
                                           double adultCost, double youthCost, double childCost) {
        Badge b = new Badge();
        b.setName(name);
        b.setDayText(dayText);
        b.addAgeRange("Adult", 18, 255, adultCost, "323E99", "Adult");
        b.addAgeRange("Youth", 13, 17, youthCost, "FFFF00", "Youth");
        b.addAgeRange("Child", 6, 12, childCost, "CC202A", "Child");
        b.addAgeRange("5 and under", 0, 5, 0.00, "CC202A", "Child");
        return b;
    }

    public static final Badge emptyBadgeFactory() {
        Badge b = new Badge();
        b.setName("");
        b.setDayText("");
        b.addAgeRange("Adult", 18, 255, 59.99, "323E99", "Adult");
        b.addAgeRange("Youth", 13, 17, 49.99, "FFFF00", "Youth");
        b.addAgeRange("Child", 6, 12, 35.99, "CC202A", "Child");
        b.addAgeRange("5 and under", 0, 5, 0.00, "CC202A", "Child");
        return b;
    }
}
