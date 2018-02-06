package team.unstudio.bukkitrepl.command;

import javarepl.Evaluator;
import javarepl.completion.CommandCompleter;
import javarepl.console.ConsoleLogger;
import javarepl.console.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import team.unstudio.udpl.util.ReflectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadPluginCommand extends Command {
    private static final String COMMAND = ":plugin";
    private final Evaluator evaluator;
    private final ConsoleLogger logger;

    public LoadPluginCommand(Evaluator evaluator, ConsoleLogger logger) {
        super(COMMAND + " - load bukkit plugin to classpath", it -> it.startsWith(COMMAND), new CommandCompleter(COMMAND));
        this.evaluator = evaluator;
        this.logger = logger;
    }

    public void execute(String expression) {
        String[] pluginNames = expression.toLowerCase().replace(COMMAND, "").trim().split(" ");

        List<Plugin> plugins;

        if (pluginNames.length == 0 || pluginNames[0].isEmpty()) plugins = Arrays.asList(Bukkit.getPluginManager().getPlugins());
        else {
            plugins = new ArrayList<>();
            for (String pluginName : pluginNames) {
                Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
                if (plugin == null) logger.error("插件 " + pluginName + " 不存在！");
                else plugins.add(plugin);
            }
        }

        for (Plugin plugin : plugins) {
            File file;
            try {
                file = (File) ReflectionUtils.getValue(plugin, JavaPlugin.class, true, "file");
            } catch (Throwable e) {
                logger.error("获取插件 " + plugin.getName() + " 信息失败！");
                e.printStackTrace();
                continue;
            }

            try {
                evaluator.addClasspathUrl(file.toURI().toURL());
            } catch (Throwable e) {
                logger.error("加载插件 " + plugin.getName() + " 失败！");
                e.printStackTrace();
            }
            logger.success("加载插件 " + plugin.getName() + " 成功！");
        }
    }
}