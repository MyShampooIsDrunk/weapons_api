package myshampooisdrunk.weapons_api.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import myshampooisdrunk.weapons_api.WeaponAPI;
import myshampooisdrunk.weapons_api.mixin.EnchantedBookItemMixin;
import net.minecraft.enchantment.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class CustomEnchantmentHelper {
    public static final String STORED_CUSTOM_ENCHANT_KEY = "stored_custom_enchants";
    public static final String CUSTOM_ENCHANT_KEY = "shampoos_custom_enchants";
    public static final String CUSTOM_ID_KEY = "id";
    public static final String CUSTOM_LEVEL_KEY = "level";
    public static NbtCompound createNbt(Identifier id, int level){
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString(CUSTOM_ID_KEY, String.valueOf(id));
        nbtCompound.putInt(CUSTOM_LEVEL_KEY, level);
        return nbtCompound;
//        NbtCompound customEnch = item.getOrCreateSubNbt(CUSTOM_ENCHANT_KEY);
//        NbtList enchants;
//        NbtCompound ench = new NbtCompound();
//        ench.putString(CUSTOM_ID_KEY, String.valueOf(enchantment.getId()));
//        ench.putInt(CUSTOM_LEVEL_KEY, level);
//        item.setSubNbt(CUSTOM_ENCHANT_KEY,customEnch);
//        return item;
        //NbtCompound nbtCompound = new NbtCompound();
        //        nbtCompound.putString(ID_KEY, String.valueOf(id));
        //        nbtCompound.putShort(LEVEL_KEY, (short)lvl);
    }
    public static void set(Map<AbstractCustomEnchantment, Integer> enchantments, ItemStack stack) {
        NbtList nbtList = new NbtList();
        for (Map.Entry<AbstractCustomEnchantment, Integer> entry : enchantments.entrySet()) {
            AbstractCustomEnchantment enchantment = entry.getKey();
            if (enchantment == null) continue;
            int i = entry.getValue();
            nbtList.add(createNbt(enchantment.getId(), i));
            if (!stack.isOf(Items.ENCHANTED_BOOK)) continue;
            addCustomEnchantment(stack, new CustomEnchantmentInstance(enchantment, i));
        }
        if (nbtList.isEmpty()) {
            stack.removeSubNbt(CUSTOM_ENCHANT_KEY);
        } else if (!stack.isOf(Items.ENCHANTED_BOOK)) {
            stack.setSubNbt(CUSTOM_ENCHANT_KEY, nbtList);
        }
    }
    public static NbtList getEnchantmentNbt(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null) {
            return nbtCompound.getList(STORED_CUSTOM_ENCHANT_KEY, NbtElement.COMPOUND_TYPE);
        }
        return new NbtList();
    }

    public static int getLevel(AbstractCustomEnchantment enchantment, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        Identifier identifier = getEnchantmentId(enchantment);
        NbtList nbtList = getEnchantments(stack);
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            Identifier identifier2 = getIdFromNbt(nbtCompound);
            if (identifier2 == null || !identifier2.equals(identifier)) continue;
            return getLevelFromNbt(nbtCompound);
        }
        return 0;
    }
    public static void writeLevelToNbt(NbtCompound nbt, int lvl) {
        nbt.putShort(CUSTOM_LEVEL_KEY, (short)lvl);
    }

    public static int getLevelFromNbt(NbtCompound nbt) {
        return MathHelper.clamp(nbt.getInt(CUSTOM_LEVEL_KEY), 0, Integer.MAX_VALUE);
    }

    @Nullable
    public static Identifier getIdFromNbt(NbtCompound nbt) {
        return Identifier.tryParse(nbt.getString(CUSTOM_ID_KEY));
    }

    @Nullable
    public static Identifier getEnchantmentId(AbstractCustomEnchantment enchantment) {
        return enchantment.getId();
    }
    public static NbtList getEnchantments(ItemStack stack){
        if (stack.getNbt() != null) {
            return stack.getNbt().getList(CUSTOM_ENCHANT_KEY, NbtElement.COMPOUND_TYPE);
        }
        return new NbtList();
    }
    //public void appendCustomTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci){
    //        ItemStack.appendEnchantments(tooltip, CustomEnchantmentHelper.getEnchantmentNbt(stack));
    //    }
    public static void appendCustomEnchantments(List<Text> tooltip, NbtList enchantments) {
        for (int i = 0; i < enchantments.size(); ++i) {
            NbtCompound nbtCompound = enchantments.getCompound(i);
            AbstractCustomEnchantment e = WeaponAPI.ENCHANTMENTS.getOrDefault(getIdFromNbt(nbtCompound),null);
            if(e != null){
                tooltip.add(e.getName(CustomEnchantmentHelper.getLevelFromNbt(nbtCompound)));
            }
        }
    }
