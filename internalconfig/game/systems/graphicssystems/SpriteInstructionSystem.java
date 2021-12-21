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
import internalconfig.game.components.DrawOrder;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.components.TextSpriteInstruction;
import resource.AbstractResourceManager;
import resource.Resource;
import util.image.ImageUtil;
import util.math.geometry.AbstractVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.awt.*;
import java.awt.image.BufferedImage;

import static internalconfig.game.components.SpriteInstruction.*;
import static internalconfig.game.components.ComponentTypes.*;

public class SpriteInstructionSystem implements AbstractSystem<Double> {

    private static final Font FONT = new Font("Monospaced", Font.BOLD, 17);

    private final AbstractResourceManager<BufferedImage> imageManager;

    private final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;
    private final AbstractComponentType<Rectangle> spriteSubImageComponentType;
    private final AbstractComponentType<DrawOrder> drawOrderComponentType;
    private final AbstractComponentType<Void> visibleMarker;

    private final AbstractComponentType<BufferedImage> spriteComponentType;

    public SpriteInstructionSystem(AbstractResourceManager<BufferedImage> imageManager,
                                   AbstractComponentTypeContainer componentTypeContainer) {
        this.imageManager = imageManager;
        spriteInstructionComponentType = componentTypeContainer.getTypeInstance(SpriteInstructionComponentType.class);
        spriteSubImageComponentType = componentTypeContainer.getTypeInstance(SpriteSubImageComponentType.class);
        visibleMarker = componentTypeContainer.getTypeInstance(VisibleMarker.class);
        drawOrderComponentType = componentTypeContainer.getTypeInstance(DrawOrderComponentType.class);
        spriteComponentType = componentTypeContainer.getTypeInstance(SpriteComponentType.class);
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
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            if (group == null) {
                getGroup(dataStorage);
            }

            ComponentIterator<SpriteInstruction> itr = group.getComponentIterator(spriteInstructionComponentType);
            while (itr.hasNext()) {
                SpriteInstruction spriteInstruction = itr.next();
                EntityHandle handle = dataStorage.makeHandle(itr.entityIDOfPreviousComponent());

                if (!spriteInstruction.isUpdated() || !dataStorage.containsComponent(handle, spriteComponentType)) {
                    if (dataStorage.containsComponent(handle, spriteSubImageComponentType)) {
                        Rectangle subImage = dataStorage.getComponent(handle, spriteSubImageComponentType);
                        sliceBoard.publishMessage(ECSUtil.makeSetComponentMessage(new SetComponentOrder<>(
                                handle,
                                spriteComponentType,
                                makeImage(spriteInstruction, subImage)
                        )));
                    } else {
                        sliceBoard.publishMessage(ECSUtil.makeSetComponentMessage(new SetComponentOrder<>(
                                handle,
                                spriteComponentType,
                                makeImage(spriteInstruction)
                        )));
                    }
                    spriteInstruction.update();
                }
            }

            sliceBoard.ageAndCullMessages();
        }

        private void getGroup(AbstractDataStorage dataStorage) {
            group = dataStorage.createGroup(spriteInstructionComponentType, drawOrderComponentType, visibleMarker);
        }

        private BufferedImage makeImage(SpriteInstruction spriteInstruction) {
            String sImage = spriteInstruction.getImage();

            BufferedImage image;
            if(!(spriteInstruction instanceof TextSpriteInstruction)) {
                Resource<BufferedImage> imageResource = imageManager.getResource(sImage);
                if (imageResource == null) {
                    throw new RuntimeException("Could not get image resource from string: " + sImage);
                }
                image = imageResource.getData();
            } else {
                TextSpriteInstruction textSpriteInstruction = (TextSpriteInstruction)spriteInstruction;
                image = ImageUtil.textToImage(sImage, textSpriteInstruction.getCharsPerLine(), FONT, Color.WHITE);
            }

            double scale = spriteInstruction.getScale();
            double rotation = spriteInstruction.getRotation().getAngle();
            double transparency = spriteInstruction.getTransparency();
            AbstractVector offset = spriteInstruction.getOffset();

            if (scale != DEFAULT_SCALE) {
                image = ImageUtil.scaleImage(image, scale);
            }
            if (rotation != DEFAULT_ROTATION) {
                image = ImageUtil.rotateImage(image, rotation);
            }
            if (transparency != DEFAULT_TRANSPARENCY) {
                image = ImageUtil.transparentImage(image, transparency);
            }
            if (!offset.isZero()) {
                image = ImageUtil.offsetImage(image, (int) offset.getX(), (int) offset.getY());
            }

            return image;
        }

        private BufferedImage makeImage(SpriteInstruction spriteInstruction, Rectangle subImage) {
            String sImage = spriteInstruction.getImage();
            Resource<BufferedImage> imageResource = imageManager.getResource(sImage);
            if (imageResource == null) {
                throw new RuntimeException("Could not get image resource from string: " + sImage);
            }
            BufferedImage image = imageResource.getData();

            image = ImageUtil.subImage(image, subImage);

            double scale = spriteInstruction.getScale();
            double rotation = spriteInstruction.getRotation().getAngle();
            double transparency = spriteInstruction.getTransparency();
            AbstractVector offset = spriteInstruction.getOffset();

            if (scale != DEFAULT_SCALE) {
                image = ImageUtil.scaleImage(image, scale);
            }
            if (rotation != DEFAULT_ROTATION) {
                image = ImageUtil.rotateImage(image, rotation);
            }
            if (transparency != DEFAULT_TRANSPARENCY) {
                image = ImageUtil.transparentImage(image, transparency);
            }
            if (!offset.isZero()) {
                image = ImageUtil.offsetImage(image, (int) offset.getX(), (int) offset.getY());
            }

            return image;
        }
    }
}

