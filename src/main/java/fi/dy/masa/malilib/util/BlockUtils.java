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
import net.minecraft.util.math.MathHelper;
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
                    String value = val.toString();
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
                            colour = isLastElement ? GuiBase.TXT_GOLD : GuiBase.TXT_YELLOW;
                            if (isLastElement) value += "!";
                            break;
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
                            {
                                // from net.minecraft.client.particle.NoteParticle
                                // see also net.minecraft.block.NoteBlock
                                double d = (double)(Integer)val / 24.0;
                                float red = Math.max(0.0f, MathHelper.sin(((float)d + 0.0f) * ((float)Math.PI * 2)) * 0.65f + 0.35f);
                                float green = Math.max(0.0f, MathHelper.sin(((float)d + 0.33333334f) * ((float)Math.PI * 2)) * 0.65f + 0.35f);
                                float blue = Math.max(0.0f, MathHelper.sin(((float)d + 0.6666667f) * ((float)Math.PI * 2)) * 0.65f + 0.35f);

                                // 128 + float*127 to keep colour bright and visible over background
                                colour = String.format("§x#%02x%02x%02x", 128 + (int)(red*127), 128 + (int)(green*127), 128 + (int)(blue*127));
                            }
                            switch ((Integer)val % 12)
                            {
                                case 0: value += " (F♯)"; break;
                                case 1: value += " (G)"; break;
                                case 2: value += " (G♯)"; break;
                                case 3: value += " (A)"; break;
                                case 4: value += " (B♭)"; break;
                                case 5: value += " (B)"; break;
                                case 6: value += " (C)"; break;
                                case 7: value += " (C♯)"; break;
                                case 8: value += " (D)"; break;
                                case 9: value += " (E♭)"; break;
                                case 10: value += " (E)"; break;
                                case 11: value += " (F)"; break;
                            }
                            break;
                    }
                    lines.add(prop.getName() + separator + colour + value);
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
