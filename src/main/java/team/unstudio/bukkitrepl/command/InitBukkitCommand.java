package team.unstudio.bukkitrepl.command;

import javarepl.Evaluator;
import javarepl.completion.CommandCompleter;
import javarepl.console.ConsoleLogger;
import javarepl.console.commands.Command;
import team.unstudio.bukkitrepl.BukkitConsoleHelper;

import static javarepl.console.commands.EvaluateExpression.evaluate;

public class InitBukkitCommand extends Command {
    private static final String COMMAND = ":init";
    private final Evaluator evaluator;
    private final ConsoleLogger logger;

    public InitBukkitCommand(Evaluator evaluator, ConsoleLogger logger) {
        super(COMMAND + " - inti classloader to get bukkit's class", COMMAND::equalsIgnoreCase, new CommandCompleter(COMMAND));
        this.evaluator = evaluator;
        this.logger = logger;
    }

    public void execute(String expression) {
        BukkitConsoleHelper.hookEvaluator(evaluator);
        for (String defaultExpression : BukkitConsoleHelper.DEFAULT_EXPRESSIONS) {
            evaluate(evaluator, logger, defaultExpression);
        }
    }
}
