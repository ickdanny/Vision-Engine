package internalconfig.game.systems.graphicssystems;

import ecs.AbstractECSInterface;
import util.messaging.AbstractPublishSubscribeBoard;

import java.awt.*;
import java.awt.image.BufferedImage;

import static internalconfig.game.systems.Topics.PAUSE_STATE;

public class GameGraphicsSystem extends GraphicsSystem{
    private BufferedImage savedImage;

    public GameGraphicsSystem(BufferedImage toDraw){
        super(toDraw);
        savedImage = null;
    }

    @Override
    public void run(AbstractECSInterface ecsInterface, Double deltaTime) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        if(sliceBoard.hasTopicalMessages(PAUSE_STATE)){
            if(savedImage == null){
                savedImage = new BufferedImage(toDraw.getWidth(), toDraw.getHeight(), toDraw.getType());
                drawDrawCommands(sliceBoard, 1d, savedImage);
            }
            Graphics2D g2d = toDraw.createGraphics();
            g2d.drawImage(savedImage, 0, 0, null);
            g2d.dispose();
        }
        else{
            savedImage = null;
            drawDrawCommands(sliceBoard, deltaTime, toDraw);
        }
    }
}
