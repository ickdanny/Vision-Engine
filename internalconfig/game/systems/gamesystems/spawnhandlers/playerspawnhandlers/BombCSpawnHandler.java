package internalconfig.game.systems.gamesystems.spawnhandlers.playerspawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DrawPlane;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.geometry.AABB;
import util.math.geometry.AbstractVector;
import util.math.geometry.CartesianVector;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.*;

class BombCSpawnHandler extends AbstractBombSpawnHandler {

    private static final double SPEED = 20;
    private static final AABB HITBOX = new AABB(WIDTH / 2d, 50);
    private static final double OUTBOUND = -110;
    private static final int DAMAGE = 20;

    public BombCSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, 120 / 8)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            DoublePoint pos = GameUtil.getPos(ecsInterface.getSliceData(), entityID, positionComponentType);
            DoublePoint spawnPos = new DoublePoint(WIDTH / 2d, pos.getY());

            AbstractVector upVelocity = new CartesianVector(0, -SPEED);
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightPathCollidable(spawnPos, upVelocity, HITBOX, OUTBOUND)
                            .markAsBomb()
                            .setDamage(DAMAGE)
                            .setDrawOrder(DrawPlane.MIDGROUND, 198)
                            .setSpriteInstruction(new SpriteInstruction("bomb_c_up"))
                            .packageAsMessage()
            );

            AbstractVector downVelocity = new CartesianVector(0, SPEED);
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightPathCollidable(spawnPos, downVelocity, HITBOX, OUTBOUND)
                            .markAsBomb()
                            .setDamage(DAMAGE)
                            .setDrawOrder(DrawPlane.MIDGROUND, 198)
                            .setSpriteInstruction(new SpriteInstruction("bomb_c_down"))
                            .packageAsMessage()
            );
        }
    }
}
