package su.sergiusonesimus.recreate.content.contraptions.components.structureMovement;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Direction.Axis;
import su.sergiusonesimus.recreate.foundation.utility.AngleHelper;
import su.sergiusonesimus.recreate.foundation.utility.NBTHelper;
import su.sergiusonesimus.recreate.util.VecHelper;

/**
 * Ex: Minecarts, Couplings <br>
 * Oriented contraptions can rotate freely around two axes
 * simultaneously.
 */
public abstract class OrientedContraption extends Contraption {

    // TODO
    // private static final Ingredient FUEL_ITEMS = Ingredient.of(Items.COAL, Items.CHARCOAL);

    private Optional<UUID> coupling;
    private Direction initialOrientation;

    protected Vec3 motionBeforeStall;
    protected boolean forceAngle;
    private boolean isSerializingFurnaceCart;
    private boolean attachedExtraInventories;
    private boolean manuallyPlaced;

    public float prevYaw;
    public float yaw;
    public float targetYaw;

    public float prevPitch;
    public float pitch;

    public OrientedContraption() {
        super();
    }

    public OrientedContraption(World world) {
        super(world);
        motionBeforeStall = VecHelper.ZERO;
        attachedExtraInventories = false;
        isSerializingFurnaceCart = false;
    }

    public OrientedContraption(World world, Direction initialOrientation) {
        this(world);
        setInitialOrientation(initialOrientation);
        startAtInitialYaw();
    }

    public OrientedContraption(World world, Direction initialOrientation, float initialYaw) {
        this(world, initialOrientation);
        startAtYaw(initialYaw);
        manuallyPlaced = true;
    }

    public void setInitialOrientation(Direction direction) {
        initialOrientation = direction;
    }

    public Direction getInitialOrientation() {
        return initialOrientation;
    }

    // TODO Leftover entity method. Might have to delete
    // @Override
    // public float getYawOffset() {
    // return getInitialYaw();
    // }

    public float getInitialYaw() {
        return (isInitialOrientationPresent() ? initialOrientation : Direction.SOUTH).toYRot();
    }

    // TODO
    // @Override
    // protected void defineSynchedData() {
    // super.defineSynchedData();
    // entityData.define(COUPLING, Optional.empty());
    // entityData.define(INITIAL_ORIENTATION, Direction.UP);
    // }

    // TODO
    // @Override
    // public ContraptionRotationState getRotationState() {
    // ContraptionRotationState crs = new ContraptionRotationState();
    //
    // float yawOffset = getYawOffset();
    // crs.zRotation = pitch;
    // crs.yRotation = -yaw + yawOffset;
    //
    // if (pitch != 0 && yaw != 0) {
    // crs.secondYRotation = -yaw;
    // crs.yRotation = yawOffset;
    // }
    //
    // return crs;
    // }

    @Override
    public void readNBT(NBTTagCompound compound, boolean spawnPacket) {
        super.readNBT(compound, spawnPacket);

        if (compound.hasKey("InitialOrientation"))
            setInitialOrientation(NBTHelper.readEnum(compound, "InitialOrientation", Direction.class));

        yaw = compound.getFloat("Yaw");
        pitch = compound.getFloat("Pitch");
        manuallyPlaced = compound.getBoolean("Placed");

        if (compound.hasKey("ForceYaw")) startAtYaw(compound.getFloat("ForceYaw"));

        // TODO
        // NBTTagList vecNBT = compound.getTagList("CachedMotion", 6);
        // if (vecNBT.tagCount() != 0) {
        // motionBeforeStall = Vec3.createVectorHelper(
        // vecNBT.func_150309_d(0),
        // vecNBT.func_150309_d(1),
        // vecNBT.func_150309_d(2));
        // if (motionBeforeStall.xCoord != 0 || motionBeforeStall.yCoord != 0 || motionBeforeStall.zCoord != 0)
        // targetYaw = prevYaw = yaw += yawFromVector(motionBeforeStall);
        // setDeltaMovement(VecHelper.ZERO);
        // }

        // TODO
        // setCouplingId(compound.hasKey("OnCoupling") ? compound.getUUID("OnCoupling") : null);
    }

