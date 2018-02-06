package team.unstudio.bukkitrepl;

import javarepl.console.SimpleConsole;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;
import team.unstudio.udpl.command.anno.AnnoCommandManager;
import team.unstudio.udpl.config.AutoCharsetYamlConfiguration;
import team.unstudio.udpl.i18n.I18n;
import team.unstudio.udpl.i18n.SLangI18n;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BukkitRuntimeREPL extends JavaPlugin {
    private static BukkitRuntimeREPL instance;
    public static boolean ENABLE_SANDBOX;
    public static I18n I18N;
    private static String[] ALLOWED_PLAYER_LIST;

    @Override
    public void onLoad() {
        instance = this;
    }

    public static BukkitRuntimeREPL getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Configuration config = AutoCharsetYamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));

        ENABLE_SANDBOX = config.getBoolean("sandbox", true);
        ALLOWED_PLAYER_LIST = config.getStringList("allowed_player").stream().map(String::toLowerCase).collect(Collectors.toList()).toArray(new String[0]);

        getLogger().info("Loaded configuration successfully");

        I18N = SLangI18n.fromClassLoader(getClassLoader(), "general.slang");

        getLogger().info("Loaded language successfully " + I18N.getDefaultLocale());

        AnnoCommandManager.builder().name("repl").plugin(this).build().addHandler(new CommandREPL()).registerCommand();

        ConsoleManager.INSTANCE.init();

        getLogger().info("Registered listener successfully");

        SimpleConsole console = new SimpleConsole(BukkitConsoleHelper.createDefaultConfig());
        console.start();
        console.execute("import team.unstudio.bukkitrepl.BukkitRuntimeREPL;");
        console.execute("BukkitRuntimeREPL.getInstance().getLogger().info(\"Hello REPL!\");");
        console.shutdown();
        getLogger().info("Tested JDK status!");
    }

    public static final boolean isPlayerAllowed(String name) {
        return Arrays.binarySearch(ALLOWED_PLAYER_LIST, name) >= 0;
    }

    public static String i18n(String key, Object... format) {
        return I18N.format(key, format);
    }

    @Override
    public void onDisable() {
        ConsoleManager.INSTANCE.onDisable();
    }
}
