package mcjty.lib.thirteen;

import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ChannelBuilder {
    private ResourceLocation channelName;
    private Supplier<String> networkProtocolVersion;
    private Predicate<String> clientAcceptedVersions;
    private Predicate<String> serverAcceptedVersions;

    public static ChannelBuilder named(ResourceLocation channelName) {
        ChannelBuilder builder = new ChannelBuilder();
        builder.channelName = channelName;
        return builder;
    }

    public ChannelBuilder networkProtocolVersion(Supplier<String> networkProtocolVersion) {
        this.networkProtocolVersion = networkProtocolVersion;
        return this;
    }

    public ChannelBuilder clientAcceptedVersions(Predicate<String> clientAcceptedVersions) {
        this.clientAcceptedVersions = clientAcceptedVersions;
        return this;
    }

    public ChannelBuilder serverAcceptedVersions(Predicate<String> serverAcceptedVersions) {
        this.serverAcceptedVersions = serverAcceptedVersions;
        return this;
    }

    public SimpleChannel simpleChannel() {
        return new SimpleChannel(channelName.getResourcePath());
    }
}