    @Override
    public NBTTagCompound writeNBT(boolean spawnPacket) {
        NBTTagCompound compound = super.writeNBT(spawnPacket);

        // TODO
        // if (motionBeforeStall != null)
        // compound.put("CachedMotion",
        // newDoubleList(motionBeforeStall.xCoord, motionBeforeStall.yCoord, motionBeforeStall.zCoord));

        // TODO
        // Direction optional = initialOrientation;
        // if (optional.getAxis()
        // .isHorizontal()) NBTHelper.writeEnum(compound, "InitialOrientation", optional);
        // if (forceAngle) {
        // compound.setFloat("ForceYaw", yaw);
        // forceAngle = false;
        // }

        compound.setBoolean("Placed", manuallyPlaced);
        compound.setFloat("Yaw", yaw);
        compound.setFloat("Pitch", pitch);

        // TODO
        // if (getCouplingId() != null)
        // compound.putUUID("OnCoupling", getCouplingId());
        return compound;
    }

    // TODO
    // @Override
    // public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
    // super.onSyncedDataUpdated(key);
    // if (key == INITIAL_ORIENTATION && isInitialOrientationPresent() && !manuallyPlaced)
    // startAtInitialYaw();
    // }

    public boolean isInitialOrientationPresent() {
        return initialOrientation.getAxis()
            .isHorizontal();
    }

    public void startAtInitialYaw() {
        startAtYaw(getInitialYaw());
    }

    public void startAtYaw(float yaw) {
        targetYaw = this.yaw = prevYaw = yaw;
        forceAngle = true;
    }

    @Override
    public Vec3 applyRotation(Vec3 localPos, float partialTicks) {
        localPos = VecHelper.rotate(localPos, getInitialYaw(), Axis.Y);
        localPos = VecHelper.rotate(localPos, getViewXRot(partialTicks), Axis.Z);
        localPos = VecHelper.rotate(localPos, getViewYRot(partialTicks), Axis.Y);
        return localPos;
    }

    @Override
    public Vec3 reverseRotation(Vec3 localPos, float partialTicks) {
        localPos = VecHelper.rotate(localPos, -getViewYRot(partialTicks), Axis.Y);
        localPos = VecHelper.rotate(localPos, -getViewXRot(partialTicks), Axis.Z);
        localPos = VecHelper.rotate(localPos, -getInitialYaw(), Axis.Y);
        return localPos;
    }

    public float getViewYRot(float partialTicks) {
        return -(partialTicks == 1.0F ? yaw : AngleHelper.angleLerp(partialTicks, prevYaw, yaw));
    }

    public float getViewXRot(float partialTicks) {
        return partialTicks == 1.0F ? pitch : AngleHelper.angleLerp(partialTicks, prevPitch, pitch);
    }

    @Override
    public void tick() {
        super.tick();
        // TODO
        // Entity e = getVehicle();
        // if (e == null) return;
        //
        // boolean rotationLock = false;
        // boolean pauseWhileRotating = false;
        // boolean wasStalled = stalled;
        // if (this instanceof MountedContraption) {
        // MountedContraption mountedContraption = (MountedContraption) contraption;
        // rotationLock = mountedContraption.rotationMode == CartMovementMode.ROTATION_LOCKED;
        // pauseWhileRotating = mountedContraption.rotationMode == CartMovementMode.ROTATE_PAUSED;
        // }
        //
        // Entity riding = e;
        // while (riding.getVehicle() != null && !(this instanceof StabilizedContraption))
        // riding = riding.getVehicle();
        //
        // boolean isOnCoupling = false;
        // UUID couplingId = getCouplingId();
        // isOnCoupling = couplingId != null && riding instanceof EntityMinecart;
        //
        // if (!attachedExtraInventories) {
        // attachInventoriesFromRidingCarts(riding, isOnCoupling, couplingId);
        // attachedExtraInventories = true;
        // }
        //
        // boolean rotating = updateOrientation(rotationLock, wasStalled, riding, isOnCoupling);
        // if (!rotating || !pauseWhileRotating)
        // tickActors();
        // boolean isStalled = stalled;
        //
        // LazyOptional<MinecartController> capability =
        // riding.getCapability(CapabilityMinecartController.MINECART_CONTROLLER_CAPABILITY);
        // if (capability.isPresent()) {
        // if (!level.isClientSide())
        // capability.orElse(null)
        // .setStalledExternally(isStalled);
        // } else {
        // if (isStalled) {
        // if (!wasStalled)
        // motionBeforeStall = riding.getDeltaMovement();
        // riding.setDeltaMovement(0, 0, 0);
        // }
        // if (wasStalled && !isStalled) {
        // riding.setDeltaMovement(motionBeforeStall);
        // motionBeforeStall = VecHelper.ZERO;
        // }
        // }
        //
        // if (parentWorld.isRemote) return;
        //
        // if (!stalled) {
        // if (isOnCoupling) {
        // Couple<MinecartController> coupledCarts = getCoupledCartsIfPresent();
        // if (coupledCarts == null)
        // return;
        // coupledCarts.map(MinecartController::cart)
        // .forEach(this::powerFurnaceCartWithFuelFromStorage);
        // return;
        // }
        // powerFurnaceCartWithFuelFromStorage(riding);
        // }
    }

