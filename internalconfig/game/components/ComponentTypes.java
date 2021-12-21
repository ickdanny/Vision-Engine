package internalconfig.game.components;

import ecs.component.AbstractComponentType;
import ecs.entity.EntityHandle;
import internalconfig.game.components.spawns.Spawns;
import util.math.geometry.AABB;
import util.math.geometry.TwoFramePosition;
import util.messaging.Topic;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class ComponentTypes {

    public static class MenuCommandComponentType_Up extends MenuCommandComponentType{}
    public static class MenuCommandComponentType_Down extends MenuCommandComponentType{}
    public static class MenuCommandComponentType_Left extends MenuCommandComponentType{}
    public static class MenuCommandComponentType_Right extends MenuCommandComponentType{}
    public static class MenuCommandComponentType_Select extends MenuCommandComponentType{}

    public static class NeighboringElementComponentType_Up extends EntityHandleComponentType{}
    public static class NeighboringElementComponentType_Down extends EntityHandleComponentType{}
    public static class NeighboringElementComponentType_Left extends EntityHandleComponentType{}
    public static class NeighboringElementComponentType_Right extends EntityHandleComponentType{}

    public static class DefaultSelectedElementMarker extends Marker{}

    public static class ButtonComponentType extends AbstractComponentTypeTemplate<ButtonData>{}
    public static class ButtonActionType extends StringComponentType{}
    public static class LockConditionComponentType extends AbstractComponentTypeTemplate<LockConditions>{}

    public static class SpriteInstructionComponentType extends AbstractComponentTypeTemplate<SpriteInstruction>{}
    public static class SpriteSubImageComponentType extends AbstractComponentTypeTemplate<Rectangle>{}
    public static class SpriteComponentType extends AbstractComponentTypeTemplate<BufferedImage>{}
    public static class VisibleMarker extends Marker{}
    public static class DrawOrderComponentType extends AbstractComponentTypeTemplate<DrawOrder>{}
    public static class AnimationComponentType extends AbstractComponentTypeTemplate<AnimationComponent>{}
    public static class RotateSpriteForwardMarker extends Marker{}
    public static class ConstantSpriteRotationComponentType extends DoubleComponentType{}
    public static class SinusoidalSpriteVerticalOffsetComponentType extends AbstractComponentTypeTemplate<SinusoidalSpriteVerticalOffsetComponent>{}
    public static class ScrollingSubImageComponentType extends AbstractComponentTypeTemplate<ScrollingSubImageComponent>{}
    public static class MakeOpaqueWhenPlayerFocusedAndAliveMarker extends Marker{}
    public static class TrailComponentType extends AbstractComponentTypeTemplate<TrailComponent>{}

    public static class PositionComponentType extends AbstractComponentTypeTemplate<TwoFramePosition>{}
    public static class VelocityComponentType extends AbstractComponentTypeTemplate<VelocityComponent>{}

    public static class GamePlaneMarker extends Marker{}
    public static class InboundComponentType extends DoubleComponentType{}
    public static class OutboundComponentType extends DoubleComponentType{}

    public static class HitboxComponentType extends AbstractComponentTypeTemplate<AABB>{}
    public static class CollidableMarker extends Marker{}

    public static class HealthComponentType extends AbstractComponentTypeTemplate<HealthComponent>{}
    public static class DamageComponentType extends IntComponentType{}

    public static class PickupTypeComponentType extends AbstractComponentTypeTemplate<PickupTypes>{}
    public static class PickupDataComponentType extends IntComponentType{}

    public static class SpawnComponentType extends AbstractComponentTypeTemplate<SpawnComponent>{}
    public static class ProgramComponentType extends AbstractComponentTypeTemplate<ProgramComponent>{}

    public static class PlayerDamage{
        private PlayerDamage(){}
        public static class Give extends Marker{}
        public static class Receive extends Marker{}
    }
    public static class BombDamage{
        private BombDamage(){}
        public static class Give extends Marker{}
        public static class Receive extends Marker{}
    }
    public static class EnemyDamage{
        private EnemyDamage(){}
        public static class Give extends Marker{}
        public static class Receive extends Marker{}
    }
    public static class PickupDamage{
        private PickupDamage(){}
        public static class Give extends Marker{}
        public static class Receive extends Marker{}
    }
    public static class BulletSlowDamage{
        private BulletSlowDamage(){}
        public static class Give extends Marker{}
        public static class Receive extends Marker{}
    }

    public static class DamageGiveCommandComponentType extends AbstractComponentTypeTemplate<DamageGiveCommands>{}
    public static class DamageReceiveCommandComponentType extends AbstractComponentTypeTemplate<DamageReceiveCommands>{}
    public static class DeathCommandComponentType extends AbstractComponentTypeTemplate<DeathCommands>{}
    public static class DeathSpawnComponentType extends AbstractComponentTypeTemplate<Spawns>{}


    //componentTypeContainer sets indices correctly
    public static final AbstractComponentTypeContainer MENU_COMPONENT_TYPES = new ComponentTypeContainer(
            new MenuCommandComponentType_Up(),
            new MenuCommandComponentType_Down(),
            new MenuCommandComponentType_Left(),
            new MenuCommandComponentType_Right(),
            new MenuCommandComponentType_Select(),

            new NeighboringElementComponentType_Up(),
            new NeighboringElementComponentType_Down(),
            new NeighboringElementComponentType_Left(),
            new NeighboringElementComponentType_Right(),

            new DefaultSelectedElementMarker(),

            new ButtonComponentType(),
            new ButtonActionType(),
            new LockConditionComponentType(),

            new SpriteInstructionComponentType(),
            new SpriteSubImageComponentType(),
            new SpriteComponentType(),
            new VisibleMarker(),
            new DrawOrderComponentType(),
            new AnimationComponentType(),
            new RotateSpriteForwardMarker(),
            new ConstantSpriteRotationComponentType(),

            new PositionComponentType(),
            new VelocityComponentType(),
            new OutboundComponentType() //for shot preview
    );

    public static final AbstractComponentTypeContainer DIALOGUE_COMPONENT_TYPES = new ComponentTypeContainer(
            new SpriteInstructionComponentType(),
            new SpriteSubImageComponentType(),
            new SpriteComponentType(),
            new VisibleMarker(),
            new DrawOrderComponentType(),
            new AnimationComponentType(),
            new RotateSpriteForwardMarker(),
            new ConstantSpriteRotationComponentType(),

            new PositionComponentType(),
            new VelocityComponentType()
    );

    public static final AbstractComponentTypeContainer LOAD_COMPONENT_TYPES = new ComponentTypeContainer(
            new SpriteInstructionComponentType(),
            new SpriteSubImageComponentType(),
            new SpriteComponentType(),
            new VisibleMarker(),
            new DrawOrderComponentType(),

            new PositionComponentType(),
            new VelocityComponentType()
    );

    public static final AbstractComponentTypeContainer CREDITS_COMPONENT_TYPES = new ComponentTypeContainer(
            new SpriteInstructionComponentType(),
            new SpriteSubImageComponentType(),
            new SpriteComponentType(),
            new VisibleMarker(),
            new DrawOrderComponentType(),

            new PositionComponentType(),
            new VelocityComponentType()
    );

    public static final AbstractComponentTypeContainer GAME_COMPONENT_TYPES = new ComponentTypeContainer(
            new SpriteInstructionComponentType(),
            new SpriteSubImageComponentType(),
            new SpriteComponentType(),
            new VisibleMarker(),
            new DrawOrderComponentType(),
            new AnimationComponentType(),
            new RotateSpriteForwardMarker(),
            new ConstantSpriteRotationComponentType(),
            new SinusoidalSpriteVerticalOffsetComponentType(),
            new ScrollingSubImageComponentType(),
            new MakeOpaqueWhenPlayerFocusedAndAliveMarker(),
            new TrailComponentType(),
            new GamePlaneMarker(),

            new PositionComponentType(),
            new VelocityComponentType(),

            new InboundComponentType(),
            new OutboundComponentType(),

            new HitboxComponentType(),
            new CollidableMarker(),

            new SpawnComponentType(),
            new ProgramComponentType(),

            new PlayerDamage.Give(),
            new PlayerDamage.Receive(),
            new BombDamage.Give(),
            new BombDamage.Receive(),
            new EnemyDamage.Give(),
            new EnemyDamage.Receive(),
            new PickupDamage.Give(),
            new PickupDamage.Receive(),
            new BulletSlowDamage.Give(),
            new BulletSlowDamage.Receive(),

            new DamageGiveCommandComponentType(),
            new DamageReceiveCommandComponentType(),
            new DeathCommandComponentType(),
            new DeathSpawnComponentType(),

            new PickupTypeComponentType(),
            new PickupDataComponentType(),

            new DamageComponentType(),
            new HealthComponentType()
    );

    public abstract static class AbstractComponentTypeTemplate<T> implements AbstractComponentType<T> {
        private int index;
        private final Topic<EntityHandle> setComponentTopic;
        private final Topic<EntityHandle> removeComponentTopic;

        public AbstractComponentTypeTemplate() {
            setComponentTopic = new Topic<>();
            removeComponentTopic = new Topic<>();
        }

        @Override
        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public Topic<EntityHandle> getSetComponentTopic() {
            return setComponentTopic;
        }

        @Override
        public Topic<EntityHandle> getRemoveComponentTopic() {
            return removeComponentTopic;
        }
    }
    private static class IntComponentType extends AbstractComponentTypeTemplate<Integer>{}
    private static class DoubleComponentType extends AbstractComponentTypeTemplate<Double>{}
    private static class StringComponentType extends AbstractComponentTypeTemplate<String>{}
    private static class MenuCommandComponentType extends AbstractComponentTypeTemplate<MenuCommands>{}
    private static class EntityHandleComponentType extends AbstractComponentTypeTemplate<EntityHandle>{}
    private static class Marker extends AbstractComponentTypeTemplate<Void> {
        @Override
        public boolean isMarker() {
            return true;
        }
    }
}
