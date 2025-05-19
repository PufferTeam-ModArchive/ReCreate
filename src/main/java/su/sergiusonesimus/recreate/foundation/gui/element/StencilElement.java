package su.sergiusonesimus.recreate.foundation.gui.element;

import org.lwjgl.opengl.GL11;

public abstract class StencilElement extends RenderElement {

    @Override
    public void render() {
        GL11.glPushMatrix();
        transform();
        prepareStencil();
        renderStencil();
        prepareElement();
        renderElement();
        cleanUp();
        GL11.glPopMatrix();
    }

    protected abstract void renderStencil();

    protected abstract void renderElement();

    protected void transform() {
        GL11.glTranslatef(x, y, z);
    }

    protected void prepareStencil() {
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(~0);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xFF);
    }

    protected void prepareElement() {
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
    }

    protected void cleanUp() {
        GL11.glDisable(GL11.GL_STENCIL_TEST);

    }
}
