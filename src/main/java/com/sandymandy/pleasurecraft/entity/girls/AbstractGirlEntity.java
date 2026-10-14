package com.sandymandy.pleasurecraft.entity.girls;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.LoopType;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.util.GeckoLibUtil;
import com.sandymandy.pleasurecraft.entity.ai.goal.ConditionalGoal;
import com.sandymandy.pleasurecraft.entity.ai.goal.GirlAttackGoal;
import com.sandymandy.pleasurecraft.entity.ai.goal.StopMovementGoal;
import com.sandymandy.pleasurecraft.networking.C2S.AnimationSyncC2SPacket;
import com.sandymandy.pleasurecraft.networking.C2S.NextSceneAnimationC2SPacket;
import com.sandymandy.pleasurecraft.networking.S2C.ClothingArmorVisibilityS2CPacket;
import com.sandymandy.pleasurecraft.scene.SceneOption;
import com.sandymandy.pleasurecraft.scene.SceneStateManager;
import com.sandymandy.pleasurecraft.screen.GirlInventoryScreenHandlerFactory;
import com.sandymandy.pleasurecraft.util.Messages;
import com.sandymandy.pleasurecraft.util.inventory.GirlInventory;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public abstract class AbstractGirlEntity extends TamableAnimal implements GeoEntity {
    private static final EntityDataAccessor<Boolean> MOVING_TO_BED = SynchedEntityData.defineId(AbstractGirlEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LOCKED_STATE = SynchedEntityData.defineId(AbstractGirlEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(AbstractGirlEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STRIPPED = SynchedEntityData.defineId(AbstractGirlEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FOLLOWING = SynchedEntityData.defineId(AbstractGirlEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IN_SCENE = SynchedEntityData.defineId(AbstractGirlEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> OVERRIDE_ANIM = SynchedEntityData.defineId(AbstractGirlEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> OVERRIDE_LOOP = SynchedEntityData.defineId(AbstractGirlEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> OVERRIDE_HOLD = SynchedEntityData.defineId(AbstractGirlEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> SCENE_PROGRESS = SynchedEntityData.defineId(AbstractGirlEntity.class, EntityDataSerializers.FLOAT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final SceneStateManager sceneManager = new SceneStateManager(this);
    // Estado que solo usa el cliente al dibujar (el renderizador lo copia al estado de render de cada fotograma)
    public Map<String, Boolean> boneVisibility = new HashMap<>();
    public Map<String, Vec2> boneUVOffsets = new HashMap<>();
    public final Map<EquipmentSlot, Boolean> clothingVisibility = new EnumMap<>(EquipmentSlot.class);
    public final Map<EquipmentSlot, Boolean> armorVisibility = new EnumMap<>(EquipmentSlot.class);
    private final GirlInventory inventory = GirlInventory.ofSize();
    private BlockPos basePos;
    private LivingEntity attackTarget;
    private int ticksSinceLastHit;
    private static final int MAX_TICKS_NO_HIT = 20 * 20;
    public float previousYaw = 0;
    public Vec3 previousVelocity = Vec3.ZERO;
    private boolean freeze = false;
    private boolean lockMovement = false;
    public Vec3 clientPassengerBonePos = Vec3.ZERO;
    public Vec3 serverPassengerBonePos = Vec3.ZERO;
    public boolean showHiddenBones = false;
    public final int maxRelationshipLevel = 3;
    public int currentRelationshipLevel;
    private String currentAnimState = "idle";
    private boolean currentLoopState = false;
    private boolean currentHoldState = false;

    protected Item getTameItem() {
        return Items.DANDELION;
    }

    protected String getGirlDisplayName() {
        return "Null";
    }

    protected String getGirlID() {
        return "null";
    }

    public int getSizeGUI(){return 20;}

    public float getYAxisGUI(){return 0.0625F;}

    public abstract List<SceneOption> getSceneOptions();



    protected Map<EquipmentSlot, List<String>> getClothingBones() {
        Map<EquipmentSlot, List<String>> clothing = new HashMap<>();

        clothing.put(EquipmentSlot.HEAD, new ArrayList<>());

        clothing.put(EquipmentSlot.CHEST, new ArrayList<>());

        clothing.put(EquipmentSlot.LEGS, new ArrayList<>());

        clothing.put(EquipmentSlot.FEET, new ArrayList<>());

        return clothing;
    }
    // Armor bones mapped by EquipmentSlot

    public Map<EquipmentSlot, List<String>> getArmorBones() {
        Map<EquipmentSlot, List<String>> armor = new HashMap<>();

        armor.put(EquipmentSlot.HEAD, new ArrayList<>(List.of(
                "armorHelmet"
        )));

        armor.put(EquipmentSlot.CHEST, new ArrayList<>(List.of(
                "armorBoobs",
                "armorChest",
                "armorShoulderL",
                "armorShoulderR"
        )));

        armor.put(EquipmentSlot.LEGS, new ArrayList<>(List.of(
                "armorHip",
                "armorPantsLowL",
                "armorPantsUpL",
                "armorPantsLowR",
                "armorPantsUpR",
                "armorBootyL",
                "armorBootyR"
        )));

        armor.put(EquipmentSlot.FEET, new ArrayList<>(List.of(
                "armorShoesL",
                "armorShoesR"
        )));

        return armor;
    }


    protected AbstractGirlEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MOVING_TO_BED, false);
        builder.define(LOCKED_STATE, false);
        builder.define(SITTING, false);
        builder.define(STRIPPED, false);
        builder.define(FOLLOWING, true);
        builder.define(IN_SCENE, false);
        builder.define(OVERRIDE_ANIM,"");
        builder.define(OVERRIDE_LOOP, false);
        builder.define(OVERRIDE_HOLD, false);
        builder.define(SCENE_PROGRESS,0f);
    }


    public GirlInventory getInventory() {
        return inventory;
    }

    // La armadura que lleva puesta vive en su propio inventario, no en el equipamiento normal del juego.
    // (Se comprueba que el inventario ya exista porque el juego puede preguntar durante la construcción.)
    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        if (inventory == null) return super.getItemBySlot(slot);
        return inventory.getArmorStack(slot);
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        if (inventory == null) {
            super.setItemSlot(slot, stack);
            return;
        }
        inventory.setArmorStack(slot, stack);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(-1, new ConditionalGoal(new StopMovementGoal(this), () -> isFrozenInPlace()));
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.5D, DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES));
        this.goalSelector.addGoal(3, new GirlAttackGoal(this, 1.5, false));
        this.goalSelector.addGoal(4, new ConditionalGoal(new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F), () -> isFollowing() && !isMovingToBed()));
        this.goalSelector.addGoal(5, new ConditionalGoal(new TemptGoal(this, 1.25D, stack -> stack.is(getTameItem()), false),() -> !isMovingToBed()));
        this.goalSelector.addGoal(6, new ConditionalGoal(new RandomStrollGoal(this, 1.0D),() -> !isMovingToBed()));
        this.goalSelector.addGoal(7, new ConditionalGoal(new LookAtPlayerGoal(this, Player.class, 6.0F),() -> !isFrozenInPlace() || !isMovingToBed()));
        this.goalSelector.addGoal(8, new ConditionalGoal(new RandomLookAroundGoal(this),() -> !isFrozenInPlace() || !isMovingToBed()));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this, Player.class));
    }


    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        Item itemInHand = itemStack.getItem();
        if (this.isTame()) {

            if (this.isFoodItem(itemStack) && this.getHealth() < this.getMaxHealth()) {
                this.getNavigation().createPath(player, 20);
                this.usePlayerItem(player, hand, itemStack);
                FoodProperties foodComponent = itemStack.get(DataComponents.FOOD);
                float f = foodComponent != null ? foodComponent.nutrition() : 1.0F;
                this.heal(2.0F * f);
                return InteractionResult.SUCCESS;
            }

            if(this.isOwnedBy(player)) {
                if (itemStack.isEmpty()){
                    if (player.isShiftKeyDown()) {
                        this.setSit(!this.isSittingdown());
                        this.jumping = false;
                        this.getNavigation().stop();
                        this.setTarget(null);
                        return InteractionResult.SUCCESS;
                    }
                    else {
                        player.openMenu(new GirlInventoryScreenHandlerFactory(this));
                        this.setMovementLockedState(true);
                        getLookControl().setLookAt(player, this.getMaxHeadYRot() + 20, this.getMaxHeadXRot());
                        return InteractionResult.SUCCESS;
                    }
                }
            }else {
                if ((itemInHand.equals(getTameItem()))) {
                    player.sendOverlayMessage(Component.literal("She's Already In A Relationship With Someone"));
                    return InteractionResult.FAIL;
                }
            }
        } else {

            if (itemStack.isEmpty() && player.isShiftKeyDown()) {
                this.getNavigation().createPath(player, 20);
                player.openMenu(new GirlInventoryScreenHandlerFactory(this));
                this.setMovementLockedState(true);
                getLookControl().setLookAt(player, this.getMaxHeadYRot() + 20, this.getMaxHeadXRot());
                return InteractionResult.SUCCESS;
            }

            if (!this.level().isClientSide()) {
                if (itemInHand.equals(getTameItem()) && !player.isShiftKeyDown()) {
                    itemStack.consume(1, player);
                    this.tryTame(player);
                    return InteractionResult.SUCCESS;
                } else {
                    // Wrong item OR empty hand (not sneaking)
                    player.sendOverlayMessage(Component.literal(
                            "She ignores you. Maybe try giving her a " + getReadableTameItemName() + "."
                    ));
                    return InteractionResult.FAIL;
                }
            }


        }
        return super.mobInteract(player, hand);
    }

    private void tryTame(Player player) {
        if (this.random.nextInt(3) == 0) {
            this.tame(player);
            this.getNavigation().stop();
            setTarget(null);
            setSit(true);
            this.level().broadcastEntityEvent(this, EntityEvent.TAMING_SUCCEEDED);
            player.sendOverlayMessage(Component.literal("You Asked " + getGirlDisplayName() + " Out And She Said §aYes" ));
            this.setBasePosHere();
        } else {
            this.level().broadcastEntityEvent(this, EntityEvent.TAMING_FAILED);
        }
    }

    public void setSit(boolean sitting) {
        this.entityData.set(SITTING, sitting);
        this.setTarget(null);
        this.refreshDimensions();
        super.setOrderedToSit(sitting);
    }

    public boolean isSittingdown() {
        return this.entityData.get(SITTING);
    }

    public void setFollowing(boolean follow) {
        this.entityData.set(FOLLOWING, follow);
    }

    public boolean isFollowing() {
        return this.entityData.get(FOLLOWING);
    }

    public void setStripped(boolean stripped) {
        this.entityData.set(STRIPPED, stripped);
    }

    public boolean isStripped() {
        return this.entityData.get(STRIPPED);
    }

    public void setFreeze(boolean locked) {
        this.freeze = locked;
    }

    public boolean isFrozenInPlace() {
        return this.freeze;
    }

    public void setMovementLockedState(boolean locked) {
        this.entityData.set(LOCKED_STATE, locked);
    }

    public boolean isMovementLocked() {
        return this.entityData.get(LOCKED_STATE);
    }

    public void setMovingToBedState(boolean state) {
        this.entityData.set(MOVING_TO_BED, state);
    }

    public boolean isMovingToBed() {
        return this.entityData.get(MOVING_TO_BED);
    }


    public void setSceneState(boolean inScene) {
        this.entityData.set(IN_SCENE, inScene);
    }

    public boolean isSceneActive() {
        return this.entityData.get(IN_SCENE);
    }

    public void setOverrideAnim(String anim){
        this.entityData.set(OVERRIDE_ANIM, anim);
    }

    public String getOverrideAnim(){
        return this.entityData.get(OVERRIDE_ANIM);
    }

    public void setOverrideLoop(boolean loop){
        this.entityData.set(OVERRIDE_LOOP, loop);
    }

    public boolean getOverrideLoop(){
        return this.entityData.get(OVERRIDE_LOOP);
    }

    public void setOverrideHold(boolean hold){
        this.entityData.set(OVERRIDE_HOLD, hold);
    }

    public boolean getOverrideHold(){
        return this.entityData.get(OVERRIDE_HOLD);
    }

    public void setSceneProgress(float progress){
        this.entityData.set(SCENE_PROGRESS, progress);
    }

    public float getSceneProgress(){
        return this.entityData.get(SCENE_PROGRESS);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    public boolean isFoodItem(ItemStack stack) {
        return stack.is(ItemTags.WOLF_FOOD);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
        return null;
    }

    @Override
    public boolean shouldShowName() {
        this.setCustomName(Component.literal(getGirlDisplayName()));
        this.setCustomNameVisible(true);
        return true;
    }

    public void breakUp(Player player) {
        if(!player.level().isClientSide()){
            this.setTame(false,true); // Mark the entity as untamed
            this.setOwnerReference(null); // Remove the owner
            this.setSit(false); // Ensure the entity is not sitting
            this.setStripped(false);
            if(!isTame() && !isOwnedBy(player)){
                player.sendOverlayMessage(Component.literal("§cYou Broke Up With " + getGirlDisplayName()));
            }
        }
    }

    public void setBasePosHere(){
        this.basePos = this.blockPosition();
    }

    public void setBasePos(BlockPos pos) {
        this.basePos = pos;
    }

    public BlockPos getBasePos(){
        return basePos;
    }

    public void teleportToBase() {
        if (this.basePos != null && this.level() != null) {
            setSit(true);
            this.teleportTo(this.basePos.getX(), this.basePos.getY(), this.basePos.getZ());
        }
    }

    public void toggleModelBones(List<String> bones, boolean visible){
        if(!level().isClientSide()){
            return;
        }

        if (this.boneVisibility == null) {
            this.boneVisibility = new HashMap<>();
        }


        for (String boneName : bones) {
            this.boneVisibility.put(boneName, visible);
        }
    }

    public void overrideBoneUV(List<String> bones, float uOffset, float vOffset) {
        if (this.boneUVOffsets == null) this.boneUVOffsets = new HashMap<>();

        for (String boneName : bones) {
            this.boneUVOffsets.put(boneName, new Vec2(uOffset, vOffset));
        }
    }

    @Nullable
    public Vec2 getBoneUVOffset(String boneName) {
        if (boneUVOffsets != null && boneUVOffsets.containsKey(boneName)) {
            return boneUVOffsets.get(boneName);
        }
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<AbstractGirlEntity>("controller", 4, this::handleAnimations));
    }

    private PlayState handleAnimations(AnimationTest<AbstractGirlEntity> state) {
        AnimationController<AbstractGirlEntity> controller = state.controller();

        String overrideAnim = this.getOverrideAnim();
        boolean overrideLoop = this.getOverrideLoop();
        boolean overrideHold = this.getOverrideHold();

        // 1. Forced animation override
        if (overrideAnim != null && !overrideAnim.isEmpty()) {
            this.currentAnimState = overrideAnim;
            this.currentLoopState = overrideLoop;
            this.currentHoldState = overrideHold;


            // End override if it was one-shot and finished playing
            if (!overrideLoop && controller.hasAnimationFinished()) {
                if (isSceneActive()){
                    ClientPlayNetworking.send(new NextSceneAnimationC2SPacket(this.getId(),this.currentAnimState));
                }
                else {
                    stopOverrideAnimations();
                }

            }
        }
        else {
            this.currentAnimState = getDefaultAnimation(state);
            this.currentLoopState = true;
        }

        LoopType loopType;

        if (this.currentLoopState) {
            loopType = LoopType.LOOP;
        } else if (this.currentHoldState) {
            loopType = LoopType.HOLD_ON_LAST_FRAME;
        } else {
            loopType = LoopType.PLAY_ONCE;
        }


        controller.setAnimation(RawAnimation.begin().then
                (getAnimationPath(this.currentAnimState), loopType));
        return PlayState.CONTINUE;

    }

    private String getDefaultAnimation(AnimationTest<AbstractGirlEntity> state) {
        if (!this.onGround() && !isSittingdown()) return "fly";
        if (state.isMoving() && !isSittingdown()) return "walk";
        if (isSittingdown()) return "sit";
        return "idle";
    }



    // Call this to force an animation
    public void playAnimation(String animationName, boolean loop, boolean holdOnLastFrame) {
        if (!this.level().isClientSide()) { // run only on server
            this.setOverrideAnim(animationName != null ? animationName : "");
            this.setOverrideLoop(loop);
            this.setOverrideHold(holdOnLastFrame);
        } else {
            ClientPlayNetworking.send(new AnimationSyncC2SPacket(this.getId(),
                    animationName != null ? animationName : "",
                    loop,
                    holdOnLastFrame));
        }
    }

    public void stopOverrideAnimations() {
        if (this.level().isClientSide()) {
            ClientPlayNetworking.send(new AnimationSyncC2SPacket(this.getId(), "",false,false));
        } else {
            // En el servidor (por ejemplo al terminar una escena) no hay cliente al que mandar el paquete: se cambia directamente
            this.setOverrideAnim("");
            this.setOverrideLoop(false);
            this.setOverrideHold(false);
        }
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);

        ContainerHelper.saveAllItems(output, this.inventory.getItems());
        output.putBoolean("SitSate", this.isSittingdown());
        output.putBoolean("StripState", this.isStripped());
        output.putBoolean("SceneState", this.isSceneActive());
        output.putBoolean("FrozenState", this.isFrozenInPlace());


        if (this.basePos != null) {
            output.putInt("BaseX", this.basePos.getX());
            output.putInt("BaseY", this.basePos.getY());
            output.putInt("BaseZ", this.basePos.getZ());
        }
    }


    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        ContainerHelper.loadAllItems(input, this.inventory.getItems());
        this.setSit(input.getBooleanOr("SitSate", false));
        this.setStripped(input.getBooleanOr("StripState", false));
        Optional<Integer> x = input.getInt("BaseX");
        Optional<Integer> y = input.getInt("BaseY");
        Optional<Integer> z = input.getInt("BaseZ");
        if (x.isPresent() && y.isPresent() && z.isPresent()) {
            this.basePos = new BlockPos(x.get(), y.get(), z.get());
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.getSceneManager().tick();
        previousYaw = getYRot();
        previousVelocity = getDeltaMovement();
        updateClothingAndArmor();
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (attackTarget != null) {
            ticksSinceLastHit++;

            if (ticksSinceLastHit >= MAX_TICKS_NO_HIT) {
                // Lost interest - stop attacking
                this.setTarget(null);
                attackTarget = null;
                ticksSinceLastHit = 0;
            }
        }

        if(isMovementLocked()){
            this.getNavigation().stop();
        }
    }


    private void updateClothingAndArmor() {
        if (this.level().isClientSide()) return;

        boolean stripped = isStripped();

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            clothingVisibility.put(slot, !stripped && this.inventory.getArmorStack(slot).isEmpty());
            armorVisibility.put(slot, !stripped && !this.inventory.getArmorStack(slot).isEmpty());


        }

        // --- send packet to client ---
        List<Boolean> clothingList = Arrays.stream(EquipmentSlot.values())
                .map(s -> clothingVisibility.getOrDefault(s, false))
                .toList();

        List<Boolean> armorList = Arrays.stream(EquipmentSlot.values())
                .map(s -> armorVisibility.getOrDefault(s, false))
                .toList();

        ClothingArmorVisibilityS2CPacket packet =
                new ClothingArmorVisibilityS2CPacket(this.getId(), clothingList, armorList);

        for (ServerPlayer player : Objects.requireNonNull(this.level().getServer()).getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, packet);
        }

    }

    public void applyClothingAndArmor() {
        if (!this.level().isClientSide()) return;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            List<String> clothingBones = getClothingBones().get(slot);
            if (clothingBones != null) {
                toggleModelBones(clothingBones, clothingVisibility.getOrDefault(slot, false));
            }

            List<String> armorBones = getArmorBones().get(slot);
            if (armorBones != null) {
                toggleModelBones(armorBones, armorVisibility.getOrDefault(slot, false));
            }

            if (!this.inventory.getArmorStack(slot).isEmpty() && armorBones != null) {
                String typeName = BuiltInRegistries.ITEM.getKey(this.inventory.getArmorStack(slot).getItem()).getPath().toLowerCase();
                float uv = getArmorU(typeName);
                this.overrideBoneUV(armorBones, uv, 0);
            }
        }



        // Naked bits
        toggleModelBones(List.of("vagina"), isStripped());
    }


    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);

        if (target != null) {
            attackTarget = target;
            ticksSinceLastHit = 0; // reset countdown on new target
        } else {
            attackTarget = null;
            ticksSinceLastHit = 0;
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        boolean success = super.doHurtTarget(world, target);

        if (success && target == attackTarget) {
            ticksSinceLastHit = 0; // reset timer on successful hit
        }

        return success;
    }

    /** El renderizador llama aquí con la posición en el mundo del hueso donde va el pasajero. */
    public void handlePassengerBone(Vec3 worldPos){
        if (worldPos != null) {
            this.clientPassengerBonePos = worldPos;
        }
    }

    public void cachePassengerBone(boolean valid, Vec3 pos){
        if (valid) {
            this.serverPassengerBonePos = pos;

        }
    }

    public Vec3 getPassengerBone(){
        if(this.level().isClientSide()){
            return clientPassengerBonePos;
        }
        else {
            return serverPassengerBonePos;
        }
    }



    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return this.getPassengerBone().add(0,1,0);
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity passenger) {
        /*This is not based on the local coordinates from the entity. it is the global coordinates based on the world. It also has a base offset of -0.6 on the Y Axis
        * This is also based on the client not the server*/
        if(this.getPassengerBone() == Vec3.ZERO){
            return this.position().add(0,this.getSceneManager().passengerYOffset,0);
        }
        else {
            return this.getPassengerBone().add(0,this.getSceneManager().passengerYOffset,0);
        }
    }

    public SceneStateManager getSceneManager() {
        return sceneManager;
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return false;
    }

    @Override
    protected void dropEquipment(ServerLevel world) {
        super.dropEquipment(world); // calls standard drop logic
        for (ItemStack stack : this.getInventory().getItems()) {
            if (!stack.isEmpty()) {
                this.spawnAtLocation(world, stack);
            }
        }
        this.getInventory().clearContent();
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if (this.isInvulnerableTo(world, source)) return false;

        String damageType = source.getMsgId();
        // If killed by /kill or void, allow normal death
        if (damageType.equals("outOfWorld") || damageType.equals("genericKill")) {
            return super.hurtServer(world, source, amount);
        }

        if(this.isTame() && (this.getHealth() - amount <= 0.0F) &! (damageType.equals("outOfWorld") || damageType.equals("genericKill") || isFrozenInPlace())) {
            this.setHealth(getMaxHealth());
            // If basePos is still null, fall back to current position
            BlockPos respawnPos = (this.basePos != null)
                    ? this.basePos
                    : this.blockPosition();

            // Send a message referencing whichever Pos we have
            new Messages().GlobleMessage(
                    this.level(),
                    getGirlDisplayName() + " died and respawned at base: " +
                            respawnPos.getX() + ", " +
                            respawnPos.getY() + ", " +
                            respawnPos.getZ()
            );

            // Drops inventory as if she died
            this.dropEquipment(world);

            // If basePos was null, make sure to set it now so future hits won't NPE
            if (this.basePos == null) {
                this.basePos = respawnPos;
            }
            teleportToBase();

            return false;
        }
        else if(isFrozenInPlace() &! damageType.equals("outOfWorld") || damageType.equals("genericKill")){
            if(!this.isVehicle()){
                new Messages().GlobleMessage(
                        this.level(),getGirlDisplayName() + " is busy at the moment");
            }
            return false;
        }
        else{
            return super.hurtServer(world, source, amount);
        }
    }

    @Override
    public void push(Entity entity) {
        if (!freeze) {
            super.push(entity);
        }
    }

    @Override
    public boolean isPushable() {
        return !freeze;
    }


    @Override
    public void push(double dx, double dy, double dz) {
        if (!freeze) {
            super.push(dx, dy, dz);
        }
    }


    public String getReadableTameItemName() {
        Item tameItem = this.getTameItem();
        Identifier id = BuiltInRegistries.ITEM.getKey(tameItem);

        if (id != null) {
            String path = id.getPath(); // e.g., "blue_allium"

            // Capitalize each word split by underscores
            String[] words = path.split("_");
            StringBuilder formatted = new StringBuilder();

            for (String word : words) {
                if (!word.isEmpty()) {
                    formatted.append(Character.toUpperCase(word.charAt(0)))
                            .append(word.substring(1))
                            .append(" ");
                }
            }

            return formatted.toString().trim(); // "Blue Allium"
        } else {
            return "Unknown Item";
        }
    }

    public void messageAsEntity(String message){
        this.messageAsEntity(null,message);
    }

    public void messageAsEntity(@Nullable Player playerEntity, String message){
        if(this.level().isClientSide()) return;
        String finalMessage = "<"+getGirlDisplayName()+"> " + message;

        if(playerEntity == null){
            new Messages().GlobleMessage(this.level(), finalMessage);
        }
        else {
            new Messages().PlayerSpecificMessage(playerEntity,finalMessage);
        }

    }

    private String getAnimationPath(String animation){
        return "animation." + this.getGirlID() + "." + animation;
    }

    private float getArmorU(String armorType){
        if (armorType.contains("turtle")) return 0.10546875f;
        if (armorType.contains("leather")) return 0.0703125f;
        if (armorType.contains("iron")) return 0.03515625f;
        if (armorType.contains("chain")) return 0.052734375f;
        if (armorType.contains("gold")) return 0.0176f;
        if (armorType.contains("netherite")) return 0.087890625f;
        return 0f;
    }
}
