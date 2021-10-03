package tankwar;

import javax.swing.*;
import java.awt.*;

public class Wall {
    private int x, y;
    private boolean horizontal;
    private int bricks;

    private final Image brickImage;
    private final int brickWidth;
    private final int brickHeight;

    public Wall(int x, int y, boolean horizontal, int bricks) {
        this.x = x;
        this.y = y;
        this.horizontal = horizontal;
        this.bricks = bricks;
        this.brickImage = new ImageIcon("assets/images/brick.png").getImage();
        this.brickWidth = brickImage.getWidth(null);
        this.brickHeight = brickImage.getHeight(null);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public int getBricks() {
        return bricks;
    }

    /*public boolean hitWall(int x, int y) {
        if (this.horizontal) {
            if ((x >= this.x && x <= this.x + this.bricks * brickWidth)
                    && (y >= this.y && y <= this.y + brickHeight)) {
                return true;
            }
        } else {
            if ((x >= this.x && x <= this.x + brickWidth)
                    && (y >= this.y && y <= this.y + this.bricks * brickHeight)) {
                return true;
            }
        }
        return false;
    }*/

    public Rectangle getRectangle() {
        return horizontal
                ? new Rectangle(x, y, brickWidth * bricks, brickHeight)
                : new Rectangle(x, y, brickWidth, brickHeight * bricks);
    }

    public void draw(Graphics g) {

        if (this.horizontal) {
            for (int i = 0; i < bricks; i++) {
                g.drawImage(brickImage, x + i * brickWidth, y, null);
            }
        } else {
            for (int i = 0; i < bricks; i++) {
                g.drawImage(brickImage, x, y + i * brickHeight, null);
            }
        }
    }
}
