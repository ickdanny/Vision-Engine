package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.component.componentstorage.AbstractGroup;
import ecs.component.componentstorage.ComponentIterator;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import util.math.geometry.AABB;
import internalconfig.game.components.AbstractComponentTypeContainer;
import util.math.geometry.TwoFramePosition;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;
import util.spatialpartition.AbstractSpatialPartition;
import util.spatialpartition.QuadTree;
import util.tuple.Tuple2;

import java.util.List;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.systems.Topics.COLLISIONS;

@SuppressWarnings("unused")
public class CollisionSystem implements AbstractSystem<Double> {

    private final AbstractComponentType<TwoFramePosition> positionComponentType;
    private final AbstractComponentType<AABB> hitboxComponentType;
    private final AbstractComponentType<Void> collidableMarker;

    public CollisionSystem(AbstractComponentTypeContainer componentTypeContainer){
        positionComponentType = componentTypeContainer.getTypeInstance(PositionComponentType.class);
        hitboxComponentType = componentTypeContainer.getTypeInstance(HitboxComponentType.class);
        collidableMarker = componentTypeContainer.getTypeInstance(CollidableMarker.class);
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

            if(group == null){
                getGroup(dataStorage);
            }

            checkCollisions(dataStorage, sliceBoard);

            sliceBoard.ageAndCullMessages();
        }

        private void checkCollisions(AbstractDataStorage dataStorage, AbstractPublishSubscribeBoard sliceBoard){
            AbstractSpatialPartition<EntityHandle> spatialPartition = new QuadTree<>(COLLISION_BOUNDS);

            ComponentIterator<TwoFramePosition> posItr = group.getComponentIterator(positionComponentType);
            ComponentIterator<AABB> hitboxItr = group.getComponentIterator(hitboxComponentType);

            while(posItr.hasNext() && hitboxItr.hasNext()){
                TwoFramePosition pos = posItr.next();
                AABB hitbox = hitboxItr.next();
                EntityHandle handle = dataStorage.makeHandle(posItr.entityIDOfPreviousComponent());
                //the following line does not work with generic arrays
                List<EntityHandle> collisions = spatialPartition.insertAndReturnCollisions(handle, hitbox, pos);
                for(EntityHandle collided : collisions){
                    sliceBoard.publishMessage(makeCollisionMessage(handle, collided, dataStorage.getMessageLifetime()));
                }
            }

            if(posItr.hasNext() || hitboxItr.hasNext()){
                throw new RuntimeException();
            }
        }

        private Message<Tuple2<EntityHandle, EntityHandle>> makeCollisionMessage(
                EntityHandle a, EntityHandle b, int messageLifetime){
            return new Message<>(COLLISIONS, new Tuple2<>(a, b), messageLifetime);
        }

        private void getGroup(AbstractDataStorage dataStorage){
            group = dataStorage.createGroup(positionComponentType, hitboxComponentType, collidableMarker);
        }
    }
}
