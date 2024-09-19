package com.linngdu664.bsf.config;

import com.linngdu664.bsf.Main;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ClientConfig extends BSFConfig {
    public static final ClientConfig INSTANCE;
    public static final ModConfigSpec SPEC;
    public static ConfigValueHolder<Double> SCREENSHAKE_INTENSITY = new ConfigValueHolder(Main.MODID, "client/screenshake", (builder) ->
            builder.comment("Intensity of screenshake. Higher numbers increase amplitude. Disable to turn off screenshake.")
                    .defineInRange("screenshake_intensity", 1.0, 0.0, 5.0));

    static {
        Pair<ClientConfig, ModConfigSpec> specPair = (new ModConfigSpec.Builder()).configure(ClientConfig::new);
        SPEC = specPair.getRight();
        INSTANCE = specPair.getLeft();
    }

    public ClientConfig(ModConfigSpec.Builder builder) {
        super(Main.MODID, "client", builder);
    }
}
