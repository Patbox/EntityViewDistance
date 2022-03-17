package eu.pb4.entityviewdistance.mixin.client.sodium;

import com.google.common.collect.ImmutableList;
import eu.pb4.entityviewdistance.modcompat.SodiumCompat;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptionPages;
import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Pseudo
@Mixin(SodiumGameOptionPages.class)
public class SodiumGameOptionPagesMixin {
    @Inject(method = "quality", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT, remap = false, require = 0)
    private static void evd_addOption(CallbackInfoReturnable<OptionPage> cir, List<OptionGroup> groups) {
        var list = groups.get(1).getOptions();

        var builder = new ImmutableList.Builder<Option<?>>();

        for (var entry : list) {
            if (!(entry.getName() instanceof TranslatableText translatableText && translatableText.getKey().equals("options.entityDistanceScaling"))) {
                builder.add(entry);
            } else {
                builder.add(SodiumCompat.BUTTON);
            }
        }

        ((SodiumOptionGroupAccessor) groups.get(1)).setOptions(builder.build());
    }
}
