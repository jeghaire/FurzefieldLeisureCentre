package com.furzefield;

/**
 *
 * @author Mavi
 */
public class LessonType {
    private final String name;
    private final double price;

    public LessonType(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return name + " (£" + price + ")";
    }
}