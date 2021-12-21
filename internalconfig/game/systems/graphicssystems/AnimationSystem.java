package internalconfig.game.systems.graphicssystems;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.component.componentstorage.AbstractGroup;
import ecs.component.componentstorage.ComponentIterator;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import ecs.system.criticalorders.RemoveComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.Animation;
import internalconfig.game.components.AnimationComponent;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.components.VelocityComponent;
import util.Ticker;
import util.math.geometry.AbstractVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.ComponentTypes.*;

@SuppressWarnings("unused")
public class AnimationSystem implements AbstractSystem<Double> {

    private final AbstractComponentType<AnimationComponent> animationComponentType;
    private final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;
    private final AbstractComponentType<VelocityComponent> velocityComponentType;

    public AnimationSystem(AbstractComponentTypeContainer componentTypeContainer) {
        animationComponentType = componentTypeContainer.getTypeInstance(AnimationComponentType.class);
        spriteInstructionComponentType = componentTypeContainer.getTypeInstance(SpriteInstructionComponentType.class);
        velocityComponentType = componentTypeContainer.getTypeInstance(VelocityComponentType.class);
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double> {

        private AbstractGroup group;

        private Instance() {
            group = null;
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            if (group == null) {
                getGroup(dataStorage);
            }

            ComponentIterator<AnimationComponent> itr = group.getComponentIterator(animationComponentType);
            while (itr.hasNext()) {
                AnimationComponent animationComponent = itr.next();
                Ticker ticker = animationComponent.getTicker();
                if(ticker.stepAndGetTick() == 1) {
                    EntityHandle handle = dataStorage.makeHandle(itr.entityIDOfPreviousComponent());

                    handleAnimation(dataStorage, ecsInterface.getSliceBoard(), animationComponent, handle);
                }
            }
            ecsInterface.getSliceBoard().ageAndCullMessages();
        }

        private void handleAnimation(
                AbstractDataStorage dataStorage,
                AbstractPublishSubscribeBoard sliceBoard,
                AnimationComponent animationComponent,
                EntityHandle handle
        ) {
            boolean removeComponent = false;
            if (!handleTurning(dataStorage, animationComponent, handle)) {
                removeComponent = stepAnimation(animationComponent);
            }
            if (dataStorage.containsComponent(handle, spriteInstructionComponentType)) {
                SpriteInstruction spriteInstruction = dataStorage.getComponent(handle, spriteInstructionComponentType);
                spriteInstruction.setImage(animationComponent.getCurrentAnimation().getCurrentFrame());
            }
            if (removeComponent) {
                sliceBoard.publishMessage(ECSUtil.makeRemoveComponentMessage(
                        new RemoveComponentOrder(handle, animationComponentType)
                ));
            }
        }

        private boolean handleTurning( //returns true if sprite changed
                                       AbstractDataStorage dataStorage,
                                       AnimationComponent animationComponent,
                                       EntityHandle handle
        ) {
            Animation[] animations = animationComponent.getAnimations();
            if (animations.length > 1) {
                if (dataStorage.containsComponent(handle, velocityComponentType)) {
                    AbstractVector velocity = dataStorage.getComponent(handle, velocityComponentType).getVelocity();
                    if (velocity.getX() < -ANIMATION_VELOCITY_EPSILON) {
                        return tryToTurnLeft(animationComponent, animations);
                    } else if (velocity.getX() > ANIMATION_VELOCITY_EPSILON) {
                        return tryToTurnRight(animationComponent, animations);
                    } else {
                        return tryToTurnToCenter(animationComponent, animations);
                    }
                }
            }
            return false;
        }

        private boolean tryToTurnLeft(AnimationComponent animationComponent, Animation[] animations) {
            int currentIndex = animationComponent.getCurrentIndex();
            if (currentIndex > 0) {
                int nextIndex = currentIndex - 1;

                animations[currentIndex].setCurrentIndex(0);
                animations[nextIndex].setCurrentIndex(0);

                animationComponent.setCurrentIndex(nextIndex);
                return true;
            }
            return false;
        }

        private boolean tryToTurnRight(AnimationComponent animationComponent, Animation[] animations) {
            int currentIndex = animationComponent.getCurrentIndex();
            if (currentIndex < animations.length - 1) {
                int nextIndex = currentIndex + 1;

                animations[currentIndex].setCurrentIndex(0);
                animations[nextIndex].setCurrentIndex(0);

                animationComponent.setCurrentIndex(nextIndex);
                return true;
            }
            return false;
        }

        private boolean tryToTurnToCenter(AnimationComponent animationComponent, Animation[] animations) {
            int currentIndex = animationComponent.getCurrentIndex();
            int idleIndex = animationComponent.getIdleIndex();
            if (currentIndex != idleIndex) {
                if (currentIndex < idleIndex) {
                    return tryToTurnRight(animationComponent, animations);
                } else {
                    return tryToTurnLeft(animationComponent, animations);
                }
            }
            return false;
        }

        //returns true if end of animation
        private boolean stepAnimation(AnimationComponent animationComponent) {
            Animation currentAnimation = animationComponent.getCurrentAnimation();
            int nextFrame = currentAnimation.getCurrentIndex() + 1;
            if (nextFrame >= currentAnimation.getFrames().length) {
                if (!currentAnimation.isLooping()) {
                    return true;
                }
                nextFrame = 0;
            }
            currentAnimation.setCurrentIndex(nextFrame);
            return false;
        }

        private void getGroup(AbstractDataStorage dataStorage) {
            group = dataStorage.createGroup(animationComponentType);
        }
    }
}
