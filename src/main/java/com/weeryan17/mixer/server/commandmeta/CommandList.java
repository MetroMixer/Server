package com.weeryan17.mixer.server.commandmeta;

import com.weeryan17.mixer.server.commands.CreateChannelsCommand;
import com.weeryan17.mixer.server.commands.IdentifyCommand;
import com.weeryan17.mixer.server.commands.VolumeCommand;
import com.weeryan17.mixer.shared.command.meta.CommandData;
import com.weeryan17.mixer.shared.command.meta.CommandType;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Locale;

public enum CommandList {

    IDENTIFY(IdentifyCommand.class),
    CREATE_CHANNELS(CreateChannelsCommand.class),
    VOLUME(VolumeCommand.class);

    private String command;
    private Class<? extends Command> javaClass;
    CommandList(String command, Class<? extends Command<?>> javaClass) {
        this.command = command;
        this.javaClass = javaClass;
    }

    CommandList(Class<? extends Command> javaClass) {
        this.command = this.name().toLowerCase(Locale.ROOT);
        this.javaClass = javaClass;
    }

    public static CommandList getByCommand(String command) {
        return Arrays.stream(CommandList.values()).filter(commandType -> commandType.command.equals(command)).findFirst().orElse(null);
    }

    public Command createCommand() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return javaClass.getConstructor().newInstance();
    }

}
