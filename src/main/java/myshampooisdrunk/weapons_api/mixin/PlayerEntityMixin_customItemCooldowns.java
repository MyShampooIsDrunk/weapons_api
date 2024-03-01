package myshampooisdrunk.weapons_api.mixin;

import com.mojang.authlib.GameProfile;
import myshampooisdrunk.weapons_api.cooldown.CustomItemCooldownManager;
import myshampooisdrunk.weapons_api.cooldown.CustomItemCooldownManagerI;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerEntity.class,priority = Integer.MAX_VALUE-10)
public abstract class PlayerEntityMixin_customItemCooldowns extends LivingEntity implements CustomItemCooldownManagerI {
    private final CustomItemCooldownManager manager = new CustomItemCooldownManager();
//    @Inject(at=@At("RETURN"),method="<init>")
//    private void onInit(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci){
//        manager = new CustomItemCooldownManager();
//    }
    @Override
    public CustomItemCooldownManager getCustomItemCooldownManager(){
        return manager;
    }
    @Inject(at=@At(value="INVOKE",target = "Lnet/minecraft/entity/player/ItemCooldownManager;update()V"),method="tick")
    public void updateCustomItemMan(CallbackInfo ci){
        manager.update();
    }

    protected PlayerEntityMixin_customItemCooldowns(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
}
//@Mixin(value = MinecraftServer.class,priority = Integer.MAX_VALUE-10)
//public abstract class MinecraftServerMixin extends ReentrantThreadExecutor<ServerTask> implements ServerChunkTickManagerInterface {
//
//    private ServerChunkTickManager serverChunkTickManager;
//
//    public MinecraftServerMixin(String string) {
//        super(string);
//    }
//
//    @Inject(at=@At("RETURN"), method="<init>")
//    private void onInit(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci){
//        serverChunkTickManager = new ServerChunkTickManager((MinecraftServer)(Object)this);
//    }
//    @Override
//    public ServerChunkTickManager getServerChunkTickManager(){
//        return serverChunkTickManager;
//    }
//}
