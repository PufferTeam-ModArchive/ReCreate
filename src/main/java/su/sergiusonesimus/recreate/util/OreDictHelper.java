package su.sergiusonesimus.recreate.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictHelper {

    public static boolean containsMatch(boolean strict, List<ItemStack> inputs, ItemStack... targets) {
        try {
            Method containsMatch = OreDictionary.class
                .getDeclaredMethod("containsMatch", boolean.class, List.class, ItemStack[].class);
            containsMatch.setAccessible(true);
            return (boolean) containsMatch.invoke(null, strict, inputs, targets);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException e) {
            return false;
        }
    }

}
