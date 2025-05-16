package su.sergiusonesimus.recreate.content.contraptions.base;

import net.minecraft.block.Block;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import su.sergiusonesimus.recreate.content.contraptions.base.IRotate.SpeedLevel;
import su.sergiusonesimus.recreate.util.Direction.Axis;
import su.sergiusonesimus.recreate.util.VecHelper;

public class KineticEffectHandler {

    int overStressedTime;
    float overStressedEffect;
    int particleSpawnCountdown;
    KineticTileEntity kte;

    public KineticEffectHandler(KineticTileEntity kte) {
        this.kte = kte;
    }

    public void tick() {
        World world = kte.getWorld();

        if (world.isRemote) {
            if (overStressedTime > 0) if (--overStressedTime == 0) if (kte.isOverStressed()) {
                overStressedEffect = 1;
                spawnEffect("smoke", 0.2f, 5);
            } else {
                overStressedEffect = -1;
                spawnEffect("cloud", .075f, 2);
            }

            if (overStressedEffect != 0) {
                overStressedEffect -= overStressedEffect * .1f;
                if (Math.abs(overStressedEffect) < 1 / 128f) overStressedEffect = 0;
            }

        } else if (particleSpawnCountdown > 0) {
            if (--particleSpawnCountdown == 0) spawnRotationIndicators();
        }
    }

    public void queueRotationIndicators() {
        particleSpawnCountdown = 2;
    }

    public void spawnEffect(String particleName, float maxMotion, int amount) {
        World world = kte.getWorld();
        if (world == null) return;
        if (!world.isRemote) return;
        for (int i = 0; i < amount; i++) {
            Vec3 motion = VecHelper.offsetRandomly(Vec3.createVectorHelper(0, 0, 0), world.rand, maxMotion);
            Vec3 position = VecHelper.getCenterOf(kte.xCoord, kte.yCoord, kte.zCoord);
            world.spawnParticle(
                particleName,
                position.xCoord,
                position.yCoord,
                position.zCoord,
                motion.xCoord,
                motion.yCoord,
                motion.zCoord);
        }
    }

    public void spawnRotationIndicators() {
        float speed = kte.getSpeed();
        if (speed == 0) return;

        Block block = kte.getBlockType();
        int meta = kte.getBlockMetadata();
        if (!(block instanceof KineticBlock)) return;

        KineticBlock kb = (KineticBlock) block;
        float radius1 = kb.getParticleInitialRadius();
        float radius2 = kb.getParticleTargetRadius();

        Axis axis = kb.getAxis(meta);
        int posX = kte.xCoord;
        int posY = kte.yCoord;
        int posZ = kte.zCoord;
        World world = kte.getWorld();
        if (axis == null) return;
        if (world == null) return;

        char axisChar = axis.toString()
            .charAt(0);
        Vec3 vec = VecHelper.getCenterOf(posX, posY, posZ);
        SpeedLevel speedLevel = SpeedLevel.of(speed);
        int color = speedLevel.getColor();
        int particleSpeed = speedLevel.getParticleSpeed();
        particleSpeed *= Math.signum(speed);

        if (world instanceof WorldServer) {
            // AllTriggers.triggerForNearbyPlayers(AllTriggers.ROTATION, world, pos, 5);
            // TODO Spawn rotation indicators
            // RotationIndicatorParticleData particleData =
            // new RotationIndicatorParticleData(color, particleSpeed, radius1, radius2, 10, axisChar);
            // ((WorldServer) world).sendParticles(particleData, vec.x, vec.y, vec.z, 20, 0, 0, 0, 1);
        }
    }

    public void triggerOverStressedEffect() {
        overStressedTime = overStressedTime == 0 ? 2 : 0;
    }

}
