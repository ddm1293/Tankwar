package tankwar;

import java.awt.*;

public class FirstAid {

    private int x, y;
    private boolean alive;
    private final Image firstAidImage = Tools.getImage("blood.png");
    public static final int RECOVERY = 50;

    public FirstAid(int x, int y) {
        this.x = x;
        this.y = y;
        this.alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void draw(Graphics g) {
        g.drawImage(firstAidImage, x, y, null);
    }

    public Rectangle getRectangle() {
        return new Rectangle(this.x, this.y, firstAidImage.getWidth(null),
                firstAidImage.getHeight(null));
    }


}
