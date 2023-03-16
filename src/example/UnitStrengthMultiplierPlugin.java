package example;

import arc.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;

public class UnitStrengthMultiplierPlugin extends Plugin {
    private float damageMultiplier = 1f;
    private float healthMultiplier = 1f;
    private float timeToWait = 600f;
    private String uiText;

    public void registerClientCommands(CommandHandler handler){
        handler.<Player>register("unitConfig", "<timeInSecondToWaitBeforeIncreaseBy0.1x>", "Set the time to wait before increasing unit damage and health by 0.1x", (args, player) -> {
            timeToWait = Float.parseFloat(args[0]);
            Call.infoPopup("Time to wait set to " + timeToWait + " seconds.", 5f, player);
        });
    }

    public void init(){
        uiText = "DM: [#" + "9b383b" + "]" + String.format("%.1f", damageMultiplier) + "[] HM: [#" + "3a74c5" + "]" + String.format("%.1f", healthMultiplier) + "[]";
        Events.on(UnitSpawnEvent.class, event -> {
            Unit unit = event.unit();
            unit.maxHealth(unit.maxHealth() * healthMultiplier);
            unit.health(unit.health() * healthMultiplier);
            unit.damage(unit.damage() * damageMultiplier);
        });

        Events.on(EventType.Trigger.update, () -> {
            if(Time.time() % timeToWait < 0.05f){
                damageMultiplier += 0.1f;
                healthMultiplier += 0.1f;
                uiText = "DM: [#" + "9b383b" + "]" + String.format("%.1f", damageMultiplier) + "[] HM: [#" + "3a74c5" + "]" + String.format("%.1f", healthMultiplier) + "[]";
            }
        });
    }

    public void registerServerCommands(CommandHandler handler){
    }

    public UnitStrengthMultiplierPlugin(){
    }
}
