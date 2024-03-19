package myshampooisdrunk.weapons_api.enchantment;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

public interface CustomEnchantsOnEnchantedBook {
    public static void addCustomEnchantment(ItemStack stack, CustomEnchantmentInstance instance) {
        NbtList nbtList = CustomEnchantmentHelper.getEnchantmentNbt(stack);
        boolean bl = true;
        Identifier identifier = CustomEnchantmentHelper.getEnchantmentId(instance.enchantment);
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            Identifier identifier2 = CustomEnchantmentHelper.getIdFromNbt(nbtCompound);
            if (identifier2 == null || !identifier2.equals(identifier)) continue;
            if (CustomEnchantmentHelper.getLevelFromNbt(nbtCompound) < instance.level) {
                CustomEnchantmentHelper.writeLevelToNbt(nbtCompound, instance.level);
            }
            bl = false;
            break;
        }
        if (bl) {
            nbtList.add(CustomEnchantmentHelper.createNbt(identifier, instance.level));
        }
        stack.getOrCreateNbt().put(CustomEnchantmentHelper.STORED_CUSTOM_ENCHANT_KEY, nbtList);
    }
}
