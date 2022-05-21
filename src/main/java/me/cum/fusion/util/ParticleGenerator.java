
package me.cum.fusion.util;

import java.util.*;
import org.lwjgl.opengl.*;
import net.minecraft.util.math.*;

public class ParticleGenerator
{
    private final int width;
    private final int height;
    private final ArrayList<Particle> particles;
    int state;
    int a;
    int r;
    int g;
    int b;
    
    public ParticleGenerator(final int count, final int width, final int height) {
        this.particles = new ArrayList<Particle>();
        this.state = 0;
        this.a = 255;
        this.r = 255;
        this.g = 255;
        this.b = 255;
        this.width = width;
        this.height = height;
        for (int i = 0; i < count; ++i) {
            final Random random = new Random();
            this.particles.add(new Particle(random.nextInt(width), random.nextInt(height)));
        }
    }
    
    public void drawParticles(final int mouseX, final int mouseY) {
        for (final Particle p : this.particles) {
            if (p.reset) {
                p.resetPosSize();
                p.reset = false;
            }
            p.draw(mouseX, mouseY);
        }
    }
    
    public static void drawBorderedCircle(int x, int y, float radius, final int outsideC, final int insideC) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        final float scale = 0.1f;
        GL11.glScalef(scale, scale, scale);
        x *= (int)(1.0f / scale);
        y *= (int)(1.0f / scale);
        radius *= 1.0f / scale;
        drawCircle(x, y, radius, insideC);
        drawUnfilledCircle(x, y, radius, 1.0f, outsideC);
        GL11.glScalef(1.0f / scale, 1.0f / scale, 1.0f / scale);
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    
    public static void drawCircle(final int x, final int y, final float radius, final int color) {
        final float alpha = (color >> 24 & 0xFF) / 255.0f;
        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(9);
        for (int i = 0; i <= 360; ++i) {
            GL11.glVertex2d(x + Math.sin(i * 3.141526 / 180.0) * radius, y + Math.cos(i * 3.141526 / 180.0) * radius);
        }
        GL11.glEnd();
    }
    
    public static void drawUnfilledCircle(final int x, final int y, final float radius, final float lineWidth, final int color) {
        final float alpha = (color >> 24 & 0xFF) / 255.0f;
        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(2848);
        GL11.glBegin(2);
        for (int i = 0; i <= 360; ++i) {
            GL11.glVertex2d(x + Math.sin(i * 3.141526 / 180.0) * radius, y + Math.cos(i * 3.141526 / 180.0) * radius);
        }
        GL11.glEnd();
        GL11.glDisable(2848);
    }
    
    public static double distance(final float x, final float y, final float x1, final float y1) {
        return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
    }
    
    public class Particle
    {
        private int x;
        private int y;
        private int k;
        private float size;
        private boolean reset;
        private final Random random;
        
        public Particle(final int x, final int y) {
            this.random = new Random();
            this.x = x;
            this.y = y;
            this.size = this.genRandom(1.0f, 3.0f);
        }
        
        public void draw(final int mouseX, final int mouseY) {
            if (this.size <= 0.0f) {
                this.reset = true;
            }
            this.size -= 0.05f;
            ++this.k;
            final int xx = (int)(MathHelper.cos(0.1f * (this.x + this.k)) * 10.0f);
            final int yy = (int)(MathHelper.cos(0.1f * (this.y + this.k)) * 10.0f);
            ParticleGenerator.drawBorderedCircle(this.x + xx, this.y + yy, this.size, 0, 553648127);
            final float distance = (float)ParticleGenerator.distance((float)(this.x + xx), (float)(this.y + yy), (float)mouseX, (float)mouseY);
            if (distance < 50.0f) {
                final float alpha1 = Math.min(1.0f, Math.min(1.0f, 1.0f - distance / 50.0f));
                GL11.glEnable(2848);
                GL11.glDisable(2929);
                GL11.glColor4f(255.0f, 255.0f, 255.0f, 255.0f);
                GL11.glDisable(3553);
                GL11.glDepthMask(false);
                GL11.glBlendFunc(770, 771);
                GL11.glEnable(3042);
                GL11.glLineWidth(0.1f);
                GL11.glBegin(1);
                GL11.glVertex2f((float)(this.x + xx), (float)(this.y + yy));
                GL11.glVertex2f((float)mouseX, (float)mouseY);
                GL11.glEnd();
            }
        }
        
        public void resetPosSize() {
            this.x = this.random.nextInt(ParticleGenerator.this.width);
            this.y = this.random.nextInt(ParticleGenerator.this.height);
            this.size = this.genRandom(1.0f, 3.0f);
        }
        
        public float genRandom(final float min, final float max) {
            return (float)(min + Math.random() * (max - min + 1.0f));
        }
    }
}
