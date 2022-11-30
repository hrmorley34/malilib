package fi.dy.masa.malilib.util;

import java.util.Comparator;
import net.minecraft.state.property.BooleanProperty;
// import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;

public class PropertyCompare implements Comparator<Property<?>> {
    /*
     * Get a number representing the order of the name;
     * 0 if it is not special
     */
    public static int getNameComparable(String name) {
        switch (name) {
            case "north": return -106;
            case "east": return -105;
            case "south": return -104;
            case "west": return -103;
            case "up": return -102;
            case "down": return -101;
        }
        return 0;
    }

    @Override
    public int compare(Property<?> p1, Property<?> p2) {
        String p1namel = p1.getName().toLowerCase();
        String p2namel = p2.getName().toLowerCase();
        Integer p1int = getNameComparable(p1namel);
        Integer p2int = getNameComparable(p2namel);
        if (p1int != p2int) return p1int.compareTo(p2int);

        Integer namecompare = p1namel.compareTo(p2namel); // simple alphabetical sort, for later use

        // p1int == p2int from if above
        if (p1int != 0) return namecompare; // both are special

        // otherwise, neither are special, so sort by type
        if (p1 instanceof BooleanProperty) {
            if (p2 instanceof BooleanProperty) return namecompare;
            else return -1;
        }
        else if (p2 instanceof BooleanProperty) return 1;
        else if (p1 instanceof IntProperty) {
            if (p2 instanceof IntProperty) return namecompare;
            else return -1;
        }
        else if (p2 instanceof IntProperty) return 1;
        /*else if (p1 instanceof DirectionProperty) {
            if (p2 instanceof DirectionProperty) return namecompare;
            else return -1;
        }
        else if (p2 instanceof DirectionProperty) return 1;*/
        else return namecompare;
    }
}
