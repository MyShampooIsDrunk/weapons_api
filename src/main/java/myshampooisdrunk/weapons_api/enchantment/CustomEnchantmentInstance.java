package myshampooisdrunk.weapons_api.enchantment;

import net.minecraft.util.collection.Weighted;

public class CustomEnchantmentInstance extends Weighted.Absent{
    public final AbstractCustomEnchantment enchantment;
    public final int level;

    public CustomEnchantmentInstance(AbstractCustomEnchantment enchantment, int level) {
        super(enchantment.getRarity().getWeight());
        this.enchantment = enchantment;
        this.level = level;
    }
}
