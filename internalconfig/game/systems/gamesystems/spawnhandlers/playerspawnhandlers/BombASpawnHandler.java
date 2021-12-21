package internalconfig.game.systems.gamesystems.spawnhandlers.playerspawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DrawPlane;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.components.spawns.PlayerSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.geometry.AABB;
import util.math.geometry.AbstractVector;
import util.math.geometry.CartesianVector;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.BOUNDARY_Y_LOW;
import static internalconfig.game.components.Instructions.DIE;
import static internalconfig.game.components.spawns.PlayerSpawns.BOMB_A_EXPLOSION;

class BombASpawnHandler extends AbstractBombSpawnHandler {

    private static final AABB HITBOX = new AABB(30, 70);
    private static final int DAMAGE = 600;
    private static final double SPEED = HEIGHT / 15d;

    private static final InstructionNode<?, ?>[] BOMB_A_PROGRAM =
            ProgramBuilder.linearLink(
                    new InstructionNode<>(BOUNDARY_Y_LOW, 0d),
                    DIE
            ).compile();

    public BombASpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tick != PlayerSpawns.BOMB.getDuration()) {
            return;
        }
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        DoublePoint pos = GameUtil.getPos(ecsInterface.getSliceData(), entityID, positionComponentType);

        AbstractVector velocity = new CartesianVector(0, -SPEED);

        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, velocity, HITBOX, NORMAL_OUTBOUND)
                        .markAsBomb()
                        .setDamage(DAMAGE)
                        .setProgram(BOMB_A_PROGRAM)
                        .setDeathSpawnWithCommandAndSpawnComponent(BOMB_A_EXPLOSION)
                        .setDrawOrder(DrawPlane.MIDGROUND, 198)
                        .setSpriteInstruction(new SpriteInstruction("bomb_a_spike"))
                        .packageAsMessage()
        );
    }
}
