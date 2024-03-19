package myshampooisdrunk.weapons_api;

import myshampooisdrunk.weapons_api.command.CustomEnchantCommand;
import myshampooisdrunk.weapons_api.enchantment.AbstractCustomEnchantment;
import myshampooisdrunk.weapons_api.example.GoofySillyGoofyItem;
import myshampooisdrunk.weapons_api.example.HopefullyThisItemWorks;
import myshampooisdrunk.weapons_api.register.CustomItemRegistry;
import myshampooisdrunk.weapons_api.weapon.AbstractCustomItem;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class WeaponAPI implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("shampoos_weapons_api");
	public static final Map<Identifier, AbstractCustomEnchantment> ENCHANTMENTS = new HashMap<>();
	public static final Map<Item,Set<AbstractCustomItem>> ITEMS = new HashMap<>();
	public static final Map<Item,Integer> ITEM_COUNT = new HashMap<>();
	public static final Map<Identifier, Pair<CraftingRecipe,AbstractCustomItem>> CUSTOM_RECIPES = new HashMap<>();
	public static final Map<Identifier,Triple<Identifier,Identifier,Identifier>> RECIPE_IDS = new HashMap<>();

	public static void initializeRecipes(){
		for(Identifier id : CUSTOM_RECIPES.keySet()){
			RECIPE_IDS.put(id,Triple.of(
					new Identifier(id.getNamespace(),id.getPath() + "_recipe"),
					new Identifier(id.getNamespace(),id.getPath() + "_recipe_output"),
					new Identifier(id.getNamespace(),id.getPath() + "_recipe_advancement")
			));
		}
	}
	
	@Override
	public void onInitialize() {
		AbstractCustomEnchantment veryVerySillyEnchantment = new AbstractCustomEnchantment(
				new Identifier("shampoos_stupid_mod","bruhhhhhhhhhhhhhhhh"),
				AbstractCustomEnchantment.Rarity.VERY_RARE,
				EnchantmentTarget.WEAPON,
				new EquipmentSlot[] {}
		){
			@Override
			public void onUse(World world, PlayerEntity user, Hand hand, int level, CallbackInfoReturnable cir){
				user.giveItemStack(new ItemStack(Items.DIRT,level));
			}
			@Override
			public int getMaxLevel(){return 12;}
		};
		CustomItemRegistry.registerCustomEnchantment(veryVerySillyEnchantment);
//		AbstractCustomItem bbbbbbbbbbbbbbbbbb = new GoofySillyGoofyItem();
//		AbstractCustomItem bbbbbbbbbbbbbbbbbb2 = new HopefullyThisItemWorks();
//		CustomItemRegistry.registerItem(bbbbbbbbbbbbbbbbbb);
//		CustomItemRegistry.registerItem(bbbbbbbbbbbbbbbbbb2);
//		CustomItemRegistry.registerRecipe(new ShapelessRecipe("", CraftingRecipeCategory.MISC,
//				bbbbbbbbbbbbbbbbbb.create(),
//				DefaultedList.copyOf(Ingredient.EMPTY,Ingredient.ofItems(Items.STONE),Ingredient.ofItems(Items.STONE))
//		),bbbbbbbbbbbbbbbbbb.getIdentifier(),bbbbbbbbbbbbbbbbbb);
//		CustomItemRegistry.registerRecipe(new ShapedRecipe("", CraftingRecipeCategory.MISC,
//				RawShapedRecipe.create(Map.of('d',Ingredient.ofItems(Items.DEEPSLATE),'a',Ingredient.EMPTY),"ddd","dda","daa")
//				,bbbbbbbbbbbbbbbbbb2.create()
//		),bbbbbbbbbbbbbbbbbb2.getIdentifier(),bbbbbbbbbbbbbbbbbb2);
//		CustomItemRegistry.addToGroup(bbbbbbbbbbbbbbbbbb2, ItemGroups.COMBAT);
//		CustomItemRegistry.addToGroup(bbbbbbbbbbbbbbbbbb, ItemGroups.COMBAT);
//
//		initializeRecipes();
		CommandRegistrationCallback.EVENT.register(
				(dispatcher, registryAccess, environment) -> CustomEnchantCommand.register(dispatcher)
		);
	}
}