    // TODO
    // protected boolean updateOrientation(boolean rotationLock, boolean wasStalled, Entity riding, boolean
    // isOnCoupling) {
    // if (isOnCoupling) {
    // Couple<MinecartController> coupledCarts = getCoupledCartsIfPresent();
    // if (coupledCarts == null)
    // return false;
    //
    // Vec3 positionVec = coupledCarts.getFirst()
    // .cart()
    // .position();
    // Vec3 coupledVec = coupledCarts.getSecond()
    // .cart()
    // .position();
    //
    // double diffX = positionVec.x - coupledVec.x;
    // double diffY = positionVec.y - coupledVec.y;
    // double diffZ = positionVec.z - coupledVec.z;
    //
    // prevYaw = yaw;
    // prevPitch = pitch;
    // yaw = (float) (Math.atan2(diffZ, diffX) * 180 / Math.PI);
    // pitch = (float) (Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ)) * 180 / Math.PI);
    //
    // if (getCouplingId().equals(riding.getUUID())) {
    // pitch *= -1;
    // yaw += 180;
    // }
    // return false;
    // }
    //
    // if (this instanceof StabilizedContraption) {
    // if (!(riding instanceof OrientedContraption))
    // return false;
    // StabilizedContraption stabilized = (StabilizedContraption) contraption;
    // Direction facing = stabilized.getFacing();
    // if (facing.getAxis()
    // .isVertical())
    // return false;
    // OrientedContraption parent = (OrientedContraption) riding;
    // prevYaw = yaw;
    // yaw = -parent.getViewYRot(1);
    // return false;
    // }
    //
    // prevYaw = yaw;
    // if (wasStalled)
    // return false;
    //
    // boolean rotating = false;
    // Vec3 movementVector = riding.getDeltaMovement();
    // Vec3 locationDiff = riding.position()
    // .subtract(riding.xo, riding.yo, riding.zo);
    // if (!(riding instanceof EntityMinecart))
    // movementVector = locationDiff;
    // Vec3 motion = movementVector.normalize();
    //
    // if (!rotationLock) {
    // if (riding instanceof EntityMinecart) {
    // EntityMinecart minecartEntity = (EntityMinecart) riding;
    // BlockPos railPosition = minecartEntity.getCurrentRailPosition();
    // BlockState blockState = level.getBlockState(railPosition);
    // if (blockState.getBlock() instanceof BlockRailBase abstractRailBlock) {
    // RailShape railDirection =
    // abstractRailBlock.getRailDirection(blockState, level, railPosition, minecartEntity);
    // motion = VecHelper.project(motion, MinecartSim2020.getRailVec(railDirection));
    // }
    // }
    //
    // if (motion.length() > 0) {
    // targetYaw = yawFromVector(motion);
    // if (targetYaw < 0)
    // targetYaw += 360;
    // if (yaw < 0)
    // yaw += 360;
    // }
    //
    // prevYaw = yaw;
    // float maxApproachSpeed = (float) (motion.length() * 12f / (Math.max(1, getBoundingBox().getXsize() / 6f)));
    // float yawHint = AngleHelper.getShortestAngleDiff(yaw, yawFromVector(locationDiff));
    // float approach = AngleHelper.getShortestAngleDiff(yaw, targetYaw, yawHint);
    // approach = Math.clamp(approach, -maxApproachSpeed, maxApproachSpeed);
    // yaw += approach;
    // if (Math.abs(AngleHelper.getShortestAngleDiff(yaw, targetYaw)) < 1f)
    // yaw = targetYaw;
    // else
    // rotating = true;
    // }
    // return rotating;
    // }

