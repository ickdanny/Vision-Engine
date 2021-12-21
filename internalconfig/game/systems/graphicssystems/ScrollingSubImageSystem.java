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
import ecs.system.criticalorders.SetComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.ScrollingSubImageComponent;
import util.messaging.AbstractPublishSubscribeBoard;

import java.awt.*;

public class ScrollingSubImageSystem implements AbstractSystem<Double>{

    private final AbstractComponentType<Rectangle> spriteSubImageComponentType;
    private final AbstractComponentType<ScrollingSubImageComponent> scrollingSubImageComponentType;

    public ScrollingSubImageSystem(AbstractComponentTypeContainer componentTypeContainer){
        spriteSubImageComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.SpriteSubImageComponentType.class);
        scrollingSubImageComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.ScrollingSubImageComponentType.class);
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double>{
        private AbstractGroup group;

        private Instance() {
            group = null;
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            if (group == null) {
                getGroup(dataStorage);
            }

            ComponentIterator<Rectangle> spriteSubImageItr = group.getComponentIterator(spriteSubImageComponentType);
            ComponentIterator<ScrollingSubImageComponent> scrollingSubImageItr = group.getComponentIterator(scrollingSubImageComponentType);

            while(spriteSubImageItr.hasNext() && scrollingSubImageItr.hasNext()){


                Rectangle subImage = spriteSubImageItr.next();
                ScrollingSubImageComponent scrollingSubImageComponent = scrollingSubImageItr.next();

                int entityID = spriteSubImageItr.entityIDOfPreviousComponent();
                EntityHandle handle = dataStorage.makeHandle(entityID);

                updateScrollingSubImageComponent(scrollingSubImageComponent);
                subImage.setBounds(
                        (int)scrollingSubImageComponent.getX(),
                        (int)scrollingSubImageComponent.getY(),
                        (int)scrollingSubImageComponent.getWidth(),
                        (int)scrollingSubImageComponent.getHeight()
                );

                sliceBoard.publishMessage(ECSUtil.makeSetComponentMessage(new SetComponentOrder<>(handle, spriteSubImageComponentType, subImage)));
            }

            if(spriteSubImageItr.hasNext() || scrollingSubImageItr.hasNext()){
                throw new RuntimeException("unexpected extra component in iterator");
            }

            sliceBoard.ageAndCullMessages();
        }

        private void updateScrollingSubImageComponent(ScrollingSubImageComponent scrollingSubImageComponent){
            scrollingSubImageComponent.setX(Math.max(0, scrollingSubImageComponent.getX() + scrollingSubImageComponent.getXVelocity()));
            scrollingSubImageComponent.setY(Math.max(0, scrollingSubImageComponent.getY() + scrollingSubImageComponent.getYVelocity()));
            scrollingSubImageComponent.setWidth(Math.max(0, scrollingSubImageComponent.getWidth() + scrollingSubImageComponent.getWidthVelocity()));
            scrollingSubImageComponent.setHeight(Math.max(0, scrollingSubImageComponent.getHeight() + scrollingSubImageComponent.getHeightVelocity()));
        }


        private void getGroup(AbstractDataStorage dataStorage){
            group = dataStorage.createGroup(spriteSubImageComponentType, scrollingSubImageComponentType);
        }
    }
}