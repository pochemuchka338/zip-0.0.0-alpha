package net.ip.zip.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SmokeGrenadeParticle extends TextureSheetParticle {
    protected SmokeGrenadeParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        super(level, x, y, z, vx, vy, vz);
        this.setSize(0.8f, 0.8f);
        this.quadSize = 1.5f + level.random.nextFloat() * 1.5f;
        this.lifetime = 120 + level.random.nextInt(60);
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.alpha = 0.85f;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.xd *= 0.98;
        this.zd *= 0.98;
        this.yd *= 0.98;
        if (this.age > this.lifetime - 30) {
            this.alpha = 0.85f * ((float)(this.lifetime - this.age) / 30.0f);
        } else {
            this.alpha = 0.85f;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
            SmokeGrenadeParticle particle = new SmokeGrenadeParticle(level, x, y, z, vx, vy, vz);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}