package team.unstudio.bukkitrepl.event;

import javarepl.console.commands.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public abstract class ConsoleCreateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private List<javarepl.Result> results;
    private List<Class<? extends Command>> commandsList;

    public ConsoleCreateEvent(List<javarepl.Result> resultList, List<Class<? extends Command>> commandsList) {
        this.results = results;
        this.commandsList = commandsList;
    }

    public List<javarepl.Result> getResults() {
        return results;
    }

    public List<Class<? extends Command>> getCommandsList() {
        return commandsList;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
