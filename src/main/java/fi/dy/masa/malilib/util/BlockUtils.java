package fi.dy.masa.malilib.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import fi.dy.masa.malilib.gui.GuiBase;

public class BlockUtils
{
    /**
     * Returns the first PropertyDirection property from the provided state, if any.
     * @param state
     * @return the first PropertyDirection, or null if there are no such properties
     */
    @Nullable
    public static DirectionProperty getFirstDirectionProperty(BlockState state)
    {
        for (Property<?> prop : state.getProperties())
        {
            if (prop instanceof DirectionProperty)
            {
                return (DirectionProperty) prop;
            }
        }

        return null;
    }

    /**
     * Returns the EnumFacing value of the first found PropertyDirection
     * type blockstate property in the given state, if any.
     * If there are no PropertyDirection properties, then null is returned.
     * @param state
     * @return
     */
    @Nullable
    public static Direction getFirstPropertyFacingValue(BlockState state)
    {
        DirectionProperty prop = getFirstDirectionProperty(state);
        return prop != null ? state.get(prop) : null;
    }

    public static List<String> getFormattedBlockStateProperties(BlockState state)
    {
        return getFormattedBlockStateProperties(state, ": ");
    }

    public static List<String> getFormattedBlockStateProperties(BlockState state, String separator)
    {
        List<Property<?>> properties = new ArrayList<Property<?>>(state.getProperties());

        if (properties.size() > 0)
        {
            List<String> lines = new ArrayList<>();

            Collections.sort(properties, new PropertyCompare());

            for (Property<?> prop : properties)
            {
                Comparable<?> val = state.get(prop);

                // booleans representing if this is the first or final possible value of the property (eg. first/last growth stage)
                Boolean isFirstElement = val.equals(prop.getValues().toArray()[0]);
                Boolean isLastElement = val.equals(prop.getValues().toArray()[prop.getValues().size() - 1]);

                if (prop instanceof BooleanProperty)
                {
                    String pre = val.equals(Boolean.TRUE) ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
                    lines.add(prop.getName() + separator + pre + val.toString());
                }
                else if (prop instanceof DirectionProperty)
                {
                    lines.add(prop.getName() + separator + GuiBase.TXT_GOLD + val.toString());
                }
                else if (prop instanceof IntProperty)
                {
                    String colour = GuiBase.TXT_AQUA;
                    switch (prop.getName().toLowerCase()) {
                        case "honey_level": // beehive/nest: {0..5}
                        case "stage": // saplings: {0, 1}
                        case "age":
                            /* beetroot: {0..3}
                               cactus: {0..15}
                               carrots: {0..7}
                               chorus: flower {0..5}
                               cocoa: {0..2}
                               fire?: {0..15}
                               frosted ice?: {0..3}
                               kelp: {0..25}
                               melon/pumpkin stem (growing): {0..7}
                               nether wart: {0..3}
                               potatoes: {0..7}
                               sugar cane: {0..15}
                               berry bush: {0..3}
                               wheat: {0..7} */
                            colour = isLastElement ? GuiBase.TXT_GOLD : GuiBase.TXT_YELLOW; break;
                        case "moisture": // farmland: {0..7}
                            colour = isLastElement ? GuiBase.TXT_BLUE : GuiBase.TXT_AQUA; break;
                        case "delay": // repeater: {1..4}
                            colour = GuiBase.TXT_YELLOW; break; // why not?
                        case "charges": // respawn anchor: {0..4}
                            colour = isLastElement ? GuiBase.TXT_GOLD : (isFirstElement ? GuiBase.TXT_LIGHT_PURPLE : GuiBase.TXT_YELLOW); break;
                        case "level":
                            // composter: {0..8}
                            // lava, water: ignore
                            break;
                        case "rotation": // mob heads (floor), signs (floor): {0..15}
                            colour = GuiBase.TXT_GOLD; break;
                        case "note": // note blocks: {0..24}
                            colour = GuiBase.TXT_LIGHT_PURPLE;
                            /* bukkit lied to me
                            switch (val.toString()) {
                                // nabbed from https://minecraft.fandom.com/wiki/Talk:Note_Block#Hex_Colors_for_the_Notes_were_Missing.2C_so_I_offer_you_the_colors
                                // probably not the best way to do this
                                case "0": colour = "§#67D605"; break;
                                case "1": colour = "§#8BD602"; break;
                                case "2": colour = "§#D6D600"; break;
                                case "3": colour = "§#D68B1B"; break;
                                case "4": colour = "§#D64F26"; break;
                                case "5": colour = "§#D72A29"; break;
                                case "6": colour = "§#CE2827"; break;
                                case "7": colour = "§#D72A29"; break;
                                case "8": colour = "§#D72A4F"; break;
                                case "9": colour = "§#D72A86"; break;
                                case "10": colour = "§#D72AD6"; break;
                                case "11": colour = "§#8C16D7"; break;
                                case "12": colour = "§#5006D6"; break;
                                case "13": colour = "§#2C00FE"; break;
                                case "14": colour = "§#1E01F6"; break;
                                case "15": colour = "§#252DFE"; break;
                                case "16": colour = "§#3660FF"; break;
                                case "17": colour = "§#4C95EB"; break;
                                case "18": colour = "§#6BD7D6"; break;
                                case "19": colour = "§#68D68C"; break;
                                case "20": colour = "§#67D651"; break;
                                case "21": colour = "§#67D626"; break;
                                case "22": colour = "§#62CD0D"; break;
                                case "23": colour = "§#72EB10"; break;
                                case "24": colour = "§#67D605"; break;
                            }*/
                            break;
                    }
                    lines.add(prop.getName() + separator + colour + val.toString());
                }
                else
                {
                    String colour = "";
                    switch (prop.getName().toLowerCase()) {
                        case "leaves": // bamboo: {none, small, large}
                        case "east": // redstone dust: {none, side, up}; walls: {none, low, tall}
                        case "north": // ^
                        case "south": // ^
                        case "west": // ^
                            // pseudo-boolean: none -> red, rest are green
                            colour = val.toString().equalsIgnoreCase("none") ? GuiBase.TXT_RED : GuiBase.TXT_GREEN; break;
                        case "part": // bed: {foot, head}
                        case "attachment": // bell: {ceiling, double_wall, floor, single_wall}
                        case "axis": // bone, chain, hay bale, logs, pillars: {x, y, z}; nether portal: {x, z}
                        case "face": // button, grindstone: {wall, ceiling, floor}
                        case "type": // chest: {left, right, single}; slabs: {bottom, top, double}; stairs: {bottom, top}; *some pistons: {normal, sticky}, oops
                        case "half": // doors, flowers, (sea)grass, trapdoors: {lower, upper}
                        case "hinge": // doors: {left, right}
                        case "orientation": // jigsaw: no one cares
                        case "shape": // rails: {east_west, north_east, ..., ascending_east, ...}; stairs: {inner_left, ..., outer_right, straight}
                            colour = GuiBase.TXT_GOLD; break;
                        case "instrument":
                            // note block: {harp, banjo, ...}
                            colour = GuiBase.TXT_BLUE; break;
                        case "mode":
                            // comparator: {compare, subtract}; structure block: {corner, data, load, save}
                            colour = GuiBase.TXT_YELLOW; break;
                    }
                    lines.add(prop.getName() + separator + colour + val.toString().toLowerCase());
                }
            }

            return lines;
        }

        return Collections.emptyList();
    }
}
