package window;

import util.observer.AbstractObserver;
import util.observer.AbstractPushObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
public class BufferedImagePanel extends JPanel implements AbstractGraphicalDisplay {
    private BufferedImage image;
    private AbstractPushObserver<BufferedImage> imageReceiver;
    private AbstractObserver imageUpdateReceiver;

    public BufferedImagePanel(int width, int height){
        setBackground(Color.BLACK);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        makeGraphicalObservers();
    }

    private void makeGraphicalObservers(){
        imageReceiver = makeImageReceiver();
        imageUpdateReceiver = makeImageUpdateReceiver();
    }

    private AbstractPushObserver<BufferedImage> makeImageReceiver() {
        return new AbstractPushObserver<BufferedImage>() {
            @Override
            public void update(BufferedImage data) {
                setImage(data);
            }
        };
    }

    private void setImage(BufferedImage image){
        //this.image = ImageUtil.scaleImage(image, getWidth(), getHeight());
//        this.image = ImageUtil.toCompatibleImage(image);
        this.image = image;
    }

    private AbstractObserver makeImageUpdateReceiver(){
        return new AbstractObserver(){
            @Override
            public void update(){
                repaint();
            }
        };
    }

    @Override
    public AbstractPushObserver<BufferedImage> getImageReceiver() {
        return imageReceiver;
    }

    @Override
    public AbstractObserver getImageUpdateReceiver() {
        return imageUpdateReceiver;
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }
}
