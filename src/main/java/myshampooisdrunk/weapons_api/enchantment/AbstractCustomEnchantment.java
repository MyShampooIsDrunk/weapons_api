package myshampooisdrunk.weapons_api.enchantment;

import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumMap;
import java.util.Map;

public abstract class AbstractCustomEnchantment {
    public static final String CUSTOM_ENCHANTMENT_TRANSLATION_KEY = "custom_enchants.enchant.";
    private final EquipmentSlot[] slotTypes;
    private final Rarity rarity;
    public final EnchantmentTarget target;
    protected String translationKey;
    private final Identifier id;
    protected AbstractCustomEnchantment(Identifier id,Rarity rarity, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        this.rarity = rarity;
        this.target = target;
        this.slotTypes = slotTypes;
        this.id = id;
    }

    public Map<EquipmentSlot, ItemStack> getEquipment(LivingEntity entity) {
        EnumMap<EquipmentSlot, ItemStack> map = Maps.newEnumMap(EquipmentSlot.class);
        for (EquipmentSlot equipmentSlot : this.slotTypes) {
            ItemStack itemStack = entity.getEquippedStack(equipmentSlot);
            if (itemStack.isEmpty()) continue;
            map.put(equipmentSlot, itemStack);
        }
        return map;
    }

    public Rarity getRarity() {
        return this.rarity;
    }

    public int getMinLevel() {
        return 1;
    }

    public int getMaxLevel() {
        return 1;
    }

    public int getMinPower(int level) {
        return 1 + level * 10;
    }

    public int getMaxPower(int level) {
        return this.getMinPower(level) + 5;
    }

    public int getProtectionAmount(int level, DamageSource source) {
        return 0;
    }

    public float getAttackDamage(int level, EntityGroup group) {
        return 0.0f;
    }
    public final boolean canCombine(AbstractCustomEnchantment other) {
        return this.canAccept(other) && other.canAccept(this);
    }
    protected boolean canAccept(AbstractCustomEnchantment other) {
        return this != other;
    }
    public String getTranslationKey() {
        return this.translationKey;
    }

    public Text getName(int level) {//key is supposed to be smth like "encharmtnet.level"
        MutableText mutableText = Text.translatable(this.getTranslationKey());
        if (this.isCursed()) {
            mutableText.formatted(Formatting.RED);
        } else {
            mutableText.formatted(Formatting.GRAY);
        }
        if (level != 1 || this.getMaxLevel() != 1) {
            mutableText.append(ScreenTexts.SPACE).append(Text.translatable(CUSTOM_ENCHANTMENT_TRANSLATION_KEY + level));
        }
        return mutableText;
    }
    public boolean isAcceptableItem(ItemStack stack) {
        return this.target.isAcceptableItem(stack.getItem());
    }
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
    }
    public void onUserDamaged(LivingEntity user, Entity attacker, int level) {
    }
    public void onAttack(Entity target, LivingEntity user, int level, CallbackInfo ci){

    }
    public void onUse(World world, PlayerEntity user, Hand hand,int level, CallbackInfoReturnable cir){
    }
    public void onSneak(boolean sneaking, PlayerEntity player, int level, CallbackInfo ci){
    }
    public void whileSneak(PlayerEntity p, int level, CallbackInfo ci){
    }
    public void onDrop(PlayerEntity p, ItemStack stack, boolean throwRandomly, boolean retainOwnership, int level, CallbackInfo ci){
    }
    public void onBlockInteraction(){
    }
    public void onInteract(PlayerEntity user, Entity entity, Hand hand, int level, CallbackInfoReturnable<ActionResult> cir){
    }
    public void onJump(PlayerEntity p, int level, CallbackInfo ci){
    }
    public boolean isTreasure() {
        return false;
    }
    public boolean isCursed() {
        return false;
    }
    public boolean isAvailableForEnchantedBookOffer() {
        return true;
    }
    public boolean isAvailableForRandomSelection() {
        return true;
    }
    public Identifier getId(){
        return id;
    }

    public static enum Rarity {
        COMMON(10),
        UNCOMMON(5),
        RARE(2),
        VERY_RARE(1);

        private final int weight;

        private Rarity(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return this.weight;
        }
    }
}
