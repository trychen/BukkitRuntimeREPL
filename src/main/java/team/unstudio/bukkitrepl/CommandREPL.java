package team.unstudio.bukkitrepl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unstudio.udpl.command.anno.Alias;
import team.unstudio.udpl.command.anno.Command;
import team.unstudio.udpl.command.anno.Optional;
import team.unstudio.udpl.command.anno.Required;

import java.util.stream.Collectors;

import static team.unstudio.bukkitrepl.BukkitExpression.player;
import static team.unstudio.bukkitrepl.BukkitRuntimeREPL.i18n;

public class CommandREPL {

    @Command(value = "remote", permission = "repl.create")
    public void remote(CommandSender sender, @Required(name = "Console Port") int port) {
        if (port >= 0 && port <= 65535) {
            try {
                ConsoleManager.INSTANCE.openConsole(port);
                sender.sendMessage(i18n("§2§l[REPL] §2Created Console Listening To Port %d Successful!", port));
            } catch (Exception e) {
                sender.sendMessage(i18n("§c§l[REPL] §cCreating Console Failed!"));
                e.printStackTrace();
            }
        } else {
            sender.sendMessage(i18n("§c§l[REPL] §cPlease Enter A Current Port (0~65535)!"));
        }
    }

    @Command(value = "chat", permission = "repl.create", senders = Player.class)
    public void chat(Player player) {
        if (BukkitRuntimeREPL.isPlayerAllowed(player.getName().toLowerCase()))
            ConsoleManager.INSTANCE.openConsole(player);
        else player.sendMessage(i18n("§c§l[REPL] §cYou have no permission to create console!"));
    }

    @Command(value = "list", permission = "repl.list")
    public void list(CommandSender sender) {
        sender.sendMessage(i18n("§7§l[Players Opened REPL] §r§7%s", ConsoleManager.INSTANCE.getPlayersCreatedConsole().stream().map(Player::getName).collect(Collectors.joining(", "))));
        sender.sendMessage(i18n("§7§l[Ports Running REPL] §r§7%s", ConsoleManager.INSTANCE.getRemoteConsoles().stream().map(BukkitConsoleHelper.RemoteConsoleHolder::getPort).map(String::valueOf).collect(Collectors.joining(", "))));
    }

    @Command(value = "shut", permission = "repl.shutdown")
    @Alias("shutdown")
    public void shut(CommandSender sender, @Required(name = "Player's Name or Console Port") String playerOrPort) {
        if (!BukkitRuntimeREPL.isPlayerAllowed(sender.getName().toLowerCase())) {
            sender.sendMessage(i18n("§c§l[REPL] §cYou have no permission to shutdown console!"));
            return;
        }

        try {
            Integer port = Integer.valueOf(playerOrPort);

            if (ConsoleManager.INSTANCE.getRemoteConsoles().stream().filter(it -> it.getPort() == port).count() == 0) {
                throw new NullPointerException();
            }

            ConsoleManager.INSTANCE.shutdown(port);
        } catch (NumberFormatException | NullPointerException e) {
            Player player = player(playerOrPort);
            if (player == null || ConsoleManager.INSTANCE.getConsole(player) == null) {
                sender.sendMessage(i18n("§c§l[REPL Manager] §cNo such player or port has console!"));
                return;
            }
            if (player != sender) sender.sendMessage(i18n("§2§l[REPL] §r§2Console has been shutdown!"));
            ConsoleManager.INSTANCE.shutdown(player);
        }
    }

    @Command(value = "help", permission = "repl.help")
    public void help(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(i18n("§3§l[REPL Manager] §r§3start - Start a console on chat"));
        sender.sendMessage(i18n("§3§l[REPL Manager] §r§3start [port] - Start a remote console to port"));
        sender.sendMessage(i18n("§3§l[REPL Manager] §r§3list - Show all console"));
        sender.sendMessage(i18n("§3§l[REPL Manager] §r§3shut [port/playerName] - Stop a remote/player's console"));
    }
}
