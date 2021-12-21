package internalconfig.game.systems.gamesystems.spawnhandlers;

import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.component.TypeComponentTuple;
import ecs.system.criticalorders.AddEntityOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.Animation;
import internalconfig.game.components.AnimationComponent;
import internalconfig.game.components.DamageGiveCommands;
import internalconfig.game.components.DamageReceiveCommands;
import internalconfig.game.components.DeathCommands;
import internalconfig.game.components.DrawOrder;
import internalconfig.game.components.DrawPlane;
import internalconfig.game.components.HealthComponent;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.PickupTypes;
import internalconfig.game.components.ProgramComponent;
import internalconfig.game.components.ScrollingSubImageComponent;
import internalconfig.game.components.SinusoidalSpriteVerticalOffsetComponent;
import internalconfig.game.components.SpawnComponent;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.components.TrailComponent;
import internalconfig.game.components.VelocityComponent;
import internalconfig.game.components.spawns.GraphicSpawns;
import internalconfig.game.components.spawns.Spawns;
import util.math.geometry.AABB;
import util.math.geometry.AbstractVector;
import util.math.geometry.ConstCartesianVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.TwoFramePosition;
import util.messaging.Message;

import java.awt.*;
import java.util.ArrayList;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.GameConfig.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public final class SpawnBuilder {

    private final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;
    private final AbstractComponentType<Rectangle> spriteSubImageComponentType;
    private final AbstractComponentType<ScrollingSubImageComponent> scrollingSubImageComponentType;
    private final AbstractComponentType<DrawOrder> drawOrderComponentType;
    private final AbstractComponentType<Void> visibleMarker;
    private final AbstractComponentType<Void> gamePlaneMarker;

    private final AbstractComponentType<TwoFramePosition> positionComponentType;
    private final AbstractComponentType<VelocityComponent> velocityComponentType;
    private final AbstractComponentType<AABB> hitboxComponentType;
    private final AbstractComponentType<Void> collidableMarker;
    private final AbstractComponentType<HealthComponent> healthComponentType;
    private final AbstractComponentType<Integer> damageComponentType;

    private final AbstractComponentType<AnimationComponent> animationComponentType;
    private final AbstractComponentType<Void> rotateSpriteForwardMarker;
    private final AbstractComponentType<Void> makeOpaqueWhenPlayerFocusedAndAliveMarker;
    private final AbstractComponentType<Double> constantSpriteRotationComponentType;
    private final AbstractComponentType<SinusoidalSpriteVerticalOffsetComponent> sinusoidalSpriteVerticalOffsetComponentType;
    private final AbstractComponentType<TrailComponent> trailComponentType;

    private final AbstractComponentType<SpawnComponent> spawnComponentType;
    private final AbstractComponentType<ProgramComponent> programComponentType;

    private final AbstractComponentType<Double> inboundComponentType;
    private final AbstractComponentType<Double> outboundComponentType;

    private final AbstractComponentType<Void> playerDamageGive;
    private final AbstractComponentType<Void> playerDamageReceive;
    private final AbstractComponentType<Void> bombDamageGive;
    private final AbstractComponentType<Void> bombDamageReceive;
    private final AbstractComponentType<Void> enemyDamageGive;
    private final AbstractComponentType<Void> enemyDamageReceive;
    private final AbstractComponentType<Void> pickupDamageGive;
    private final AbstractComponentType<Void> pickupDamageReceive;
    private final AbstractComponentType<Void> bulletSlowDamageGive;
    private final AbstractComponentType<Void> bulletSlowDamageReceive;

    private final AbstractComponentType<DamageGiveCommands> damageGiveCommandComponentType;
    private final AbstractComponentType<DamageReceiveCommands> damageReceiveCommandComponentType;
    private final AbstractComponentType<DeathCommands> deathCommandComponentType;
    private final AbstractComponentType<Spawns> deathSpawnComponentType;

    private final AbstractComponentType<PickupTypes> pickupTypeComponentType;
    private final AbstractComponentType<Integer> pickupDataComponentType;

    public SpawnBuilder(AbstractComponentTypeContainer componentTypeContainer) {

        spriteInstructionComponentType = componentTypeContainer.getTypeInstance(SpriteInstructionComponentType.class);
        spriteSubImageComponentType = componentTypeContainer.getTypeInstance(SpriteSubImageComponentType.class);
        scrollingSubImageComponentType = componentTypeContainer.getTypeInstance(ScrollingSubImageComponentType.class);

        drawOrderComponentType = componentTypeContainer.getTypeInstance(DrawOrderComponentType.class);
        visibleMarker = componentTypeContainer.getTypeInstance(VisibleMarker.class);
        gamePlaneMarker = componentTypeContainer.getTypeInstance(GamePlaneMarker.class);

        positionComponentType = componentTypeContainer.getTypeInstance(PositionComponentType.class);
        velocityComponentType = componentTypeContainer.getTypeInstance(VelocityComponentType.class);
        hitboxComponentType = componentTypeContainer.getTypeInstance(HitboxComponentType.class);
        collidableMarker = componentTypeContainer.getTypeInstance(CollidableMarker.class);
        healthComponentType = componentTypeContainer.getTypeInstance(HealthComponentType.class);
        damageComponentType = componentTypeContainer.getTypeInstance(DamageComponentType.class);

        animationComponentType = componentTypeContainer.getTypeInstance(AnimationComponentType.class);
        rotateSpriteForwardMarker = componentTypeContainer.getTypeInstance(RotateSpriteForwardMarker.class);
        makeOpaqueWhenPlayerFocusedAndAliveMarker = componentTypeContainer.getTypeInstance(MakeOpaqueWhenPlayerFocusedAndAliveMarker.class);
        constantSpriteRotationComponentType = componentTypeContainer.getTypeInstance(ConstantSpriteRotationComponentType.class);
        sinusoidalSpriteVerticalOffsetComponentType = componentTypeContainer.getTypeInstance(SinusoidalSpriteVerticalOffsetComponentType.class);
        trailComponentType = componentTypeContainer.getTypeInstance(TrailComponentType.class);

        spawnComponentType = componentTypeContainer.getTypeInstance(SpawnComponentType.class);
        programComponentType = componentTypeContainer.getTypeInstance(ProgramComponentType.class);

        inboundComponentType = componentTypeContainer.getTypeInstance(InboundComponentType.class);
        outboundComponentType = componentTypeContainer.getTypeInstance(OutboundComponentType.class);

        playerDamageGive = componentTypeContainer.getTypeInstance(PlayerDamage.Give.class);
        playerDamageReceive = componentTypeContainer.getTypeInstance(PlayerDamage.Receive.class);
        bombDamageGive = componentTypeContainer.getTypeInstance(BombDamage.Give.class);
        bombDamageReceive = componentTypeContainer.getTypeInstance(BombDamage.Receive.class);
        enemyDamageGive = componentTypeContainer.getTypeInstance(EnemyDamage.Give.class);
        enemyDamageReceive = componentTypeContainer.getTypeInstance(EnemyDamage.Receive.class);
        pickupDamageGive = componentTypeContainer.getTypeInstance(PickupDamage.Give.class);
        pickupDamageReceive = componentTypeContainer.getTypeInstance(PickupDamage.Receive.class);
        bulletSlowDamageGive = componentTypeContainer.getTypeInstance(BulletSlowDamage.Give.class);
        bulletSlowDamageReceive = componentTypeContainer.getTypeInstance(BulletSlowDamage.Receive.class);

        damageGiveCommandComponentType = componentTypeContainer.getTypeInstance(DamageGiveCommandComponentType.class);
        damageReceiveCommandComponentType =
                componentTypeContainer.getTypeInstance(DamageReceiveCommandComponentType.class);
        deathCommandComponentType = componentTypeContainer.getTypeInstance(DeathCommandComponentType.class);
        deathSpawnComponentType = componentTypeContainer.getTypeInstance(DeathSpawnComponentType.class);

        pickupTypeComponentType = componentTypeContainer.getTypeInstance(PickupTypeComponentType.class);
        pickupDataComponentType = componentTypeContainer.getTypeInstance(PickupDataComponentType.class);
    }

    public TypeComponentTupleList makeEntity() {
        return new TypeComponentTupleList();
    }

    public TypeComponentTupleList makePosition(DoublePoint pos) {
        TypeComponentTupleList toRet = new TypeComponentTupleList();
        toRet.add(new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(pos)));
        return toRet;
    }

    public TypeComponentTupleList makeVisible(DoublePoint pos) {
        TypeComponentTupleList toRet = new TypeComponentTupleList();
        toRet.add(new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(pos)));
        toRet.add(new TypeComponentTuple<>(visibleMarker));
        return toRet;
    }

    public TypeComponentTupleList makeVisibleGameObject(DoublePoint pos) {
        TypeComponentTupleList toRet = new TypeComponentTupleList();
        toRet.add(new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(pos)));
        toRet.add(new TypeComponentTuple<>(visibleMarker));
        toRet.add(new TypeComponentTuple<>(gamePlaneMarker));
        return toRet;
    }

    public TypeComponentTupleList makeStationaryCollidable(DoublePoint pos, AABB hitbox) {
        TypeComponentTupleList toRet = new TypeComponentTupleList();
        toRet.add(new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(pos)));
        toRet.add(new TypeComponentTuple<>(hitboxComponentType, hitbox));
        toRet.add(new TypeComponentTuple<>(collidableMarker));
        toRet.add(new TypeComponentTuple<>(visibleMarker));
        toRet.add(new TypeComponentTuple<>(gamePlaneMarker));
        return toRet;
    }

    public TypeComponentTupleList makeStationaryUncollidable(DoublePoint pos, AABB hitbox) {
        TypeComponentTupleList toRet = new TypeComponentTupleList();
        toRet.add(new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(pos)));
        toRet.add(new TypeComponentTuple<>(hitboxComponentType, hitbox));
        toRet.add(new TypeComponentTuple<>(visibleMarker));
        toRet.add(new TypeComponentTuple<>(gamePlaneMarker));
        return toRet;
    }

    public TypeComponentTupleList makeStraightPathCollidable(DoublePoint pos,
                                                             AbstractVector velocity,
                                                             AABB hitbox,
                                                             double outbound) {
        return makeStationaryCollidable(pos, hitbox)
                .setVelocity(velocity)
                .setOutbound(outbound)
                ;
    }

    public TypeComponentTupleList makeStraightPathUncollidable(DoublePoint pos,
                                                               AbstractVector velocity,
                                                               AABB hitbox,
                                                               double outbound) {
        return makeStationaryUncollidable(pos, hitbox)
                .setVelocity(velocity)
                .setOutbound(outbound)
                ;
    }

    public TypeComponentTupleList makeStraightBullet(DoublePoint pos,
                                                     AbstractVector velocity,
                                                     AABB hitbox,
                                                     double outbound,
                                                     int damage) {
        return makeStraightPathCollidable(pos, velocity, hitbox, outbound)
                .setDamage(damage)
                .setDamageGiveCommand(DamageGiveCommands.DEATH)
                ;
    }

    public TypeComponentTupleList makeStraightBulletUncollidable(DoublePoint pos,
                                                                 AbstractVector velocity,
                                                                 AABB hitbox,
                                                                 double outbound,
                                                                 int damage) {
        return makeStraightPathUncollidable(pos, velocity, hitbox, outbound)
                .setDamage(damage)
                .setDamageGiveCommand(DamageGiveCommands.DEATH)
                ;
    }

    public TypeComponentTupleList makeStraightPlayerBullet(DoublePoint pos,
                                                           AbstractVector velocity,
                                                           AABB hitbox,
                                                           double outbound,
                                                           int damage) {
        return makeStraightBullet(pos, velocity, hitbox, outbound, damage)
                .markAsPlayerProjectile();
    }

    public TypeComponentTupleList makeStraightUnslowableEnemyBullet(DoublePoint pos,
                                                                    AbstractVector velocity,
                                                                    EnemyProjectileTypes type,
                                                                    EnemyProjectileColors color,
                                                                    double outbound,
                                                                    int relativeDrawOrder) {
        TypeComponentTupleList toRet = makeStraightBulletUncollidable(pos, velocity, type.getHitbox(), outbound, 0)
                .markAsEnemyProjectile()
                .setHealth(1)
                .setDamageReceiveCommand(DamageReceiveCommands.TAKE_DAMAGE)
                .setDeathSpawnWithCommandAndSpawnComponent(GraphicSpawns.getEnemyBulletExplodeSpawn(type, color))
                .setDrawOrder(DrawPlane.MIDGROUND, 300 + relativeDrawOrder)
                .setSpriteInstruction(new SpriteInstruction("spawn" + color.getSuffix()))
                .setAnimation(
                        new AnimationComponent(
                                new Animation(false, type.getName() + color.getSuffix())
                        )
                );
        return !type.isRotateSpriteForward()
                ? toRet
                : toRet.markRotateSpriteForward();
    }

    public TypeComponentTupleList makeStraightSlowableEnemyBullet(DoublePoint pos,
                                                                  AbstractVector velocity,
                                                                  EnemyProjectileTypes type,
                                                                  EnemyProjectileColors color,
                                                                  double outbound,
                                                                  int relativeDrawOrder) {
        return makeStraightUnslowableEnemyBullet(pos, velocity, type, color, outbound, relativeDrawOrder)
                .mark(bulletSlowDamageReceive);
    }

    public TypeComponentTupleList makeBarrier(DoublePoint pos,
                                              AbstractVector velocity,
                                              double outbound,
                                              int relativeDrawOrder) {
        return makeStraightBulletUncollidable(pos, velocity, MEDIUM_BULLET_HITBOX, outbound, 0)
                .markAsEnemy()
                .setHealth(2000)
                .setDamageReceiveCommand(DamageReceiveCommands.TAKE_DAMAGE)
                .setDeathSpawnWithCommandAndSpawnComponent(GraphicSpawns.getEnemyBulletExplodeSpawn(MEDIUM, MAGENTA))
                .setDrawOrder(DrawPlane.MIDGROUND, 300 + relativeDrawOrder)
                .setSpriteInstruction(new SpriteInstruction("spawn" + MAGENTA.getSuffix()))
                .setAnimation(
                        new AnimationComponent(
                                new Animation(false, "barrier")
                        )
                )
                .markRotateSpriteForward()
                .mark(bulletSlowDamageReceive);
    }

    public TypeComponentTupleList makePickup(DoublePoint pos,
                                             AbstractVector velocity,
                                             AABB hitbox,
                                             double outbound,
                                             int drawOrder) {
        return makeStraightPathCollidable(pos, velocity, hitbox, outbound)
                .setDrawOrder(DrawPlane.MIDGROUND, drawOrder)
                .markAsPickup()
                .setDamageReceiveCommand(DamageReceiveCommands.DEATH)
                .setDeathCommand(DeathCommands.PICKUP_DEATH);
    }

    public TypeComponentTupleList makePowerPickup(DoublePoint pos,
                                                  AbstractVector velocity,
                                                  AABB hitbox,
                                                  double outbound,
                                                  int power,
                                                  int drawOrder) {
        return makePickup(pos, velocity, hitbox, outbound, drawOrder)
                .setPickupType(PickupTypes.POWER)
                .setPickupData(power);
    }

    public TypeComponentTupleList makeLargePowerPickup(DoublePoint pos, AbstractVector velocity) {
        return makePowerPickup(pos, velocity, LARGE_PICKUP_HITBOX, PICKUP_OUTBOUND, LARGE_POWER_GAIN, 202)
                .setSpriteInstruction(new SpriteInstruction("large_power"));
    }

    public TypeComponentTupleList makeSmallPowerPickup(DoublePoint pos, AbstractVector velocity) {
        return makePowerPickup(pos, velocity, SMALL_PICKUP_HITBOX, PICKUP_OUTBOUND, SMALL_POWER_GAIN, 201)
                .setSpriteInstruction(new SpriteInstruction("small_power"));
    }

    public TypeComponentTupleList makeLifePickup(DoublePoint pos,
                                                 AbstractVector velocity) {
        return makePickup(pos, velocity, LARGE_PICKUP_HITBOX, PICKUP_OUTBOUND, 203)
                .setPickupType(PickupTypes.LIFE)
                .setSpriteInstruction(new SpriteInstruction("life"));
    }

    public TypeComponentTupleList makeBombPickup(DoublePoint pos,
                                                 AbstractVector velocity) {
        return makePickup(pos, velocity, LARGE_PICKUP_HITBOX, PICKUP_OUTBOUND, 203)
                .setPickupType(PickupTypes.BOMB)
                .setSpriteInstruction(new SpriteInstruction("bomb"));
    }

    public final class TypeComponentTupleList extends ArrayList<TypeComponentTuple<?>> {

        public TypeComponentTupleList setPosition(DoublePoint pos) {
            add(new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(pos)));
            return this;
        }

        public TypeComponentTupleList setVelocity(AbstractVector velocity) {
            add(new TypeComponentTuple<>(velocityComponentType, new VelocityComponent(velocity)));
            return this;
        }

        public TypeComponentTupleList setHitbox(AABB hitbox) {
            add(new TypeComponentTuple<>(hitboxComponentType, hitbox));
            return this;
        }

        public TypeComponentTupleList setAnimation(AnimationComponent animationComponent) {
            add(new TypeComponentTuple<>(animationComponentType, animationComponent));
            return this;
        }

        public TypeComponentTupleList setConstantSpriteRotation(double rotation) {
            add(new TypeComponentTuple<>(constantSpriteRotationComponentType, rotation));
            return this;
        }

        public TypeComponentTupleList setSinusoidalSpriteVerticalOffset(SinusoidalSpriteVerticalOffsetComponent component) {
            add(new TypeComponentTuple<>(sinusoidalSpriteVerticalOffsetComponentType, component));
            return this;
        }

        public TypeComponentTupleList setTrail(TrailComponent trail) {
            add(new TypeComponentTuple<>(trailComponentType, trail));
            return this;
        }

        public TypeComponentTupleList setSpriteInstruction(SpriteInstruction spriteInstruction) {
            add(new TypeComponentTuple<>(spriteInstructionComponentType, spriteInstruction));
            return this;
        }

        public TypeComponentTupleList setSpriteSubImage(Rectangle spriteSubImage){
            add(new TypeComponentTuple<>(spriteSubImageComponentType, spriteSubImage));
            return this;
        }

        public TypeComponentTupleList setScrollingSubImage(ScrollingSubImageComponent scrollingSubImageComponent){
            add(new TypeComponentTuple<>(scrollingSubImageComponentType, scrollingSubImageComponent));
            return this;
        }

        public TypeComponentTupleList setDrawOrder(DrawPlane plane, int order) {
            add(new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(plane, order)));
            return this;
        }

        public TypeComponentTupleList setInbound(double inbound) {
            add(new TypeComponentTuple<>(inboundComponentType, inbound));
            return this;
        }

        public TypeComponentTupleList setOutbound(double outbound) {
            add(new TypeComponentTuple<>(outboundComponentType, outbound));
            return this;
        }

        public TypeComponentTupleList setHealth(int health) {
            add(new TypeComponentTuple<>(healthComponentType, new HealthComponent(health)));
            return this;
        }

        public TypeComponentTupleList setDamage(int damage) {
            add(new TypeComponentTuple<>(damageComponentType, damage));
            return this;
        }

        public TypeComponentTupleList setPickupType(PickupTypes type) {
            add(new TypeComponentTuple<>(pickupTypeComponentType, type));
            return this;
        }

        public TypeComponentTupleList setPickupData(int data) {
            add(new TypeComponentTuple<>(pickupDataComponentType, data));
            return this;
        }

        public TypeComponentTupleList setDamageGiveCommand(DamageGiveCommands damageGiveCommand) {
            add(new TypeComponentTuple<>(damageGiveCommandComponentType, damageGiveCommand));
            return this;
        }

        public TypeComponentTupleList setDamageReceiveCommand(DamageReceiveCommands damageReceiveCommand) {
            add(new TypeComponentTuple<>(damageReceiveCommandComponentType, damageReceiveCommand));
            return this;
        }

        public TypeComponentTupleList setDeathCommand(DeathCommands deathCommand) {
            add(new TypeComponentTuple<>(deathCommandComponentType, deathCommand));
            return this;
        }

        public TypeComponentTupleList setDeathSpawnWithCommandAndSpawnComponent(Spawns deathSpawn) {
            setDeathCommand(DeathCommands.DEATH_SPAWN);
            setSpawnComponent(new SpawnComponent());
            add(new TypeComponentTuple<>(deathSpawnComponentType, deathSpawn));
            return this;
        }

        public TypeComponentTupleList setSpawnComponent(SpawnComponent spawnComponent) {
            add(new TypeComponentTuple<>(spawnComponentType, spawnComponent));
            return this;
        }

        public TypeComponentTupleList setProgram(InstructionNode<?, ?>... program) {
            add(new TypeComponentTuple<>(programComponentType, new ProgramComponent(program)));
            return this;
        }

        public TypeComponentTupleList mark(AbstractComponentType<?> marker) {
            add(new TypeComponentTuple<>(marker));
            return this;
        }

        public TypeComponentTupleList markRotateSpriteForward() {
            return mark(rotateSpriteForwardMarker);
        }

        public TypeComponentTupleList markMakeOpaqueWhenPlayerFocusedAndAlive() {
            return mark(makeOpaqueWhenPlayerFocusedAndAliveMarker);
        }

        public TypeComponentTupleList markAsBulletBlocker() {
            return mark(enemyDamageReceive);
        }

        public TypeComponentTupleList markAsBulletSlower() {
            return mark(bulletSlowDamageGive);
        }

        public TypeComponentTupleList markAsPlayer() {
            return mark(enemyDamageReceive)
                    .mark(pickupDamageGive);
        }

        public TypeComponentTupleList markAsPlayerProjectile() {
            return mark(playerDamageGive);
        }

        public TypeComponentTupleList markAsBomb() {
            return mark(bombDamageGive);
        }

        public TypeComponentTupleList markAsEnemy() {
            return mark(playerDamageReceive)
                    .mark(bombDamageReceive)
                    .mark(enemyDamageGive);
        }

        public TypeComponentTupleList markAsMob(int health) {
            return markAsEnemy()
                    .setHealth(health)
                    .setDamageReceiveCommand(DamageReceiveCommands.TAKE_DAMAGE)
                    .setDrawOrder(DrawPlane.MIDGROUND, 101);
        }

        public TypeComponentTupleList markAsBoss() {
            return markAsEnemy()
                    .setDamageReceiveCommand(DamageReceiveCommands.TAKE_DAMAGE)
                    .setDeathCommand(DeathCommands.BOSS_DEATH)
                    .setDrawOrder(DrawPlane.MIDGROUND, 102);
        }

        public TypeComponentTupleList markAsEnemyProjectile() {
            return mark(bombDamageReceive)
                    .mark(enemyDamageGive);
        }

        public TypeComponentTupleList markAsPickup() {
            return mark(pickupDamageReceive);
        }

        public TypeComponentTupleList setAsFairyRed() {
            return setSpriteInstruction(new SpriteInstruction("fairy_red_1"))
                    .setAnimation(new AnimationComponent(
                            new Animation(true, "fairy_red_1", "fairy_red_2", "fairy_red_3", "fairy_red_4"),
                            10
                    ));
        }

        public TypeComponentTupleList setAsFairyOrange() {
            return setSpriteInstruction(new SpriteInstruction("fairy_orange_1"))
                    .setAnimation(new AnimationComponent(
                            new Animation(true, "fairy_orange_1", "fairy_orange_2", "fairy_orange_3", "fairy_orange_4"),
                            10
                    ));
        }

        public TypeComponentTupleList setAsFairyYellow() {
            return setSpriteInstruction(new SpriteInstruction("fairy_yellow_1"))
                    .setAnimation(new AnimationComponent(
                            new Animation(true, "fairy_yellow_1", "fairy_yellow_2", "fairy_yellow_3", "fairy_yellow_4"),
                            10
                    ));
        }

        public TypeComponentTupleList setAsWheel() {
            return setSpriteInstruction(new SpriteInstruction("wheel"))
                    .setConstantSpriteRotation(6);
        }

        public TypeComponentTupleList setAsSmokeball() {
            return setSpriteInstruction(new SpriteInstruction("smokeball"))
                    .setTrail(new TrailComponent(15, "smokeball_trail"));
        }

        public TypeComponentTupleList setAsBat() {
            return setSpriteInstruction(new SpriteInstruction("bat_1", new ConstCartesianVector(0, -12)))
                    .setAnimation(new AnimationComponent(
                            new Animation(true, "bat_1", "bat_2"),
                            7
                    ));
        }

        public TypeComponentTupleList setAsWing() {
            return setSpriteInstruction(new SpriteInstruction("wing_1", new ConstCartesianVector(0, 0)))
                    .setAnimation(new AnimationComponent(
                            new Animation(true, "wing_1", "wing_2", "wing_3", "wing_4", "wing_5", "wing_6"),
                            7
                    ));
        }

        public TypeComponentTupleList setAsRobo() {
            return setSpriteInstruction(new SpriteInstruction("robo_1", new ConstCartesianVector(0, 16)))
                    .setAnimation(new AnimationComponent(
                            new Animation(true, "robo_1", "robo_2", "robo_3"),
                            4
                    ));
        }

        public TypeComponentTupleList setAsVirion() {
            return setSpriteInstruction(new SpriteInstruction("robo_1", new ConstCartesianVector(0, 0)))
                    .setAnimation(new AnimationComponent(
                            new Animation(true, "virion_1", "virion_2", "virion_3", "virion_4"),
                            10
                    ));
        }

        public TypeComponentTupleList setAsFlame() {
            return setSpriteInstruction(new SpriteInstruction("flame_1", new ConstCartesianVector(0, -10)))
                    .setAnimation(new AnimationComponent(
                            new Animation(true, "flame_1", "flame_2", "flame_3", "flame_4")
                    ));
        }

        public TypeComponentTupleList setAsSpike() {
            return setSpriteInstruction(new SpriteInstruction("spike_1", new ConstCartesianVector(0, 0)))
                    .setAnimation(new AnimationComponent(
                            new Animation(true, "spike_1", "spike_2", "spike_3", "spike_4")
                    ));
        }

        public TypeComponentTupleList setAsAppearAnimation() {
            return setSpriteInstruction(new SpriteInstruction("death_animation_7"))
                    .setAnimation(new AnimationComponent(
                                    new Animation(false,
                                            "death_animation_7",
                                            "death_animation_6",
                                            "death_animation_5",
                                            "death_animation_4",
                                            "death_animation_3",
                                            "death_animation_2",
                                            "death_animation_1"
                                    ),
                                    2
                            )
                    );
        }

        public Message<AddEntityOrder> packageAsMessage() {
            return ECSUtil.makeAddEntityMessage(new AddEntityOrder(toArray(new TypeComponentTuple[0])));
        }

        public Message<AddEntityOrder> packageAsNamedMessage(String name) {
            return ECSUtil.makeAddEntityMessage(new AddEntityOrder(toArray(new TypeComponentTuple[0]), name));
        }
    }
}
