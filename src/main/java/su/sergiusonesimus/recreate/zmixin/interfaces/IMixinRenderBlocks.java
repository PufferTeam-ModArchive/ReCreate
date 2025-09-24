package su.sergiusonesimus.recreate.zmixin.interfaces;

public interface IMixinRenderBlocks {

    void setOverrideAlpha(int alpha);

    void setOverrideAlpha(float alpha);

    void clearOverrideAlpha();

    void overrideTextureBlockBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);

    void clearTextureBlockBounds();

}