//    public static int getEnchantment(NbtList nbt, AbstractCustomEnchantment enchantment){
//        for(int i = 0; i < nbt.size();i++){
//            NbtCompound compound = nbt.getCompound(i);
//            AbstractCustomEnchantment ench = WeaponAPI.ENCHANTMENTS.getOrDefault(getIdFromNbt(compound),null);
//            if(ench != null){
//                enchantment -> consumer.accept((Enchantment)enchantment, EnchantmentHelper.getLevelFromNbt(nbtCompound))
//            }
//
//        }
//
//    }

    public static void forEachCustomEnchantment(Consumer consumer, ItemStack stack){
        if (stack.isEmpty()) {
            return;
        }
        NbtList nbtList = getEnchantments(stack);
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound compound = nbtList.getCompound(i);
            AbstractCustomEnchantment ench = WeaponAPI.ENCHANTMENTS.getOrDefault(getIdFromNbt(compound),null);
            if(ench != null){
                consumer.accept(ench, getLevelFromNbt(compound));
            }
        }
    }
    public static void addCustomEnchantment(ItemStack stack, CustomEnchantmentInstance instance) {
        stack.getOrCreateNbt();
        if (!stack.getNbt().contains(CUSTOM_ENCHANT_KEY, NbtElement.LIST_TYPE)) {
            stack.getNbt().put(CUSTOM_ENCHANT_KEY, new NbtList());
        }
        NbtList nbtList = stack.getNbt().getList(CUSTOM_ENCHANT_KEY, NbtElement.COMPOUND_TYPE);
        nbtList.add(createNbt(getEnchantmentId(instance.enchantment), instance.level));
    }
    public static void addCustomEnchantment(ItemStack stack, AbstractCustomEnchantment enchantment, int level){
        addCustomEnchantment(stack,new CustomEnchantmentInstance(enchantment,level));
    }
