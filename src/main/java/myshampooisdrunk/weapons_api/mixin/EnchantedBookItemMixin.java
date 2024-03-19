package myshampooisdrunk.weapons_api.mixin;

import myshampooisdrunk.weapons_api.enchantment.AbstractCustomEnchantment;
import myshampooisdrunk.weapons_api.enchantment.CustomEnchantmentHelper;
import myshampooisdrunk.weapons_api.enchantment.CustomEnchantmentInstance;
import myshampooisdrunk.weapons_api.enchantment.CustomEnchantsOnEnchantedBook;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EnchantedBookItem.class)
public abstract class EnchantedBookItemMixin extends Item //implements CustomEnchantsOnEnchantedBook
{
//    @Override
//    public void addCustomEnchantment(ItemStack stack, CustomEnchantmentInstance instance) {
//        NbtList nbtList = CustomEnchantmentHelper.getEnchantmentNbt(stack);
//        boolean bl = true;
//        Identifier identifier = CustomEnchantmentHelper.getEnchantmentId(instance.enchantment);
//        for (int i = 0; i < nbtList.size(); ++i) {
//            NbtCompound nbtCompound = nbtList.getCompound(i);
//            Identifier identifier2 = CustomEnchantmentHelper.getIdFromNbt(nbtCompound);
//            if (identifier2 == null || !identifier2.equals(identifier)) continue;
//            if (CustomEnchantmentHelper.getLevelFromNbt(nbtCompound) < instance.level) {
//                CustomEnchantmentHelper.writeLevelToNbt(nbtCompound, instance.level);
//            }
//            bl = false;
//            break;
//        }
//        if (bl) {
//            nbtList.add(CustomEnchantmentHelper.createNbt(identifier, instance.level));
//        }
//        stack.getOrCreateNbt().put(CustomEnchantmentHelper.STORED_CUSTOM_ENCHANT_KEY, nbtList);
//    }
    @Inject(method="appendTooltip",at=@At("TAIL"))
    public void appendCustomTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci){
        ItemStack.appendEnchantments(tooltip, CustomEnchantmentHelper.getEnchantmentNbt(stack));
    }

    public EnchantedBookItemMixin(Settings settings) {super(settings);}
}
