package team.unstudio.bukkitrepl.event;

import javarepl.Result;
import javarepl.console.commands.Command;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerConsoleCreateEvent extends ConsoleCreateEvent {
    private Player player;
    public PlayerConsoleCreateEvent(Player player, List<javarepl.Result> resultList, List<Class<? extends Command>> commandsList) {
        super(resultList, commandsList);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
