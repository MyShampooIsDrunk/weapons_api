package myshampooisdrunk.weapons_api.mixin;

import myshampooisdrunk.weapons_api.WeaponAPI;
import myshampooisdrunk.weapons_api.enchantment.CustomEnchantmentHelper;
import myshampooisdrunk.weapons_api.weapon.AbstractCustomItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at=@At("HEAD"),method="attack")
    private void attack(Entity target, CallbackInfo ci) {
        PlayerEntity user = (PlayerEntity)(Object)this;
        ItemStack item = user.getStackInHand(Hand.MAIN_HAND);
        CustomEnchantmentHelper.getEnchantmentList(item).forEach((enchant, level)->{
            enchant.onAttack(target, user,level,ci);
        });
        if(WeaponAPI.ITEMS.containsKey(item.getItem())) {
            for (AbstractCustomItem custom : WeaponAPI.ITEMS.get(item.getItem())) {
                if (custom.getItem().equals(item.getItem()) && item.getOrCreateNbt().getInt("CustomModelData") == custom.getId()) {
                    custom.onAttack(target, user, ci);
                    break;
                }
            }
        }
    }
    @Inject(at=@At("HEAD"),method="dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;")
    private void dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir){
        CustomEnchantmentHelper.getEnchantmentList(stack).forEach((enchant, level)->{
            enchant.onDrop((PlayerEntity) (Object) this, stack,throwRandomly,retainOwnership,level,cir);
        });
        if(WeaponAPI.ITEMS.containsKey(stack.getItem())) {
            PlayerEntity user = (PlayerEntity) (Object) this;
            for (AbstractCustomItem custom : WeaponAPI.ITEMS.get(stack.getItem())) {
                if (custom.getItem().equals(stack.getItem()) && stack.getOrCreateNbt().getInt("CustomModelData") == custom.getId()) {
                    custom.onDrop(user, stack, throwRandomly, retainOwnership, cir);
                    break;
                }
            }
        }
    }
    @Inject(at=@At("HEAD"),method="tick")
    public void whileSneaking(CallbackInfo ci){
        if(this.isSneaking()){
            PlayerEntity user = (PlayerEntity)(Object)this;
            ItemStack item = user.getStackInHand(Hand.MAIN_HAND);
            CustomEnchantmentHelper.getEnchantmentList(item).forEach((enchant, level)->{
                enchant.whileSneak((PlayerEntity) (Object) this, level, ci);
            });
            if(WeaponAPI.ITEMS.containsKey(item.getItem())) {
                for (AbstractCustomItem custom : WeaponAPI.ITEMS.get(item.getItem())) {
                    if (custom.getItem().equals(item.getItem()) && item.getOrCreateNbt().getInt("CustomModelData") == custom.getId()) {
                        custom.whileSneak((PlayerEntity) (Object) this, ci);
                        break;
                    }
                }
            }
        }
    }
    @Inject(at=@At("HEAD"),method="interact")
    public void onInteract(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        PlayerEntity user = (PlayerEntity)(Object)this;
        ItemStack item = user.getStackInHand(Hand.MAIN_HAND);
        CustomEnchantmentHelper.getEnchantmentList(item).forEach((enchant, level)->{
            enchant.onInteract(user, entity,hand,level, cir);
        });
        if(WeaponAPI.ITEMS.containsKey(item.getItem())) {
            for (AbstractCustomItem custom : WeaponAPI.ITEMS.get(item.getItem())) {
                if (custom.getItem().equals(item.getItem()) && item.getOrCreateNbt().getInt("CustomModelData") == custom.getId()) {
                    custom.onEntityInteraction(user,entity,hand,cir);
                    break;
                }
            }
        }
    }
    @Inject(at=@At("HEAD"),method="jump")
    private void jump(CallbackInfo ci) {
        PlayerEntity user = (PlayerEntity)(Object)this;
        ItemStack item = user.getStackInHand(Hand.MAIN_HAND);
        CustomEnchantmentHelper.getEnchantmentList(item).forEach((enchant, level)->{
            enchant.onJump(user, level, ci);
        });
        if(WeaponAPI.ITEMS.containsKey(item.getItem())) {
            for (AbstractCustomItem custom : WeaponAPI.ITEMS.get(item.getItem())) {
                if (custom.getItem().equals(item.getItem()) && item.getOrCreateNbt().getInt("CustomModelData") == custom.getId()) {
                    custom.onJump(user, ci);
                    break;
                }
            }
        }
    }
}
