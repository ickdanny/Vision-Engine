package internalconfig.game.systems.graphicssystems;

import ecs.AbstractECSInterface;
import ecs.system.AbstractSingleInstanceSystem;
import internalconfig.game.components.DrawPlane;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static internalconfig.game.systems.Topics.*;

public class GraphicsSystem extends AbstractSingleInstanceSystem<Double> {
    protected final BufferedImage toDraw;

    public GraphicsSystem(BufferedImage toDraw){
        this.toDraw = toDraw;
    }

    @Override
    public void run(AbstractECSInterface ecsInterface, Double deltaTime) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        drawDrawCommands(sliceBoard, deltaTime, toDraw);
    }

    protected void drawDrawCommands(AbstractPublishSubscribeBoard sliceBoard, double deltaTime, BufferedImage image){
        if(sliceBoard.hasTopicalMessages(DRAW_COMMANDS)) {
            List<Message<DrawCommand>> drawCommandMessageList = sliceBoard.getMessageList(DRAW_COMMANDS);
            if(drawCommandMessageList.size() == 0){
                return;
            }

            Graphics2D g2d = image.createGraphics();

            for (Message<DrawCommand> drawCommandMessage : drawCommandMessageList){
                DrawCommand drawCommand = drawCommandMessage.getMessage();
                drawDrawCommand(drawCommand, deltaTime, g2d);
            }
            g2d.dispose();
        }
    }

    private void drawDrawCommand(DrawCommand command, double deltaTime, Graphics2D g2d){
        BufferedImage image = command.getImage();
        DoublePoint truePos = calculateTruePosition(command, deltaTime);
        drawImage(image, truePos, g2d);
    }

    private DoublePoint calculateTruePosition(DrawCommand command, double deltaTime){
        BufferedImage image = command.getImage();
        DoublePoint pos = command.getPosition();
        if(command.hasVelocity()){
            pos = command.getVelocity().lerp(pos, deltaTime);
        }
        if (command.hasOffset()) {
            pos = command.getOffset().add(pos);
        }
        DrawPlane.DrawFrom drawFrom = command.getOrder().getPlane().getDrawFrom();
        if (drawFrom == DrawPlane.DrawFrom.MIDDLE) {
            pos = new DoublePoint(
                    pos.getX() - (image.getWidth() / 2d),
                    pos.getY() - (image.getHeight() / 2d)
            );
        }
        return pos;
    }

    private void drawImage(BufferedImage image, DoublePoint pos, Graphics2D g2d){
        g2d.drawImage(image, (int)pos.getX(), (int)pos.getY(), null);
    }
}