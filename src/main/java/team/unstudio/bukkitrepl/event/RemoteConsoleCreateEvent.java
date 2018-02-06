package team.unstudio.bukkitrepl.event;

import javarepl.console.commands.Command;

import java.util.List;

public class RemoteConsoleCreateEvent extends ConsoleCreateEvent {
    private int port;

    public RemoteConsoleCreateEvent(int port, List<javarepl.Result> resultList, List<Class<? extends Command>> commandsList, List<String> expressions) {
        super(resultList, commandsList, expressions);
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
