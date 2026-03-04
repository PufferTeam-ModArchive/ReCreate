package su.sergiusonesimus.recreate.foundation.utility.animation;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;

public class ScrollingModelBox extends ModelBox {

    private float texU;
    private float texV;

    private float sizeX;
    private float sizeY;
    private float sizeZ;

    private float offset;
    private boolean decreaseY;

    ModelRenderer renderer;

    public ScrollingModelBox(ModelRenderer renderer, float texU, float texV, float x, float y, float z, float sizeX,
        float sizeY, float sizeZ, float scale) {
        this(renderer, texU, texV, x, y, z, sizeX, sizeY, sizeZ, scale, true);
    }

    public ScrollingModelBox(ModelRenderer renderer, float texU, float texV, float x, float y, float z, float sizeX,
        float sizeY, float sizeZ, float scale, boolean decreaseY) {
        super(renderer, (int) texU, (int) texV, x, y, z, (int) sizeX, (int) sizeY, (int) sizeZ, scale);

        this.renderer = renderer;

        this.texU = texU;
        this.texV = texV;

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;

        this.decreaseY = decreaseY && sizeY > 1;

        setOffset(0);
    }

    public ScrollingModelBox(ModelRenderer renderer, float texU, float texV, float x, float y, float z, float sizeX,
        float sizeY, float sizeZ, float actualSizeX, float actualSizeY, float actualSizeZ, float scale) {
        this(renderer, texU, texV, x, y, z, sizeX, sizeY, sizeZ, actualSizeX, actualSizeY, actualSizeZ, scale, true);
    }

    public ScrollingModelBox(ModelRenderer renderer, float texU, float texV, float x, float y, float z, float sizeX,
        float sizeY, float sizeZ, float actualSizeX, float actualSizeY, float actualSizeZ, float scale,
        boolean decreaseY) {
        super(renderer, (int) texU, (int) texV, x, y, z, (int) sizeX, (int) sizeY, (int) sizeZ, scale);

        this.renderer = renderer;

        this.texU = texU;
        this.texV = texV;

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;

        this.decreaseY = decreaseY && sizeY > 1;

        this.posX2 = x + actualSizeX;
        this.posY2 = y + actualSizeY;
        this.posZ2 = z + actualSizeZ;
        float maxX = x + actualSizeX;
        float maxY = y + actualSizeY;
        float maxZ = z + actualSizeZ;
        x -= scale;
        y -= scale;
        z -= scale;
        maxX += scale;
        maxY += scale;
        maxZ += scale;

        if (renderer.mirror) {
            float f7 = maxX;
            maxX = x;
            x = f7;
        }

        PositionTextureVertex v0 = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
        PositionTextureVertex v1 = new PositionTextureVertex(maxX, y, z, 0.0F, 8.0F);
        PositionTextureVertex v2 = new PositionTextureVertex(maxX, maxY, z, 8.0F, 8.0F);
        PositionTextureVertex v3 = new PositionTextureVertex(x, maxY, z, 8.0F, 0.0F);
        PositionTextureVertex v4 = new PositionTextureVertex(x, y, maxZ, 0.0F, 0.0F);
        PositionTextureVertex v5 = new PositionTextureVertex(maxX, y, maxZ, 0.0F, 8.0F);
        PositionTextureVertex v6 = new PositionTextureVertex(maxX, maxY, maxZ, 8.0F, 8.0F);
        PositionTextureVertex v7 = new PositionTextureVertex(x, maxY, maxZ, 8.0F, 0.0F);
        this.vertexPositions[0] = v0;
        this.vertexPositions[1] = v1;
        this.vertexPositions[2] = v2;
        this.vertexPositions[3] = v3;
        this.vertexPositions[4] = v4;
        this.vertexPositions[5] = v5;
        this.vertexPositions[6] = v6;
        this.vertexPositions[7] = v7;

        setOffset(0);
    }

    public void setOffset(float newOffset) {
        offset = newOffset % (renderer.textureHeight - renderer.textureWidth);

        this.quadList[0] = new ScrollingTexturedQuad(
            new PositionTextureVertex[] { vertexPositions[1], vertexPositions[2], vertexPositions[6],
                vertexPositions[5] },
            texU + sizeY - 1 + sizeX - 1,
            texV + sizeY + offset + (decreaseY ? -1 : 0),
            texU + sizeY - 1 + sizeX - 1 + sizeY,
            texV + sizeY + sizeZ + offset + (decreaseY ? -1 : 0),
            renderer.textureWidth,
            renderer.textureHeight);

        this.quadList[1] = new ScrollingTexturedQuad(
            new PositionTextureVertex[] { vertexPositions[3], vertexPositions[0], vertexPositions[4],
                vertexPositions[7] },
            texU,
            texV + sizeY + offset + (decreaseY ? -1 : 0),
            texU + sizeY,
            texV + sizeY + sizeZ + offset + (decreaseY ? -1 : 0),
            renderer.textureWidth,
            renderer.textureHeight);

        this.quadList[2] = new ScrollingTexturedQuad(
            new PositionTextureVertex[] { vertexPositions[0], vertexPositions[1], vertexPositions[5],
                vertexPositions[4] },
            texU + sizeY - 1,
            texV + sizeY + offset + (decreaseY ? -1 : 0),
            texU + sizeY - 1 + sizeX,
            texV + sizeY + sizeZ + offset + (decreaseY ? -1 : 0),
            renderer.textureWidth,
            renderer.textureHeight);

        this.quadList[3] = new ScrollingTexturedQuad(
            new PositionTextureVertex[] { vertexPositions[2], vertexPositions[3], vertexPositions[7],
                vertexPositions[6] },
            texU + sizeY - 1,
            texV + sizeY + offset + (decreaseY ? -1 : 0),
            texU + sizeY - 1 + sizeX,
            texV + sizeY + sizeZ + offset + (decreaseY ? -1 : 0),
            renderer.textureWidth,
            renderer.textureHeight);

        this.quadList[4] = new ScrollingTexturedQuad(
            new PositionTextureVertex[] { vertexPositions[1], vertexPositions[0], vertexPositions[3],
                vertexPositions[2] },
            texU + sizeY - 1,
            texV + offset,
            texU + sizeY - 1 + sizeX,
            texV + sizeY + offset + (decreaseY ? -1 : 0),
            renderer.textureWidth,
            renderer.textureHeight);

        this.quadList[5] = new ScrollingTexturedQuad(
            new PositionTextureVertex[] { vertexPositions[6], vertexPositions[7], vertexPositions[4],
                vertexPositions[5] },
            texU + sizeY - 1,
            texV + sizeY + sizeZ + offset + (decreaseY ? -1 : 0),
            texU + sizeY - 1 + sizeX,
            texV + sizeY + sizeZ + sizeY + offset + (decreaseY ? -2 : 0),
            renderer.textureWidth,
            renderer.textureHeight);

        if (renderer.mirror) {
            for (int i = 0; i < this.quadList.length; ++i) {
                this.quadList[i].flipFace();
            }
        }

    }

    public float getOffset() {
        return offset;
    }

}
