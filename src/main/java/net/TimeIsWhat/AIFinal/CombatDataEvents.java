package net.TimeIsWhat.AIFinal;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import net.TimeIsWhat.AIFinal.DataLogger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class CombatDataEvents {

    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("event", "player_hurt");
        obj.addProperty("timestamp", System.currentTimeMillis());
        obj.addProperty("damage_taken", event.getAmount());
        obj.addProperty("source", event.getSource().getMsgId());
        obj.addProperty("player_health_after", player.getHealth());

        DataLogger.log(obj);
    }

    @SubscribeEvent
    public void onMobKilled(LivingDeathEvent event) {
        // Only log if the killer is a player
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        LivingEntity mob = event.getEntity();

        JsonObject obj = new JsonObject();
        obj.addProperty("event", "mob_killed");
        obj.addProperty("timestamp", System.currentTimeMillis());
        obj.addProperty("mob_type", mob.getType().toString());
        obj.addProperty("player_health", player.getHealth());

        DataLogger.log(obj);
    }

    private static final Map<UUID, PlayerStats> statsMap = new HashMap<>();
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("myscore")
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                            PlayerStats p_stats = statsMap.computeIfAbsent(player.getUUID(), id -> new PlayerStats());

                            int armorScore = p_stats.get_armorScore();
                            double weaponScore = p_stats.get_weaponScore();
                            double playerScore = p_stats.get_playerScore(); // or your own formula
                            int playerDeaths = p_stats.get_deaths();

                            JsonObject obj = new JsonObject();
                            obj.addProperty("event", "command_used");
                            obj.addProperty("command", "myscore");
                            obj.addProperty("timestamp", System.currentTimeMillis());
                            obj.addProperty("armor_score", armorScore);
                            obj.addProperty("weapon_score", weaponScore);
                            obj.addProperty("player_score", playerScore);
                            obj.addProperty("Deaths", playerDeaths);

                            DataLogger.log(obj);

                            return 1;
                        })
        );

    }
}
