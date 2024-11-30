package eu.pb4.entityviewdistance.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @ModifyExpressionValue(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderers;reloadEntityRenderers(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;)Ljava/util/Map;"))
    private Map<EntityType<?>, EntityRenderer<?, ?>> replaceMap(Map<EntityType<?>, EntityRenderer<?, ?>> original) {
        return new IdentityHashMap<>(original);
    }

    @ModifyExpressionValue(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderers;reloadPlayerRenderers(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;)Ljava/util/Map;"))
    private Map<SkinTextures.Model, EntityRenderer<? extends PlayerEntity, ?>> replaceMap2(Map<SkinTextures.Model, EntityRenderer<? extends PlayerEntity, ?>> original) {
        return new IdentityHashMap<>(original);
    }
}
