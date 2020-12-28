package co.aikar.commands;

import co.aikar.commands.apachecommonslang.ApacheCommonsExceptionUtil;
import net.minestom.server.MinecraftServer;
import net.minestom.server.chat.ChatColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

import java.lang.reflect.Method;
import java.util.*;

public class MinestomCommandManager extends CommandManager<
        CommandSender,
        MinestomCommandIssuer,
        ChatColor,
        MinestomMessageFormatter,
        MinestomCommandExecutionContext,
        MinestomConditionContext
        > {


    protected Map<String, Command> knownCommands = new HashMap<>();
    protected Map<String, MinestomRootCommand> registeredCommands = new HashMap<>();
    protected MinestomCommandContexts contexts;
    protected MinestomCommandCompletions completions;
    protected MinestomLocales locales;

    public MinestomCommandManager() {
        this.formatters.put(MessageType.ERROR, defaultFormatter = new MinestomMessageFormatter(ChatColor.RED, ChatColor.YELLOW, ChatColor.RED));
        this.formatters.put(MessageType.SYNTAX, new MinestomMessageFormatter(ChatColor.YELLOW, ChatColor.BRIGHT_GREEN, ChatColor.WHITE));
        this.formatters.put(MessageType.INFO, new MinestomMessageFormatter(ChatColor.BLUE, ChatColor.DARK_GREEN, ChatColor.BRIGHT_GREEN));
        this.formatters.put(MessageType.HELP, new MinestomMessageFormatter(ChatColor.CYAN, ChatColor.BRIGHT_GREEN, ChatColor.YELLOW));
        getLocales();
    }

    @Override
    public boolean isCommandIssuer(Class<?> type) {
        return CommandSender.class.isAssignableFrom(type);
    }

    @Override
    public synchronized CommandContexts<MinestomCommandExecutionContext> getCommandContexts() {
        if (this.contexts == null) {
            this.contexts = new MinestomCommandContexts(this);
        }
        return contexts;
    }

    @Override
    public synchronized CommandCompletions<?> getCommandCompletions() {
        if (this.completions == null) {
            this.completions = new MinestomCommandCompletions(this);
        }
        return completions;
    }

    @Override
    public Locales getLocales() {
        if (this.locales == null) {
            this.locales = new MinestomLocales(this);
            this.locales.loadLanguages();
        }
        return locales;
    }

    @Override
    public boolean hasRegisteredCommands() {
        return !registeredCommands.isEmpty();
    }

    @Override
    public void registerCommand(BaseCommand command) {
        command.onRegister(this);
        for (Map.Entry<String, RootCommand> entry : command.registeredCommands.entrySet()) {
            String commandName = entry.getKey().toLowerCase(Locale.ENGLISH);
            MinestomRootCommand minestomCommand = (MinestomRootCommand) entry.getValue();
            if (!minestomCommand.isRegistered) {
                MinecraftServer.getCommandManager().register(minestomCommand);
            }
            minestomCommand.isRegistered = true;
            registeredCommands.put(commandName, minestomCommand);
        }
    }

    public void unregisterCommand(BaseCommand command) {
        for (RootCommand rootcommand : command.registeredCommands.values()) {
            MinestomRootCommand minestomCommand = (MinestomRootCommand) rootcommand;
            minestomCommand.getSubCommands().values().removeAll(command.subCommands.values());
            if (minestomCommand.isRegistered && minestomCommand.getSubCommands().isEmpty()) {
                unregisterCommand(minestomCommand);
                minestomCommand.isRegistered = false;
            }
        }
    }

    public void unregisterCommand(MinestomRootCommand command) {
        Command minestomCommand = MinecraftServer.getCommandManager().getCommand(command.getCommandName());
        if (minestomCommand != null) {
            MinecraftServer.getCommandManager().unregister(minestomCommand);
            String key = command.getCommandName();
            Command registered = knownCommands.get(key);
            if (command.equals(registered)) {
                knownCommands.remove(key);
            }
            registeredCommands.remove(key);
        }
    }

    public void unregisterCommands() {
        for (String key : new HashSet<>(registeredCommands.keySet())) {
            unregisterCommand(registeredCommands.get(key));
        }
    }

    public Locale setPlayerLocale(Player player, Locale locale) {
        return this.setIssuerLocale(player, locale);
    }

    @Override
    public RootCommand createRootCommand(String cmd) {
        return new MinestomRootCommand(this, cmd);
    }

    @Override
    public Collection<RootCommand> getRegisteredRootCommands() {
        return Collections.unmodifiableCollection(registeredCommands.values());
    }

    @Override
    public MinestomCommandIssuer getCommandIssuer(Object issuer) {
        if (!(issuer instanceof CommandSender)) {
            throw new IllegalArgumentException(issuer.getClass().getName() + " is not a Command Issuer.");
        }
        return new MinestomCommandIssuer(this, (CommandSender) issuer);
    }

    @Override
    public MinestomCommandExecutionContext createCommandContext(RegisteredCommand command, CommandParameter parameter, CommandIssuer sender, List<String> args, int i, Map<String, Object> passedArgs) {
        return new MinestomCommandExecutionContext(command, parameter, (MinestomCommandIssuer) sender, args, i, passedArgs);
    }

    @Override
    public MinestomCommandCompletionContext createCompletionContext(RegisteredCommand command, CommandIssuer sender, String input, String config, String[] args) {
        return new MinestomCommandCompletionContext(command, (MinestomCommandIssuer) sender, input, config, args);
    }

    @Override
    public RegisteredCommand createRegisteredCommand(BaseCommand command, String cmdName, Method method, String prefSubCommand) {
        return new MinestomRegisteredCommand(command, cmdName, method, prefSubCommand);
    }

    @Override
    public MinestomConditionContext createConditionContext(CommandIssuer issuer, String config) {
        return new MinestomConditionContext((MinestomCommandIssuer) issuer, config);
    }

    @Override
    public void log(LogLevel level, String message, Throwable throwable) {
        if (level == LogLevel.INFO) {
            MinecraftServer.LOGGER.info(LogLevel.LOG_PREFIX + message);
        } else if (level == LogLevel.ERROR) {
            MinecraftServer.LOGGER.error(LogLevel.LOG_PREFIX + message);
        }
        if (throwable != null) {
            for (String line : ACFPatterns.NEWLINE.split(ApacheCommonsExceptionUtil.getFullStackTrace(throwable))) {
                if (level == LogLevel.INFO) {
                    MinecraftServer.LOGGER.info(LogLevel.LOG_PREFIX + line);
                } else if (level == LogLevel.ERROR) {
                    MinecraftServer.LOGGER.error(LogLevel.LOG_PREFIX + line);
                }
            }
        }
    }

    @Override
    public String getCommandPrefix(CommandIssuer issuer) {
        return issuer.isPlayer() ? "/" : "";
    }

}
