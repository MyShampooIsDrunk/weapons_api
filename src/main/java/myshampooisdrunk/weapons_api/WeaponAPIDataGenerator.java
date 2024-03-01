package myshampooisdrunk.weapons_api;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeCraftedCriterion;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.*;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import net.minecraft.util.collection.DefaultedList;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WeaponAPIDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator gen) {
		//initializeDataGen(gen);
	}

	public static void initializeDataGen(FabricDataGenerator gen){
		FabricDataGenerator.Pack pack = gen.createPack();
		pack.addProvider(CustomItemLootTable::new);
		pack.addProvider(AdvancementsProvider::new);
		pack.addProvider(CustomRecipeProvider::new);
	}

	static class AdvancementsProvider extends FabricAdvancementProvider {
		protected AdvancementsProvider(FabricDataOutput dataGenerator) {
			super(dataGenerator);
		}

		@Override
		public void generateAdvancement(Consumer<AdvancementEntry> consumer) {
			for(Identifier id : WeaponAPI.CUSTOM_RECIPES.keySet()){
				AdvancementEntry temp = Advancement.Builder.create()
						.criterion("after_crafted", RecipeCraftedCriterion.Conditions.create(WeaponAPI.RECIPE_IDS.get(id).getLeft()))
						.rewards(AdvancementRewards.Builder.loot(WeaponAPI.RECIPE_IDS.get(id).getMiddle()))
						.build(WeaponAPI.RECIPE_IDS.get(id).getRight());
				consumer.accept(temp);
			}
		}
	}
	static class CustomRecipeProvider extends FabricRecipeProvider {

		public CustomRecipeProvider(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void generate(RecipeExporter exporter) {
			for(Identifier id : WeaponAPI.CUSTOM_RECIPES.keySet()){
				if(WeaponAPI.CUSTOM_RECIPES.get(id).getLeft() instanceof ShapelessRecipe r){
					ShapelessRecipeJsonBuilder j = ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.KNOWLEDGE_BOOK);
					for(Ingredient in : r.getIngredients()){
						j=j.input(in);
					}

					j.group("").criterion(RecipeProvider.hasItem(Items.AIR), RecipeProvider.conditionsFromItem(Items.AIR))
							.offerTo(exporter,WeaponAPI.RECIPE_IDS.get(id).getLeft());
				}else if(WeaponAPI.CUSTOM_RECIPES.get(id).getLeft() instanceof ShapedRecipe s){
//					for(Ingredient j : s.getIngredients()){
//						System.out.println(Arrays.toString(j.getMatchingStacks()));
//					}
					final ShapedRecipeJsonBuilder[] j = {ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.KNOWLEDGE_BOOK)};
					Character[] a = new Character[]{'a','b','c','d','e','f','g','h','i'};
					String[] pattern = new String[3];
					Map<ItemConvertible, Character> recipe = new HashMap<>();
//					for(int b = 0; b < s.getIngredients().size(); b++){
//						ItemStack[] items = s.getIngredients().get(b).getMatchingStacks();
//						Item i;
//						if(items.length == 0){
//							i=Items.AIR;
//						}else{
//							i = items[0].getItem();
//						}
//						System.out.println(i);
//						j=j.input(a[b],i);
//					}
					DefaultedList<Ingredient> ingredients = s.getIngredients();
					int counter = 0;
					for(int i = 0; i < 3; i++){
						StringBuilder level = new StringBuilder();
						for(int k = 0; k < 3; k++){
							ItemStack[] items = s.getIngredients().get(i*3+k).getMatchingStacks();
							Item item;
							if(items.length == 0){
								item=Items.AIR;
							}else{
								item = items[0].getItem();
							}
							if(!recipe.containsKey(item) && !item.equals(Items.AIR)){
								recipe.put(item,a[counter++]);
							}
							if(item.equals(Items.AIR))level.append(" ");
							if(recipe.containsKey(item)){
								level.append(recipe.get(item));
							}
						}
						pattern[i]=level.toString();
					}
					recipe.forEach((item,key)-> j[0] = j[0].input(key,item));

					j[0].pattern(pattern[0]).pattern(pattern[1]).pattern(pattern[2]).group("")
							.criterion(RecipeProvider.hasItem(Items.AIR), RecipeProvider.conditionsFromItem(Items.AIR))
							.offerTo(exporter,WeaponAPI.RECIPE_IDS.get(id).getLeft());
				}
			}
		}
	}
	public static <T> T echo(T init){
		System.out.println(init);
		return init;
	}
	static class CustomItemLootTable extends SimpleFabricLootTableProvider{
		public CustomItemLootTable(FabricDataOutput output) {
			super(output, LootContextTypes.ADVANCEMENT_REWARD);
		}

		@Override
		public void accept(BiConsumer<Identifier, LootTable.Builder> exporter) {
			for(Identifier i : WeaponAPI.CUSTOM_RECIPES.keySet()){
				ItemStack item = WeaponAPI.CUSTOM_RECIPES.get(i).getRight().create();
				exporter.accept(
						WeaponAPI.RECIPE_IDS.get(i).getMiddle(),
						new LootTable.Builder().pool(
								LootPool.builder()
										.rolls(ConstantLootNumberProvider.create(1))
										.with(
												ItemEntry.builder(item.getItem()).apply(SetNbtLootFunction.builder(item.getNbt()))
										)
						)
				);
			}
		}
	}
}
