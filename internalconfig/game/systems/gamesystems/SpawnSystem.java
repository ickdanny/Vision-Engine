package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.component.componentstorage.AbstractGroup;
import ecs.component.componentstorage.ComponentIterator;
import ecs.datastorage.AbstractDataStorage;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.SpawnComponent;
import internalconfig.game.components.SpawnUnit;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.systems.Difficulty;
import internalconfig.game.systems.GameConfigObject;
import internalconfig.game.systems.GameMode;
import internalconfig.game.systems.ShotType;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnHandlers;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Iterator;

import static internalconfig.game.components.ComponentTypes.*;

public class SpawnSystem implements AbstractSystem<Double> {

    //games the classloader into loading the spawnhandlers, which otherwise constituted a major source of lag (~150ms)
    @SuppressWarnings("FieldCanBeLocal")
    private static SpawnHandlers dummySpawnHandlers = new SpawnHandlers(
            GAME_COMPONENT_TYPES,
            new GameConfigObject()
                    .setGameMode(GameMode.STORY)
                    .setDifficulty(Difficulty.EASY)
                    .setRandom(1)
                    .setShotType(ShotType.A)
                    .setStage(1)
    );

    private final AbstractComponentType<SpawnComponent> spawnComponentType;

    private final AbstractComponentTypeContainer componentTypeContainer;

    public SpawnSystem(AbstractComponentTypeContainer componentTypeContainer) {
        spawnComponentType = componentTypeContainer.getTypeInstance(SpawnComponentType.class);
        this.componentTypeContainer = componentTypeContainer;
        dummySpawnHandlers = null;
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double> {

        private AbstractGroup group;
        private SpawnHandlers spawnHandlers; //spawnHandlers can differ based on game settings

        private Instance() {
            group = null;
            spawnHandlers = null;
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            if (group == null) {
                getGroup(dataStorage);
            }
            if (spawnHandlers == null) {
                makeSpawnHandlers(ecsInterface);
            }

            ComponentIterator<SpawnComponent> spawnItr = group.getComponentIterator(spawnComponentType);
            while (spawnItr.hasNext()) {
                SpawnComponent spawnComponent = spawnItr.next();
                int entityID = spawnItr.entityIDOfPreviousComponent();
                Iterator<SpawnUnit> spawnUnitItr = spawnComponent.iterator();
                while (spawnUnitItr.hasNext()) {
                    SpawnUnit spawnUnit = spawnUnitItr.next();
                    int tick = spawnUnit.stepAndGetTick();
                    if (tick <= 0) {
                        spawnUnitItr.remove();
                    } else {
                        Spawns spawn = spawnUnit.getSpawn();
                        spawnHandlers.handleSpawn(ecsInterface, spawn, tick, entityID);
                    }
                }
            }

            sliceBoard.ageAndCullMessages();
        }

        private void getGroup(AbstractDataStorage dataStorage) {
            group = dataStorage.createGroup(spawnComponentType);
        }

        private void makeSpawnHandlers(AbstractECSInterface ecsInterface) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            GameConfigObject gameConfigObject = GameUtil.getGameConfigObject(globalBoard);
            spawnHandlers = new SpawnHandlers(componentTypeContainer, gameConfigObject);
        }
    }
}