//    public static void useEnchant(ItemStack item, AbstractCustomEnchantment enchantment){
//        WeaponAPI.ENCHANTMENTS.forEach((id, ench)->{
//            if(CustomEnchantmentHelper.getEnchantments(item).){
//
//            }
//        });
//    }
    public static Map<AbstractCustomEnchantment, Integer> get(ItemStack stack) {
        NbtList nbtList = stack.isOf(Items.ENCHANTED_BOOK) ? getEnchantmentNbt(stack) : getEnchantments(stack);
        return CustomEnchantmentHelper.fromNbt(nbtList);
    }
    public static Map<AbstractCustomEnchantment, Integer> fromNbt(NbtList list) {
        LinkedHashMap<AbstractCustomEnchantment, Integer> map = Maps.newLinkedHashMap();
        for (int i = 0; i < list.size(); ++i) {
            NbtCompound compound = list.getCompound(i);
            AbstractCustomEnchantment ench = WeaponAPI.ENCHANTMENTS.getOrDefault(getIdFromNbt(compound),null);
            if(ench != null){
                map.put(ench, getLevelFromNbt(compound));
            }
        }
        return map;
    }
    private static void forEachCustomEnchantment(Consumer consumer, Iterable<ItemStack> stacks) {
        for (ItemStack itemStack : stacks) {
            forEachCustomEnchantment(consumer, itemStack);
        }
    }

    public static int getProtectionAmount(Iterable<ItemStack> equipment, DamageSource source) {
        MutableInt mutableInt = new MutableInt();
        forEachCustomEnchantment((AbstractCustomEnchantment enchantment, int level) -> mutableInt.add(enchantment.getProtectionAmount(level, source)), equipment);
        return mutableInt.intValue();
    }

    public static float getAttackDamage(ItemStack stack, EntityGroup group) {
        MutableFloat mutableFloat = new MutableFloat();
        forEachCustomEnchantment((AbstractCustomEnchantment enchantment, int level) -> mutableFloat.add(enchantment.getAttackDamage(level, group)), stack);
        return mutableFloat.floatValue();
    }
    public static void onUserDamaged(LivingEntity user, Entity attacker) {
        Consumer consumer = (enchantment, level) -> enchantment.onUserDamaged(user, attacker, level);
        if (user != null) {
            forEachCustomEnchantment(consumer, user.getItemsEquipped());
        }
        if (attacker instanceof PlayerEntity) {
            forEachCustomEnchantment(consumer, user.getMainHandStack());
        }
    }

    public static void onTargetDamaged(LivingEntity user, Entity target) {
        Consumer consumer = (enchantment, level) -> enchantment.onTargetDamaged(user, target, level);
        if (user != null) {
            forEachCustomEnchantment(consumer, user.getItemsEquipped());
        }
        if (user instanceof PlayerEntity) {
            forEachCustomEnchantment(consumer, user.getMainHandStack());
        }
    }
    public static int getEquipmentLevel(AbstractCustomEnchantment enchantment, LivingEntity entity) {
        Collection<ItemStack> iterable = enchantment.getEquipment(entity).values();
        if (iterable == null) {
            return 0;
        }
        int i = 0;
        for (ItemStack itemStack : iterable) {
            int j = getLevel(enchantment, itemStack);
            if (j <= i) continue;
            i = j;
        }
        return i;
    }
    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> chooseEquipmentWith(AbstractCustomEnchantment enchantment, LivingEntity entity) {
        return chooseEquipmentWith(enchantment, entity, stack -> true);
    }
    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> chooseEquipmentWith(AbstractCustomEnchantment enchantment, LivingEntity entity, Predicate<ItemStack> condition) {
        Map<EquipmentSlot, ItemStack> map = enchantment.getEquipment(entity);
        if (map.isEmpty()) {
            return null;
        }
        ArrayList<Map.Entry<EquipmentSlot, ItemStack>> list = Lists.newArrayList();
        for (Map.Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
            ItemStack itemStack = entry.getValue();
            if (itemStack.isEmpty() || getLevel(enchantment, itemStack) <= 0 || !condition.test(itemStack)) continue;
            list.add(entry);
        }
        return list.isEmpty() ? null : list.get(entity.getRandom().nextInt(list.size()));
    }
    public static int calculateRequiredExperienceLevel(net.minecraft.util.math.random.Random random, int slotIndex, int bookshelfCount, ItemStack stack) {
        Item item = stack.getItem();
        int i = item.getEnchantability();
        if (i <= 0) {
            return 0;
        }
        if (bookshelfCount > 15) {
            bookshelfCount = 15;
        }
        int j = random.nextInt(8) + 1 + (bookshelfCount >> 1) + random.nextInt(bookshelfCount + 1);
        if (slotIndex == 0) {
            return Math.max(j / 3, 1);
        }
        if (slotIndex == 1) {
            return j * 2 / 3 + 1;
        }
        return Math.max(j, bookshelfCount * 2);
    }
    public static ItemStack enchant(net.minecraft.util.math.random.Random random, ItemStack target, int level, boolean treasureAllowed) {
        List<CustomEnchantmentInstance> list = generateEnchantments(random, target, level, treasureAllowed);
        boolean bl = target.isOf(Items.BOOK);
        if (bl) {
            target = new ItemStack(Items.ENCHANTED_BOOK);
        }
        for (CustomEnchantmentInstance instance : list) {
            if (bl) {
                CustomEnchantsOnEnchantedBook.addCustomEnchantment(target, instance);
                continue;
            }else {
                addCustomEnchantment(target, instance);
            }
        }
        return target;
    }
    public static List<CustomEnchantmentInstance> generateEnchantments(Random random, ItemStack stack, int level, boolean treasureAllowed) {
        ArrayList<CustomEnchantmentInstance> list = Lists.newArrayList();
        Item item = stack.getItem();
        int i = item.getEnchantability();
        if (i <= 0) {
            return list;
        }
        level += 1 + random.nextInt(i / 4 + 1) + random.nextInt(i / 4 + 1);
        float f = (random.nextFloat() + random.nextFloat() - 1.0f) * 0.15f;
        List<CustomEnchantmentInstance> list2 = getPossibleEntries(level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE), stack, treasureAllowed);
        if (!list2.isEmpty()) {
            Weighting.getRandom(random, list2).ifPresent(list::add);
            while (random.nextInt(50) <= level) {
                if (!list.isEmpty()) {
                    removeConflicts(list2, Util.getLast(list));
                }
                if (list2.isEmpty()) break;
                Weighting.getRandom(random, list2).ifPresent(list::add);
                level /= 2;
            }
        }
        return list;
    }
    public static void removeConflicts(List<CustomEnchantmentInstance> possibleEntries, CustomEnchantmentInstance pickedEntry) {
        Iterator<CustomEnchantmentInstance> iterator = possibleEntries.iterator();
        while (iterator.hasNext()) {
            if (pickedEntry.enchantment.canCombine(iterator.next().enchantment)) continue;
            iterator.remove();
        }
    }

    /**
     * {@return whether the {@code candidate} enchantment is compatible with the
     * {@code existing} enchantments}
     */
    public static boolean isCompatible(Collection<AbstractCustomEnchantment> existing, AbstractCustomEnchantment candidate) {
        for (AbstractCustomEnchantment enchantment : existing) {
            if (enchantment.canCombine(candidate)) continue;
            return false;
        }
        return true;
    }
    public static List<CustomEnchantmentInstance> getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed) {
        ArrayList<CustomEnchantmentInstance> list = Lists.newArrayList();
        Item item = stack.getItem();
        boolean bl = stack.isOf(Items.BOOK);
        block0: for (AbstractCustomEnchantment enchantment : WeaponAPI.ENCHANTMENTS.values()) {
            if (enchantment.isTreasure() && !treasureAllowed || !enchantment.isAvailableForRandomSelection() || !enchantment.target.isAcceptableItem(item) && !bl) continue;
            for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                if (power < enchantment.getMinPower(i) || power > enchantment.getMaxPower(i)) continue;
                list.add(new CustomEnchantmentInstance(enchantment, i));
                continue block0;
            }
        }
        return list;
    }
    public static Map<AbstractCustomEnchantment,Integer> getEnchantmentList(ItemStack item){
        return CustomEnchantmentHelper.fromNbt(getEnchantments(item));
    }
    @FunctionalInterface
    static interface Consumer {
        public void accept(AbstractCustomEnchantment var1, int var2);
    }
}
