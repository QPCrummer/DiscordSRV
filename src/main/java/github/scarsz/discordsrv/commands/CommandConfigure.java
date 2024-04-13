/*
 * DiscordSRV - https://github.com/DiscordSRV/DiscordSRV
 *
 * Copyright (C) 2016 - 2024 Austin "Scarsz" Shapiro
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package github.scarsz.discordsrv.commands;

import github.scarsz.configuralize.DynamicConfig;
import github.scarsz.configuralize.Provider;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.PlayerUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class CommandConfigure {
    @Command(commandNames = { "configure", "cfg" },
            helpMessage = "Alter certain config values in-game",
            permission = "discordsrv.configure",
            usageExample = "configure text_channel <#key?>  <#channel?>"
    )
    public static void execute(CommandSender sender, String[] args) {
        // Common values to change
        Provider configProvider = DiscordSRV.config().getProvider("config");
        switch (args[0]) {
            case "text_channel":
                changeTextChannels(configProvider, args[1], args[2]);
                quickReload();
                break;
            case "voice_channel" :
                changeVoiceChannel(configProvider, args[2]);
                break;
        }
    }

    private static void writeNewValue(Provider provider, String key, Object value) {
        //String previousValue = provider.getConfig().getString(key);
        provider.getConfig().setRuntimeValue(key, value);
    }

    private static void changeTextChannels(Provider provider, String key, String value) {
        DynamicConfig config = provider.getConfig();
        Map<String, String> map = config.getMap("Channels");
        map.put(key, value);
        config.setRuntimeValue("Channels", map);
        quickReload();
    }

    private static void changeVoiceChannel(Provider provider, String value) {
        DynamicConfig config = provider.getConfig();
        config.setRuntimeValue("DiscordVCChannel", value);
        PlayerUtil.getOnlinePlayers().forEach(player -> {
            player.kick(Component.text("You must join the VC channel: " + DiscordUtil.getJda().getVoiceChannelById(value).getName()));
        });
    }

    private static void quickReload() {
        DiscordSRV.getPlugin().reloadChannels();
        if (DiscordSRV.getPlugin().getChannelUpdater() != null) DiscordSRV.getPlugin().getChannelUpdater().reload();
    }
}