    // TODO
    // protected void powerFurnaceCartWithFuelFromStorage(Entity riding) {
    // if (!(riding instanceof EntityMinecartFurnace furnaceCart)) return;
    //
    // // Notify to not trigger serialization side-effects
    // isSerializingFurnaceCart = true;
    // NBTTagCompound nbt = furnaceCart.serializeNBT();
    // isSerializingFurnaceCart = false;
    //
    // int fuel = nbt.getInt("Fuel");
    // int fuelBefore = fuel;
    // double pushX = nbt.getDouble("PushX");
    // double pushZ = nbt.getDouble("PushZ");
    //
    // int i = Math.floor(furnaceCart.getX());
    // int j = Math.floor(furnaceCart.getY());
    // int k = Math.floor(furnaceCart.getZ());
    // if (furnaceCart.level.getBlockState(new BlockPos(i, j - 1, k))
    // .is(BlockTags.RAILS))
    // --j;
    //
    // BlockPos blockpos = new BlockPos(i, j, k);
    // BlockState blockstate = this.level.getBlockState(blockpos);
    // if (furnaceCart.canUseRail() && blockstate.is(BlockTags.RAILS))
    // if (fuel > 1)
    // riding.setDeltaMovement(riding.getDeltaMovement()
    // .normalize()
    // .scale(1));
    // if (fuel < 5 && contraption != null) {
    // ItemStack coal = ItemHelper.extract(contraption.inventory, FUEL_ITEMS, 1, false);
    // if (!coal.isEmpty())
    // fuel += 3600;
    // }
    //
    // if (fuel != fuelBefore || pushX != 0 || pushZ != 0) {
    // nbt.putInt("Fuel", fuel);
    // nbt.putDouble("PushX", 0);
    // nbt.putDouble("PushZ", 0);
    // furnaceCart.deserializeNBT(nbt);
    // }
    // }

    // TODO
    // @Nullable
    // public Couple<MinecartController> getCoupledCartsIfPresent() {
    // UUID couplingId = getCouplingId();
    // if (couplingId == null)
    // return null;
    // MinecartController controller = CapabilityMinecartController.getIfPresent(level, couplingId);
    // if (controller == null || !controller.isPresent())
    // return null;
    // UUID coupledCart = controller.getCoupledCart(true);
    // MinecartController coupledController = CapabilityMinecartController.getIfPresent(level, coupledCart);
    // if (coupledController == null || !coupledController.isPresent())
    // return null;
    // return Couple.create(controller, coupledController);
    // }

    // TODO
    // protected void attachInventoriesFromRidingCarts(Entity riding, boolean isOnCoupling, UUID couplingId) {
    // if (isOnCoupling) {
    // Couple<MinecartController> coupledCarts = getCoupledCartsIfPresent();
    // if (coupledCarts == null)
    // return;
    // coupledCarts.map(MinecartController::cart)
    // .forEach(contraption::addExtraInventories);
    // return;
    // }
    // contraption.addExtraInventories(riding);
    // }

    // TODO
    // @Override
    // public NBTTagCompound saveWithoutId(NBTTagCompound nbt) {
    // return isSerializingFurnaceCart ? nbt : super.saveWithoutId(nbt);
    // }

    @Nullable
    public UUID getCouplingId() {
        Optional<UUID> uuid = coupling;
        return uuid == null ? null : uuid.isPresent() ? uuid.get() : null;
    }

    // TODO
    // public void setCouplingId(UUID id) {
    // entityData.set(COUPLING, Optional.ofNullable(id));
    // }

    // TODO
    // @Override
    // public Vec3 getAnchorVec() {
    // return new Vec3(getX() - .5, getY(), getZ() - .5);
    // }

    // TODO
    // @Override
    // protected StructureTransform makeStructureTransform() {
    // BlockPos offset = new BlockPos(getAnchorVec().add(.5, .5, .5));
    // return new StructureTransform(offset, 0, -yaw + getInitialYaw(), 0);
    // }

    @Override
    protected float getStalledAngle() {
        return yaw;
    }

    @Override
    protected void handleStallInformation(float x, float y, float z, float angle) {
        yaw = angle;
    }

