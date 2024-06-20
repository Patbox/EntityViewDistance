package eu.pb4.entityviewdistance.config;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static eu.pb4.entityviewdistance.EvdUtils.getText;

public enum EvdOverrideSide {
    BOTH(true, true, getText("side.both"), getText("side.both.desc")),
    CLIENT(true, false, getText("side.client"), getText("side.client.desc")),
    SERVER(false, true, getText("side.server"), getText("side.server.desc")),
    DISABLED(false, false, getText("side.disabled"), getText("side.disabled.desc"));

    public static final Text TOOLTIP;
    public final Text description;
    public final boolean client;
    public final boolean server;
    public final Text displayName;

    EvdOverrideSide(boolean client, boolean server, Text name, Text description) {
        this.client = client;
        this.server = server;
        this.description = description;
        this.displayName = name;
    }

    static {
        var base = Text.empty();

        for (var entry : EvdOverrideSide.values()) {
            base.append(entry.displayName.copy().formatted(Formatting.GOLD));
            base.append(Text.literal(" - ").formatted(Formatting.GRAY));
            base.append(entry.description);
            if (entry != DISABLED) {
                base.append("\n");
            }
        }

        TOOLTIP = base;
    }
}
