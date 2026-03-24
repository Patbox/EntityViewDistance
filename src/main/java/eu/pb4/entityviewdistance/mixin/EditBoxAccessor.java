package eu.pb4.entityviewdistance.mixin;

import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.gui.components.EditBox.class)
public interface EditBoxAccessor {
    @Accessor("highlightPos")
    int sim_getHighlightPos();

    @Accessor("maxLength")
    int sim_getMaxLength();

    @Accessor("value")
    void sim_setValue(String value);

    @Invoker("onValueChange")
    void sim_onValueChange(final String value);
}