package su.sergiusonesimus.recreate;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public enum AllModelTextures {

    SHAFT("axis.png"),
    CREATIVE_MOTOR("creative_motor.png"),
    COGWHEEL("cogwheel.png"),
    LARGE_COGWHEEL("large_cogwheel.png"),
    MECHANICAL_BEARING("mechanical_bearing.png"),

    WRENCH("wrench.png"),

    ;

    public static final String ASSET_PATH = "textures/models/";
    private ResourceLocation location;

    private AllModelTextures(String filename) {
        location = ReCreate.asResource(ASSET_PATH + filename);
    }

    public void bind() {
        Minecraft.getMinecraft().renderEngine.bindTexture(location);
    }

    public ResourceLocation getLocation() {
        return location;
    }

}
