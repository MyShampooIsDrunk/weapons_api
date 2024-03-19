package myshampooisdrunk.weapons_api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import myshampooisdrunk.weapons_api.WeaponAPI;
import myshampooisdrunk.weapons_api.enchantment.AbstractCustomEnchantment;
import myshampooisdrunk.weapons_api.enchantment.CustomEnchantmentHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class CustomEnchantCommand{
    private static final DynamicCommandExceptionType FAILED_ENTITY_EXCEPTION = new DynamicCommandExceptionType(entityName -> Text.stringifiedTranslatable("commands.enchant.failed.entity", entityName));
    private static final DynamicCommandExceptionType FAILED_ITEMLESS_EXCEPTION = new DynamicCommandExceptionType(entityName -> Text.stringifiedTranslatable("commands.enchant.failed.itemless", entityName));
    private static final DynamicCommandExceptionType FAILED_INCOMPATIBLE_EXCEPTION = new DynamicCommandExceptionType(itemName -> Text.stringifiedTranslatable("commands.enchant.failed.incompatible", itemName));
    private static final Dynamic2CommandExceptionType FAILED_LEVEL_EXCEPTION = new Dynamic2CommandExceptionType((level, maxLevel) -> Text.stringifiedTranslatable("commands.enchant.failed.level", level, maxLevel));
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.enchant.failed"));
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
        Collection<AbstractCustomEnchantment> collection = WeaponAPI.ENCHANTMENTS.values();
        return CommandSource.suggestIdentifiers(collection.stream().map(AbstractCustomEnchantment::getId), builder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                (LiteralArgumentBuilder)((LiteralArgumentBuilder) CommandManager.literal("enchant2")
                        .requires(source -> source.hasPermissionLevel(2)))
                        .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                .then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager
                                        .argument("custom_enchantment", IdentifierArgumentType.identifier())
                                        .suggests(SUGGESTION_PROVIDER))
                                        .executes(context -> CustomEnchantCommand.execute(
                                                (ServerCommandSource)context.getSource(),
                                                EntityArgumentType.getEntities(context, "targets"),
                                                IdentifierArgumentType.getIdentifier(context, "custom_enchantment"), 1)
                                        ))
                                .then(CommandManager.argument("level", IntegerArgumentType.integer(0))
                                        .executes(context -> CustomEnchantCommand.execute(
                                                (ServerCommandSource)context.getSource(),
                                                EntityArgumentType.getEntities(context, "targets"),
                                                IdentifierArgumentType.getIdentifier(context, "custom_enchantment"),
                                                IntegerArgumentType.getInteger(context, "level")
                                        ))
                                )
                        )
        );
    }

    private static int execute(ServerCommandSource source, Collection<? extends Entity> targets, Identifier id, int level) throws CommandSyntaxException {
        AbstractCustomEnchantment enchantment = WeaponAPI.ENCHANTMENTS.get(id);
        if (level > enchantment.getMaxLevel()) {
            throw FAILED_LEVEL_EXCEPTION.create(level, enchantment.getMaxLevel());
        }
        int i = 0;
        for (Entity entity : targets) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                ItemStack itemStack = livingEntity.getMainHandStack();
                if (!itemStack.isEmpty()) {
                    if (enchantment.isAcceptableItem(itemStack) && CustomEnchantmentHelper.isCompatible(CustomEnchantmentHelper.get(itemStack).keySet(), enchantment)) {
                        CustomEnchantmentHelper.addCustomEnchantment(itemStack,enchantment,level);
                        //itemStack.addEnchantment(enchantment, level);
                        ++i;
                        continue;
                    }
                    if (targets.size() != 1) continue;
                    throw FAILED_INCOMPATIBLE_EXCEPTION.create(itemStack.getItem().getName(itemStack).getString());
                }
                if (targets.size() != 1) continue;
                throw FAILED_ITEMLESS_EXCEPTION.create(livingEntity.getName().getString());
            }
            if (targets.size() != 1) continue;
            throw FAILED_ENTITY_EXCEPTION.create(entity.getName().getString());
        }
        if (i == 0) {
            throw FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(() -> Text.translatable("commands.enchant.success.single", enchantment.getName(level), ((Entity)targets.iterator().next()).getDisplayName()), true);
        } else {
            source.sendFeedback(() -> Text.translatable("commands.enchant.success.multiple", enchantment.getName(level), targets.size()), true);
        }
        return i;
    }
}
