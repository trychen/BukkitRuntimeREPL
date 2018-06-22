package team.unstudio.bukkitrepl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import team.unstudio.udpl.util.ActionBarUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface BukkitExpression {
    /**
     * Run method in sync bukkit thread
     */
    static <T> Future<T> sync(Callable<T> callable) {
        return Bukkit.getScheduler().callSyncMethod(BukkitRuntimeREPL.getInstance(), callable);
    }

    static void sync(Runnable runnable) {
        Bukkit.getScheduler().callSyncMethod(BukkitRuntimeREPL.getInstance(), () -> {
            runnable.run();
            return null;
        });
    }

    static void asyn(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(BukkitRuntimeREPL.getInstance(), runnable);
    }

    @Nullable
    static Collection<? extends Player> players() {
        return Bukkit.getOnlinePlayers();
    }

    @Nullable
    static Player player(String name) {
        return Bukkit.getPlayer(name);
    }

    @Nullable
    static Player player(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }

    static UUID uuid(String uuid) {
        return UUID.fromString(uuid);
    }

    static void broadcast(String string) {
        Bukkit.broadcastMessage(string);
    }

    static void actionBar(@Nonnull Player player, @Nonnull String string) {
        ActionBarUtils.send(player, string);
    }
}
