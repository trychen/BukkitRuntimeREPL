package team.unstudio.bukkitrepl.event;

import javarepl.console.commands.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public abstract class ConsoleCreateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private List<javarepl.Result> results;
    private List<Class<? extends Command>> commands;
    private List<String> expressions;

    public ConsoleCreateEvent(List<javarepl.Result> results, List<Class<? extends Command>> commands, List<String> expressions) {
        this.results = results;
        this.commands = commands;
        this.expressions = expressions;
    }

    public List<javarepl.Result> getResults() {
        return results;
    }

    public List<Class<? extends Command>> getCommands() {
        return commands;
    }

    public List<String> getExpressions() {
        return expressions;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
