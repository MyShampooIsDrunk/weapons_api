package myshampooisdrunk.weapons_api.example;

import myshampooisdrunk.weapons_api.cooldown.CustomItemCooldownManagerI;
import myshampooisdrunk.weapons_api.register.CustomItemRegistry;
import myshampooisdrunk.weapons_api.weapon.AbstractCustomItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

public class GoofySillyGoofyItem extends AbstractCustomItem {
    public GoofySillyGoofyItem() {
        super(Items.BONE,new Identifier("shampoos_weapons_api","goofy_silly_goofy_item"),"if.this.works.i.will.cry.tears.of.joy");
    }

    @Override
    public void onUse(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable cir) {
<<<<<<< HEAD
        if(!((CustomItemCooldownManagerI)player).getCustomItemCooldownManager().isCoolingDown(this)){
            ((CustomItemCooldownManagerI)player).getCustomItemCooldownManager().set(this,100);
            PlayerInventory inv = player.getInventory();
            List<Integer> slots = new ArrayList<>(List.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35));
            for(int s = 0; s < 36; s++){
                int n = world.getRandom().nextBetweenExclusive(0,slots.size());
                int s2 = slots.get(n);
                ItemStack one = inv.getStack(s);
                ItemStack two = inv.getStack(s2);
                slots.remove(n);
                inv.setStack(s,two);
                inv.setStack(s2,one);
            }
        }else{
            player.sendMessage(Text.of(create().getName().getString()+ " still has " +
                    (int)(0.95+((CustomItemCooldownManagerI) player).getCustomItemCooldownManager().getCooldownProgress(this,0)*5f) + " second(s) left." ),true);
=======
        PlayerInventory inv = player.getInventory();
        List<Integer> slots = new ArrayList<>(List.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35));
        for(int s = 0; s < 36; s++){
            int n = world.getRandom().nextBetweenExclusive(0,slots.size());
            int s2 = slots.get(n);
            ItemStack one = inv.getStack(s);
            ItemStack two = inv.getStack(s2);
            slots.remove(n);
            inv.setStack(s,two);
            inv.setStack(s2,one);
>>>>>>> 8e604ee (polishing things)
        }
    }
}
