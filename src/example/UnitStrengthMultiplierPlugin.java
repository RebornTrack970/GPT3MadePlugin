package example;

import arc.Events;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Timer;
import mindustry.content.UnitTypes;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.mod.Plugin;

public class UnitStrengthMultiplierPlugin extends Plugin {

    // Constants
    private static final String UI_TEXT_COLOR = "[#FFB6C1]";
    private static final String UI_TEXT_FORMAT = UI_TEXT_COLOR + "Dmg Multiplier: %.1fx\nHP Multiplier: %.1fx";
    private static final float MULTIPLIER_INCREMENT = 0.1f;

    // Variables
    private float damageMultiplier = 1f;
    private float healthMultiplier = 1f;
    private float increaseTime;
    private Timer.Task increaseTask;

    @Override
    public void init() {
        // Register the event listener for updating the UI text
        Events.on(EventType.WorldLoadEvent.class, event -> {
            Call.onInfoToast(UI_TEXT_FORMAT, 10f, () -> String.format(UI_TEXT_FORMAT, damageMultiplier, healthMultiplier));
        });

        // Schedule the unit multiplier increase task if needed
        if (increaseTime > 0) {
            increaseTask = Timer.schedule(() -> {
                damageMultiplier += MULTIPLIER_INCREMENT;
                healthMultiplier += MULTIPLIER_INCREMENT;
                Call.onInfoMessage(UI_TEXT_COLOR + "Unit strength increased by 0.1x!");
            }, increaseTime, increaseTime);
        }
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("unitConfig", "<delay>", "Set the delay before unit strength is increased by 0.1x.", arg -> {
            try {
                float delay = Float.parseFloat(arg);
                increaseTime = delay;
                if (increaseTime > 0) {
                    if (increaseTask != null) increaseTask.cancel();
                    increaseTask = Timer.schedule(() -> {
                        damageMultiplier += MULTIPLIER_INCREMENT;
                        healthMultiplier += MULTIPLIER_INCREMENT;
                        Call.onInfoMessage(UI_TEXT_COLOR + "Unit strength increased by 0.1x!");
                    }, increaseTime, increaseTime);
                    Call.onInfoMessage(UI_TEXT_COLOR + "Unit strength increase scheduled in " + increaseTime + " seconds.");
                } else {
                    Call.onInfoMessage(UI_TEXT_COLOR + "Unit strength increase canceled.");
                }
            } catch (NumberFormatException e) {
                Log.err("Invalid delay specified: " + arg);
            }
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        // No client commands to register
    }

    @Override
    public void registerClientListeners(Seq<ClientModListener> listeners) {
        // No client listeners to register
    }

    @Override
    public void registerServerListeners(Seq<ServerEventListener> listeners) {
        // Register the event listener for unit damage and health
        listeners.add(new ServerEventListener() {
            @Override
            public void unitDamaged(Unit unit, Healthc health, float damageAmount, boolean hit) {
                // Modify the damage amount based on the damage multiplier
                damageAmount *= damageMultiplier;
            }

            @Override
            public void unitHealthChanged(Unit unit, Healthc health, float amount) {
                // Modify the health amount based on the health multiplier
                float maxHealth = unit.health();
                unit.health(amount * healthMultiplier);
                unit.maxHealth(maxHealth * healthMultiplier);
            }
        });
    }
}
