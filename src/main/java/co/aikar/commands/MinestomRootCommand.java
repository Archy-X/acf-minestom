package co.aikar.commands;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import net.minestom.server.command.CommandProcessor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinestomRootCommand implements RootCommand, CommandProcessor {

    private final MinestomCommandManager manager;
    private final String name;
    private BaseCommand defCommand;
    private SetMultimap<String, RegisteredCommand> subCommands = HashMultimap.create();
    private List<BaseCommand> children = new ArrayList<>();
    boolean isRegistered = false;

    MinestomRootCommand(MinestomCommandManager manager, String name) {
        this.manager = manager;
        this.name = name;
    }

    @Override
    public String getDescription() {
        RegisteredCommand command = getDefaultRegisteredCommand();

        if (command != null && !command.getHelpText().isEmpty()) {
            return command.getHelpText();
        }
        if (command != null && command.scope.description != null) {
            return command.scope.description;
        }
        return defCommand.getName();
    }

    @Override
    public String getCommandName() {
        return name;
    }

    @Nullable
    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean process(@NotNull CommandSender sender, @NotNull String command, @NotNull String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase(command)) {
                args = new String[0];
            }
        }
        execute(manager.getCommandIssuer(sender), command, args);
        return true;
    }

    @Override
    public boolean hasAccess(@NotNull Player player) {
        return hasAnyPermission(manager.getCommandIssuer(player));
    }


    @Override
    public boolean enableWritingTracking() {
        return true;
    }

    @Override
    public String[] onWrite(@NotNull CommandSender sender, String text) {
        String[] split = ACFPatterns.SPACE.split(text);
        if (text.endsWith(" ")) {
            split = Arrays.copyOfRange(split, 0, split.length + 1);
            split[split.length - 1] = "";
        }
        return getTabCompletions(manager.getCommandIssuer(sender), split[0].substring(1), Arrays.copyOfRange(split, 1, split.length)).toArray(new String[0]);
    }


    @Override
    public void addChild(BaseCommand command) {
        if (this.defCommand == null || !command.subCommands.get(BaseCommand.DEFAULT).isEmpty()) {
            this.defCommand = command;
        }
        addChildShared(this.children, this.subCommands, command);
    }

    @Override
    public CommandManager getManager() {
        return manager;
    }

    @Override
    public SetMultimap<String, RegisteredCommand> getSubCommands() {
        return this.subCommands;
    }

    @Override
    public List<BaseCommand> getChildren() {
        return children;
    }

    @Override
    public BaseCommand getDefCommand() {
        return defCommand;
    }


}
