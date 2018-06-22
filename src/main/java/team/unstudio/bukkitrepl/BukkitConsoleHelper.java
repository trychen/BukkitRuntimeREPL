package team.unstudio.bukkitrepl;

import com.google.common.collect.Lists;
import javarepl.EvaluationClassLoader;
import javarepl.Evaluator;
import javarepl.Result;
import javarepl.console.Console;
import javarepl.console.ConsoleConfig;
import javarepl.console.ConsoleLogger;
import javarepl.console.SimpleConsole;
import javarepl.console.commands.*;
import javarepl.console.rest.RestConsole;
import javarepl.internal.totallylazy.Sequence;
import javarepl.internal.totallylazy.Sequences;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import team.unstudio.bukkitrepl.command.ChattingCommand;
import team.unstudio.bukkitrepl.command.InitBukkitCommand;
import team.unstudio.bukkitrepl.command.LoadPluginCommand;
import team.unstudio.bukkitrepl.event.PlayerConsoleCreateEvent;
import team.unstudio.bukkitrepl.event.RemoteConsoleCreateEvent;
import team.unstudio.udpl.util.reflect.ReflectionUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static javarepl.Result.result;
import static javarepl.console.ConsoleConfig.consoleConfig;
import static team.unstudio.bukkitrepl.BukkitExpression.sync;

public interface BukkitConsoleHelper {
    Sequence<Class<? extends Command>> DEFAULT_COMMANDS = Sequences.<Class<? extends Command>>sequence()
            .append(LoadPluginCommand.class)
            .append(InitBukkitCommand.class)
            .append(EvaluateFile.class)
            .append(AddToClasspath.class)
            .append(LoadSourceFile.class)
            .append(ListValues.class)
            .append(ShowLastSource.class)
            .append(ShowTypeOfExpression.class)
            .append(CheckExpression.class);

    Sequence<Class<? extends Command>> PLAYER_CONSOLE_DEFAULT_COMMANDS = DEFAULT_COMMANDS
            .append(ChattingCommand.QUIT.class)
            .append(ChattingCommand.SHUT.class)
            .append(ChattingCommand.SHUTDOWN.class)
            .append(ChattingCommand.CLR.class)
            .append(ChattingCommand.SPLITTER.class);

    Sequence<String> DEFAULT_EXPRESSIONS = Sequences.<String>sequence()
            .append("import static team.unstudio.bukkitrepl.BukkitExpression.*;")
            .append("import org.bukkit.*;");

    ConsoleConfig BASE_CONFIG = consoleConfig();

    @Nonnull
    static ConsoleConfig createDefaultConfig(@Nonnull Player player) {
        List<Result> resultList = Lists.newArrayList(result("my", player), result("server", Bukkit.getServer()));
        List<Class<? extends Command>> commands = PLAYER_CONSOLE_DEFAULT_COMMANDS.toList();
        List<String> expression = DEFAULT_EXPRESSIONS.toList();

        Bukkit.getPluginManager().callEvent(new PlayerConsoleCreateEvent(player, resultList, commands, expression));
        return BASE_CONFIG.results(resultList.toArray(new Result[0])).commands(commands.toArray(new Class[0])).logger(createLogger(player)).sandboxed(BukkitRuntimeREPL.ENABLE_SANDBOX).expressions(expression.toArray(new String[0]));
    }

    @Nonnull
    static ConsoleConfig createDefaultConfig() {
        return BASE_CONFIG.results(result("server", Bukkit.getServer())).sandboxed(BukkitRuntimeREPL.ENABLE_SANDBOX).commands(DEFAULT_COMMANDS.toArray(new Class[0])).expressions(DEFAULT_EXPRESSIONS.toArray(new String[0]));
    }

    @Nonnull
    static ConsoleConfig createDefaultConfig(int port) {
        List<Result> resultList = Lists.newArrayList(result("server", Bukkit.getServer()));
        List<Class<? extends Command>> commands = DEFAULT_COMMANDS.toList();
        List<String> expression = DEFAULT_EXPRESSIONS.toList();
        Bukkit.getPluginManager().callEvent(new RemoteConsoleCreateEvent(port, resultList, commands, expression));
        return BASE_CONFIG.results(result("server", Bukkit.getServer())).sandboxed(BukkitRuntimeREPL.ENABLE_SANDBOX).results(resultList.toArray(new Result[0])).commands(commands.toArray(new Class[0])).expressions(expression.toArray(new String[0]));
    }

