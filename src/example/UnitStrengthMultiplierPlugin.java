package example;

import arc.Events;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.mod.Plugin;

public class UnitStrengthMultiplierPlugin extends Plugin {

    private float damageMultiplier = 2.0f;
    private float timeToWait = 10.0f;

    @Override
    public void registerClientCommands(CommandHandler handler){
        handler.register("multiply-damage", "Sets the damage multiplier for units.", (args) -> {
            if(args.length == 0){
                Call.infoPopup("Usage: /multiply-damage [multiplier]", 5f, null);
                return;
            }

            try {
                float newMultiplier = Float.parseFloat(args[0]);
                damageMultiplier = newMultiplier;
                Call.infoPopup("Damage multiplier set to " + damageMultiplier, 5f, null);
            } catch (NumberFormatException e){
                Call.infoPopup("Invalid multiplier value.", 5f, null);
            }
        });
    }

    @Override
    public void registerServerCommands(CommandHandler handler){
        handler.register("multiply-damage", "Sets the damage multiplier for units.", (args) -> {
            if(args.length == 0){
                Log.info("Usage: /multiply-damage [multiplier]");
                return;
            }

            try {
                float newMultiplier = Float.parseFloat(args[0]);
                damageMultiplier = newMultiplier;
                Log.info("Damage multiplier set to " + damageMultiplier);
            } catch (NumberFormatException e){
                Log.info("Invalid multiplier value.");
            }
        });
    }

    @Override
    public void init(){
        Events.on(UnitSpawnEvent.class, event -> {
            Unit unit = event.unit;
            if(unit.team() != Team.derelict && unit.team() != Team.crux){
                unit.damage(unit.health() * damageMultiplier);
            }
        });

        Events.on(EventType.Trigger.update, () -> {
            if(Mathf.mod(Time.time(), timeToWait) < 0.05f){
                Groups.unit.each(unit -> {
                    if(unit.team() != Team.derelict && unit.team() != Team.crux){
                        unit.damage(unit.health() * (damageMultiplier - 1));
                    }
                });
            }
        });
    }
}
