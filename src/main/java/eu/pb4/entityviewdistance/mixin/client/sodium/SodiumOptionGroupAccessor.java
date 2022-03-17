package eu.pb4.entityviewdistance.mixin.client.sodium;

import com.google.common.collect.ImmutableList;
import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(OptionGroup.class)
public interface SodiumOptionGroupAccessor {
    @Mutable
    @Accessor
    void setOptions(ImmutableList<Option<?>> options);
}