    @Nonnull
    static SimpleConsole createHookedSimpleConsole(@Nonnull ConsoleConfig config) {
        SimpleConsole console = new SimpleConsole(config);
        hookEvaluator(console.context().get(Evaluator.class));
        hookEvaluationClassLoader(console.context().get(EvaluationClassLoader.class));
        return console;
    }

    static void hookEvaluator(Evaluator evaluator) {
        try {
            for (URL url : getNecessaryClasspath()) {
                evaluator.addClasspathUrl(url);
            }
//            EvaluationClassLoader classLoader = (EvaluationClassLoader) ReflectionUtils.getValue(evaluator, true, "classLoader");
//            hookEvaluationClassLoader(classLoader);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    static void hookEvaluationClassLoader(EvaluationClassLoader classLoader) {
        try {
            ReflectionUtils.setValue(classLoader, ClassLoader.class, true, "parent", BukkitRuntimeREPL.class.getClassLoader());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    static URL[] getNecessaryClasspath() {
        try {
            URL[] pluginURLs = (URL[]) ReflectionUtils.invokeMethod(BukkitRuntimeREPL.class.getClassLoader(), URLClassLoader.class, "getURLs", false);
            URL[] bukkitURLs = (URL[]) ReflectionUtils.invokeMethod(Bukkit.class.getClassLoader(), URLClassLoader.class, "getURLs", false);

            return ArrayUtils.addAll(pluginURLs, bukkitURLs);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return new URL[0];
    }

    static void loadFile2Console(@Nonnull EvaluationClassLoader classLoader, @Nonnull File file) throws MalformedURLException {
        classLoader.registerURL(file.toURI().toURL());
    }

    @Nonnull
    static SimpleConsole createPlayerConsole(@Nonnull Player player) {
        return createHookedSimpleConsole(createDefaultConfig(player));
    }

    @Nonnull
    static RestConsole createRemoteConsole(int port) throws Exception {
        return new RestConsole(createHookedSimpleConsole(createDefaultConfig()), port);
    }

    @Nonnull
    static RemoteConsoleHolder holdRemoteConsole(int port) throws Exception {
        return new RemoteConsoleHolder(port);
    }

    @Nonnull
    static ConsoleLogger createLogger(@Nonnull Player player) {
        return new ConsoleLogger(createHookedOutputStream(player, false), createHookedOutputStream(player, true), false);
    }

    @Nonnull
    static PrintStream createHookedOutputStream(@Nonnull Player player, boolean isError) {
        return new HookedPrintScream(player, isError);
    }

    String LINE_SEPARATOR = System.getProperty("line.separator");

    class HookedPrintScream extends PrintStream {
        private Player player;
        private boolean isError;

        HookedPrintScream(@Nonnull Player player, boolean isError) {
            super(new OutputStream() {
                public void write(int b) throws IOException {
                }
            });
            this.player = player;
            this.isError = isError;
        }

        @Override
        public void println(String x) {
            super.println(x);
            String[] str = x.split(LINE_SEPARATOR);

            for (int i = 0; i < str.length; i++) {
                if (isError) {
                    if (i == 0)
                        str[i] = "§c§l[Error] §r§c" + str[i];
                    else
                        str[i] = "§c    |     " + str[i];
                } else {
                    if (i == 0)
                        str[i] = ChatColor.GRAY + "[Output] " + str[i];
                    else
                        str[i] = ChatColor.GRAY + "    |      " + str[i];
                }
            }

            if (isError) {
                sync(() -> player.sendMessage(str));
            } else sync(() -> player.sendMessage(str));
        }
    }

    class RemoteConsoleHolder {
        RestConsole console;
        BukkitTask task;

        public RemoteConsoleHolder(int port) throws Exception {
            console = createRemoteConsole(port);

            task = Bukkit.getScheduler().runTaskAsynchronously(BukkitRuntimeREPL.getInstance(), console::start);
        }

        public BukkitTask getTask() {
            return task;
        }

        public Console getConsole() {
            return console;
        }

        public int getPort() {
            return console.port();
        }

        public void stop() {
            console.shutdown();
            task.cancel();
        }
    }
}