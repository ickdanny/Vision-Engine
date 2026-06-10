package util.image;

import util.math.geometry.DoublePoint;
import util.math.interval.DoubleInterval;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Queue;

public final class ImageUtil {

    public static final DoubleInterval VALID_TRANSPARENCY_INTERVAL = DoubleInterval.makeInclusive(0.0, 1.0);

    private ImageUtil() {
    }

    public static void clearImage(BufferedImage bufferedImage) {
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setBackground(new Color(0, 0, 0, 0));
        g2d.clearRect(0, 0, bufferedImage.getWidth(),
                bufferedImage.getHeight());
        g2d.dispose();
        //taken from http://www.java2s.com/example/java/2d-graphics/clear-bufferedimage.html
    }

    public static void clearImage(BufferedImage bufferedImage, Color color) {
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setBackground(new Color(0, 0, 0, 0));
        g2d.clearRect(0, 0, bufferedImage.getWidth(),
                bufferedImage.getHeight());
        g2d.setColor(color);
        g2d.fillRect(0, 0, bufferedImage.getWidth(),
                bufferedImage.getHeight());
        g2d.dispose();
        //taken from http://www.java2s.com/example/java/2d-graphics/clear-bufferedimage.html
    }

    public static BufferedImage copyImage(BufferedImage toCopy) {
        BufferedImage copy = new BufferedImage(toCopy.getWidth(), toCopy.getHeight(), toCopy.getType());
        Graphics2D g2d = copy.createGraphics();
        g2d.drawImage(toCopy, 0, 0, null);
        g2d.dispose();
        return copy;
    }

    public static BufferedImage scaleImage(BufferedImage toScale, int newWidth, int newHeight) {
        if (toScale.getWidth() == newWidth && toScale.getHeight() == newHeight) {
            return toScale;
        }
        BufferedImage scaled = new BufferedImage(newWidth, newHeight, toScale.getType());
        Graphics2D g2d = scaled.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(toScale, 0, 0, newWidth, newHeight, 0, 0, toScale.getWidth(), toScale.getHeight(), null);
        g2d.dispose();
        return scaled;
    }

    public static BufferedImage scaleImage(BufferedImage toScale, double scale) {
        if (scale <= 0) {
            throw new IllegalArgumentException("scale cannot be <= 0");
        }
        if (scale == 1) {
            return toScale;
        }
        return scaleImage(toScale, (int) (scale * toScale.getWidth()), (int) (scale * toScale.getHeight()));
    }

    //angle in deg
    public static BufferedImage rotateImage(BufferedImage toRotate, double angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = toRotate.getWidth();
        int h = toRotate.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, toRotate.getType());
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2d, (newHeight - h) / 2d);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(toRotate, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    public static BufferedImage transparentImage(BufferedImage toTransparent, double transparency) {
        throwIfInvalidTransparency(transparency);

        int w = toTransparent.getWidth();
        int h = toTransparent.getHeight();

        BufferedImage transparent = new BufferedImage(w, h, toTransparent.getType());
        Graphics2D g2d = transparent.createGraphics();
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) transparency);
        g2d.setComposite(ac);
        g2d.drawImage(toTransparent, 0, 0, null);
        g2d.dispose();

        return transparent;
    }

    public static void throwIfInvalidTransparency(double transparency) {
        if (!isValidTransparency(transparency)) {
            throw new RuntimeException("invalid transparency value: " + transparency);
        }
    }

    public static boolean isValidTransparency(double transparency) {
        return VALID_TRANSPARENCY_INTERVAL.isInInterval(transparency);
    }

    public static BufferedImage offsetImage(BufferedImage toOffset, int x, int y) {
        if (x == 0 && y == 0) {
            return toOffset;
        }

        int w = toOffset.getWidth();
        int h = toOffset.getHeight();
        int drawX = Math.max(x, 0);
        int drawY = Math.max(y, 0);

        BufferedImage offset = new BufferedImage(w + Math.abs(x), h + Math.abs(y), toOffset.getType());
        Graphics2D g2d = offset.createGraphics();
        g2d.drawImage(toOffset, drawX, drawY, null);
        g2d.dispose();
        return offset;
    }

    public static BufferedImage subImage(BufferedImage toSubImage, Rectangle subImage) {
        return toSubImage.getSubimage(subImage.x, subImage.y, subImage.width, subImage.height);
    }

    public static BufferedImage textToImage(String text, int charsPerLine, Font font, Color color) {
        GraphicsConfiguration gfxConfig = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                getDefaultConfiguration();

        BufferedImage dummyImage = gfxConfig.createCompatibleImage(1, 1, Transparency.TRANSLUCENT);
        Graphics2D dummyGraphics = dummyImage.createGraphics();

        dummyGraphics.setFont(font);
        FontMetrics fontMetrics = dummyGraphics.getFontMetrics();

        int textWidth = 0;

        Queue<String> lines = new ArrayDeque<>();
        while (!text.isEmpty()) {
            String cutOff;
            if (text.length() <= charsPerLine) {
                textWidth = Math.max(textWidth, fontMetrics.stringWidth(text.concat("W")));
                lines.add(text);
                break;
            } else {
                cutOff = text.substring(0, charsPerLine);
            }
            int lastSpace = cutOff.lastIndexOf(' ');
            if (lastSpace != -1) {
                String wholeWords = cutOff.substring(0, lastSpace);
                textWidth = Math.max(textWidth, fontMetrics.stringWidth(wholeWords.concat("W")));
                lines.add(wholeWords);
                text = text.substring(lastSpace).trim();
            } else {
                textWidth = Math.max(textWidth, fontMetrics.stringWidth(cutOff.concat("W")));
                lines.add(cutOff);
                text = text.substring(charsPerLine).trim();
            }
        }

        int textHeight = (int)(fontMetrics.getHeight() * (lines.size() + .5d));

        BufferedImage image = gfxConfig.createCompatibleImage(textWidth, textHeight, Transparency.TRANSLUCENT);
        Graphics2D g2d = image.createGraphics();
        g2d.setFont(font);
        g2d.setColor(color);

        int yDist = fontMetrics.getHeight();

        DoublePoint pos = new DoublePoint((int)(fontMetrics.charWidth('W')/2d), yDist);

        while(!lines.isEmpty()){
            g2d.drawString(lines.poll(), (int)pos.getX(), (int)pos.getY());
            pos.setY(pos.getY() + yDist);
        }

        return image;
    }

//    public static BufferedImage toCompatibleImage(BufferedImage image)
//    {
//        // obtain the current system graphical settings
//        GraphicsConfiguration gfxConfig = GraphicsEnvironment.
//                getLocalGraphicsEnvironment().getDefaultScreenDevice().
//                getDefaultConfiguration();
//
//        /*
//         * if image is already compatible and optimized for current system
//         * settings, simply return it
//         */
//        if (image.getColorModel().equals(gfxConfig.getColorModel()))
//            return image;
//
//        // image is not optimized, so create a new image that is
//        BufferedImage newImage = gfxConfig.createCompatibleImage(
//                image.getWidth(), image.getHeight(), image.getTransparency());
//
//        // get the graphics context of the new image to draw the old image on
//        Graphics2D g2d = newImage.createGraphics();
//
//        // actually draw the image and dispose of context no longer needed
//        g2d.drawImage(image, 0, 0, null);
//        g2d.dispose();
//
//        // return the new optimized image
//        return newImage;
//
//        //taken from https://stackoverflow.com/questions/196890/java2d-performance-issues
//    }
}