    // TODO
    // @Override
    // @SideOnly(Side.CLIENT)
    // public void doLocalTransforms(float partialTicks, PoseStack[] matrixStacks) {
    // float angleInitialYaw = getInitialYaw();
    // float angleYaw = getViewYRot(partialTicks);
    // float anglePitch = getViewXRot(partialTicks);
    //
    // for (PoseStack stack : matrixStacks)
    // stack.translate(-.5f, 0, -.5f);
    //
    // Entity ridingEntity = getVehicle();
    // if (ridingEntity instanceof EntityMinecart)
    // repositionOnCart(partialTicks, matrixStacks, ridingEntity);
    // else if (ridingEntity instanceof AbstractContraptionEntity) {
    // if (ridingEntity.getVehicle() instanceof EntityMinecart)
    // repositionOnCart(partialTicks, matrixStacks, ridingEntity.getVehicle());
    // else
    // repositionOnContraption(partialTicks, matrixStacks, ridingEntity);
    // }
    //
    // for (PoseStack stack : matrixStacks)
    // TransformStack.cast(stack)
    // .nudge(getId())
    // .centre()
    // .rotateY(angleYaw)
    // .rotateZ(anglePitch)
    // .rotateY(angleInitialYaw)
    // .unCentre();
    // }

    // TODO
    // @SideOnly(Side.CLIENT)
    // private void repositionOnContraption(float partialTicks, PoseStack[] matrixStacks, Entity ridingEntity) {
    // Vec3 pos = getContraptionOffset(partialTicks, ridingEntity);
    // for (PoseStack stack : matrixStacks)
    // stack.translate(pos.x, pos.y, pos.z);
    // }

    // TODO
    // // Minecarts do not always render at their exact location, so the contraption
    // // has to adjust aswell
    // @SideOnly(Side.CLIENT)
    // private void repositionOnCart(float partialTicks, PoseStack[] matrixStacks, Entity ridingEntity) {
    // Vec3 cartPos = getCartOffset(partialTicks, ridingEntity);
    //
    // if (cartPos == VecHelper.ZERO)
    // return;
    //
    // for (PoseStack stack : matrixStacks)
    // stack.translate(cartPos.x, cartPos.y, cartPos.z);
    // }

    // TODO
    // @SideOnly(Side.CLIENT)
    // private Vec3 getContraptionOffset(float partialTicks, Entity ridingEntity) {
    // AbstractContraptionEntity parent = (AbstractContraptionEntity) ridingEntity;
    // Vec3 passengerPosition = parent.getPassengerPosition(this, partialTicks);
    // double x = passengerPosition.x - ReCreateMath.lerp(partialTicks, this.xOld, this.getX());
    // double y = passengerPosition.y - ReCreateMath.lerp(partialTicks, this.yOld, this.getY());
    // double z = passengerPosition.z - ReCreateMath.lerp(partialTicks, this.zOld, this.getZ());
    //
    // return new Vec3(x, y, z);
    // }

    // TODO
    // @SideOnly(Side.CLIENT)
    // private Vec3 getCartOffset(float partialTicks, Entity ridingEntity) {
    // EntityMinecart cart = (EntityMinecart) ridingEntity;
    // double cartX = ReCreateMath.lerp(partialTicks, cart.lastTickPosX, cart.posX);
    // double cartY = ReCreateMath.lerp(partialTicks, cart.lastTickPosY, cart.posY);
    // double cartZ = ReCreateMath.lerp(partialTicks, cart.lastTickPosZ, cart.posZ);
    // Vec3 cartPos = cart.getPos(cartX, cartY, cartZ);
    //
    // if (cartPos != null) {
    // Vec3 cartPosFront = cart.getPosOffs(cartX, cartY, cartZ, (double) 0.3F);
    // Vec3 cartPosBack = cart.getPosOffs(cartX, cartY, cartZ, (double) -0.3F);
    // if (cartPosFront == null)
    // cartPosFront = cartPos;
    // if (cartPosBack == null)
    // cartPosBack = cartPos;
    //
    // cartX = cartPos.x - cartX;
    // cartY = (cartPosFront.y + cartPosBack.y) / 2.0D - cartY;
    // cartZ = cartPos.z - cartZ;
    //
    // return new Vec3(cartX, cartY, cartZ);
    // }
    //
    // return VecHelper.ZERO;
    // }

}
