package example;

import arc.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

public class UnitStrengthMultiplierPlugin extends Plugin {

    // Store the current strength multipliers for damage and health
    private float damageMultiplier = 1f;
    private float healthMultiplier = 1f;

    // Store the time to wait before increasing the multiplier
    private int timeToWait = 0;

    // Text shown on UI
    private String uiText = "";

    @Override
    public void init() {
        // Add a listener for unit spawn events
        Events.on(UnitSpawnEvent.class, event -> {
            // Multiply the damage and health of the unit by the current multipliers
            event.unit.damage *= damageMultiplier;
            event.unit.maxHealth *= healthMultiplier;
        });

        // Add a listener for the update event
        Events.on(EventType.Trigger.update, () -> {
            // Decrease the time to wait by 1
            timeToWait--;

            // If the time to wait is less than or equal to 0
            if (timeToWait <= 0) {
                // Increase the damage and health multipliers by 0.1
                damageMultiplier += 0.1f;
                healthMultiplier += 0.1f;

                // Reset the time to wait
                timeToWait = 60;
            }

            // Update the UI text
            uiText = "[#00FF00]Damage Multiplier: " + String.format("%.1f", damageMultiplier) + "x\n" +
                     "[#0000FF]Health Multiplier: " + String.format("%.1f", healthMultiplier) + "x";
        });
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        // Add a command that sets the time to wait before increasing the multiplier
        handler.register("unitConfig", "<time>", "Sets the time to wait before increasing the unit multiplier by 0.1x.", args -> {
            // Parse the time argument
            int time = Strings.parseInt(args[0], 0);

            // Set the time to wait
            timeToWait = time;

            // Show a message to the admin
            Call.infoPopup("Time to wait set to " + timeToWait + " seconds.", 5f);
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        // Add a command that shows the current strength multipliers
        handler.<Player>register("unitConfig", "Shows the current unit strength multipliers.", (args, player) -> {
            // Show the UI text to the player
            Call.onInfoToast(player.con, uiText, 10f);
        });
    }

    @Override
    public void registerClientModElements() {
        // Add a UI element to show the current strength multipliers
        BaseDialog dialog = new BaseDialog("Unit Multipliers");
        dialog.cont.add(new Label(() -> uiText)).grow().wrap().pad(10f);
        dialog.addCloseButton();
        Core.scene.dialog.addDialog(dialog);
    }

}
