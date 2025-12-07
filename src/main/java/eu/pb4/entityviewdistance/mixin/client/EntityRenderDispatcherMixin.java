package eu.pb4.entityviewdistance.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @ModifyExpressionValue(method = "onResourceManagerReload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderers;createEntityRenderers(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;)Ljava/util/Map;"))
    private Map<EntityType<?>, EntityRenderer<?, ?>> replaceMap(Map<EntityType<?>, EntityRenderer<?, ?>> original) {
        return new IdentityHashMap<>(original);
    }

    @ModifyExpressionValue(method = "onResourceManagerReload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderers;createAvatarRenderers(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;)Ljava/util/Map;"))
    private Map<PlayerModelType, EntityRenderer<? extends Player, ?>> replaceMap2(Map<PlayerModelType, EntityRenderer<? extends Player, ?>> original) {
        return new IdentityHashMap<>(original);
    }
}
