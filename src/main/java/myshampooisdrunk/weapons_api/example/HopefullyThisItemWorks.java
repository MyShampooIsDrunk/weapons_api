package myshampooisdrunk.weapons_api.example;

import myshampooisdrunk.weapons_api.weapon.AbstractCustomItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

public class HopefullyThisItemWorks extends AbstractCustomItem {
    public HopefullyThisItemWorks() {
        super(Items.BONE,new Identifier("shampoos_weapons_api","greatest_programmer_ever"),"if.this.breaks.i.will.cry");
    }

    @Override
    public void onUse(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable cir) {
        player.attack(player);
    }
}
