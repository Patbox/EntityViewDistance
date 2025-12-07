package eu.pb4.entityviewdistance.config;

import static eu.pb4.entityviewdistance.EvdUtils.getText;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum EvdOverrideSide {
    BOTH(true, true, getText("side.both"), getText("side.both.desc")),
    CLIENT(true, false, getText("side.client"), getText("side.client.desc")),
    SERVER(false, true, getText("side.server"), getText("side.server.desc")),
    DISABLED(false, false, getText("side.disabled"), getText("side.disabled.desc"));

    public static final Component TOOLTIP;
    public final Component description;
    public final boolean client;
    public final boolean server;
    public final Component displayName;

    EvdOverrideSide(boolean client, boolean server, Component name, Component description) {
        this.client = client;
        this.server = server;
        this.description = description;
        this.displayName = name;
    }

    static {
        var base = Component.empty();

        for (var entry : EvdOverrideSide.values()) {
            base.append(entry.displayName.copy().withStyle(ChatFormatting.GOLD));
            base.append(Component.literal(" - ").withStyle(ChatFormatting.GRAY));
            base.append(entry.description);
            if (entry != DISABLED) {
                base.append("\n");
            }
        }

        TOOLTIP = base;
    }
}
