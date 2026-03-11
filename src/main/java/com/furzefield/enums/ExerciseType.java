package com.furzefield.enums;

public enum ExerciseType {
    YOGA        ("Yoga",        12.00),
    ZUMBA       ("Zumba",       10.00),
    AQUACISE    ("Aquacise",     8.00),
    BOX_FIT     ("Box Fit",     15.00),
    BODY_BLITZ  ("Body Blitz",  11.00);

    private final String displayName;
    private final double price;

    ExerciseType(String displayName, double price) {
        this.displayName = displayName;
        this.price       = price;
    }

    public String getDisplayName() { return displayName; }
    public double getPrice()       { return price; }

    public static ExerciseType fromDisplayName(String name) {
        for (ExerciseType type : values()) {
            if (type.displayName.equalsIgnoreCase(name.trim())) return type;
        }
        throw new IllegalArgumentException("Unknown exercise type: " + name);
    }

    @Override
    public String toString() { return displayName; }
}