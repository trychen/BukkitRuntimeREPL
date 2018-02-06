package team.unstudio.bukkitrepl.command;

import javarepl.completion.CommandCompleter;
import javarepl.console.commands.Command;

public abstract class ChattingCommand extends Command {
    public static final class QUIT extends ChattingCommand {
        public QUIT() {
            super(":quit", "exit");
        }
    }
    public static final class SHUT extends ChattingCommand {
        public SHUT() {
            super(":shut", "exit and shutdown");
        }
    }
    public static final class SHUTDOWN extends ChattingCommand {
        public SHUTDOWN() {
            super(":shutdown", "exit and shutdown");
        }
    }
    public static final class CLR extends ChattingCommand {
        public CLR() {
            super(":clr", "clean chat message");
        }
    }
    public static final class SPLITTER extends ChattingCommand {
        public SPLITTER() {
            super(":splitter", "draw a splitter");
        }
    }

    protected ChattingCommand(String command, String description) {
        super(command + " - " + description, it -> false, new CommandCompleter(command));
    }

    @Override
    public void execute(String expression) {
    }
}
