package team.unstudio.bukkitrepl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import javarepl.completion.CompletionCandidate;
import javarepl.completion.CompletionResult;
import javarepl.console.Console;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import team.unstudio.udpl.util.CacheUtils;
import team.unstudio.udpl.util.ChatUtils;
import team.unstudio.udpl.util.PluginUtils;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static team.unstudio.bukkitrepl.BukkitConsoleHelper.*;
import static team.unstudio.bukkitrepl.BukkitExpression.asyn;
import static team.unstudio.bukkitrepl.BukkitExpression.player;
import static team.unstudio.bukkitrepl.BukkitRuntimeREPL.i18n;

public final class ConsoleManager implements Listener{
    public static final ConsoleManager INSTANCE = new ConsoleManager();

    private ConsoleManager(){
    }

    public void init() {
        PluginUtils.registerEvents(this, BukkitRuntimeREPL.getInstance());
        CacheUtils.register(inConsolePlayers);
    }

    private Map<Player, Console> playerConsoles = Maps.newConcurrentMap();
    private Set<Player> inConsolePlayers = Sets.newConcurrentHashSet();

    private Set<RemoteConsoleHolder> remoteConsoles = Sets.newConcurrentHashSet();

    /**
     * Open a console for player
     */
    public void openConsole(@Nonnull Player player) {
        player.sendMessage("");
        Console console = playerConsoles.get(player);
        if (console == null) {
            console = createPlayerConsole(player);
            playerConsoles.put(player, console);
            console.start();
            player.sendMessage(i18n("§2§l[REPL] §r§2Created Console Successful!"));
        }
        if (inConsolePlayers.contains(player)) {
            playerConsoles.remove(player).shutdown();
            inConsolePlayers.remove(player);
            player.sendMessage(i18n("§2§l[REPL] §r§2Console has been shutdown!"));
            return;
        } else inConsolePlayers.add(player);

        player.sendMessage(i18n("§2§l[REPL] §r§2Opened Console, now your can input your code!"));
    }

    /**
     * create a remove console on port
     */
    public void openConsole(int port) throws Exception {
        remoteConsoles.add(holdRemoteConsole(port));
    }

    public boolean isPlayerOpenedConsole(Player player) {
        return inConsolePlayers.contains(player);
    }

    public boolean playerHasConsole(Player player) {
        return playerConsoles.containsKey(player);
    }


    public Console getConsole(Player player) {
        return playerConsoles.get(player);
    }

    public void onDisable() {
        remoteConsoles.forEach(RemoteConsoleHolder::stop);
        playerConsoles.values().forEach(Console::shutdown);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Console console = playerConsoles.remove(event.getPlayer());
        if (console != null) {
            console.shutdown();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (inConsolePlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
            if (event.getMessage().equalsIgnoreCase(":quit")) {
                inConsolePlayers.remove(event.getPlayer());
                event.getPlayer().sendMessage(i18n("§e§l[REPL] §r§eYou've quited console, but it still running in the background!"));
            } else if (event.getMessage().equalsIgnoreCase(":shut") || event.getMessage().equalsIgnoreCase(":shutdown")){
                inConsolePlayers.remove(event.getPlayer());
                Console console = playerConsoles.remove(event.getPlayer());
                console.shutdown();
                event.getPlayer().sendMessage(i18n("§2§l[REPL] §r§2Console has been shutdown!"));
//            } else if (event.getMessage().equalsIgnoreCase(":urls")){
//                try {
//                    SimpleConsole console = (SimpleConsole) playerConsoles.get(event.getPlayer());
//                    EvaluationClassLoader classLoader = console.context().get(EvaluationClassLoader.class);
//                    URL[] urls = (URL[]) ReflectionUtils.invokeMethod(classLoader, URLClassLoader.class, "getURLs");
//                    for (URL url : urls) {
//                        event.getPlayer().sendMessage("[URL] " + url.toString());
//                    }
//                } catch (Throwable e) {
//                    e.printStackTrace();
//                }
            } else if (event.getMessage().equalsIgnoreCase(":clr")) {
                ChatUtils.sendEmpty(event.getPlayer());
            } else if (event.getMessage().equalsIgnoreCase(":splitter")) {
                ChatUtils.sendSplitter(event.getPlayer());
            } else {
                event.getPlayer().sendMessage(ChatColor.BOLD + "[Input] " + event.getMessage());
                asyn(() -> playerConsoles.get(event.getPlayer()).execute(event.getMessage()));
            }
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent event) {
        if (event.getChatMessage().charAt(0) == '\\' || event.getChatMessage().charAt(0) == '/') return;
        if (inConsolePlayers.contains(event.getPlayer())) {
            Console console = playerConsoles.get(event.getPlayer());
            event.getPlayer().sendMessage(i18n("§7[Output] Searching for completion candidate..."));
            CompletionResult result = console.completion(event.getChatMessage());
            event.getTabCompletions().addAll(result.candidates().map(CompletionCandidate::value).toList());
            if (event.getTabCompletions().size() == 0) event.getPlayer().sendMessage(i18n("§7[Output] Couldn't find related completion candidate"));
        }
    }

    public Set<Player> getPlayersCreatedConsole() {
        return playerConsoles.keySet();
    }

    public Set<Integer> getPortsRunningConsole() {
        return remoteConsoles.stream().map(RemoteConsoleHolder::getPort).collect(Collectors.toSet());
    }

    public Set<RemoteConsoleHolder> getRemoteConsoles() {
        return remoteConsoles;
    }

    public void shutdown(Player player) {
        playerConsoles.remove(player).shutdown();
        inConsolePlayers.remove(player);
        player.sendMessage(i18n("§2§l[REPL] §r§2Console has been shutdown!"));
    }

    public void shutdown(int port) {
        remoteConsoles.stream().filter(it -> it.getPort() == port).forEach(RemoteConsoleHolder::stop);
    }
}
