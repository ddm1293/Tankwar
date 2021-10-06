package tankwar;

import java.awt.*;

public enum Direction {
    // directionInt:
    UP("U", 0, -1, 4),  // 0100
    DOWN("D", 0, 1, 8),  // 1000
    LEFT("L", -1, 0, 1), // 0001
    RIGHT("R", 1, 0, 2), // 0010

    LEFT_UP("LU", -1, -1, 5), // 0101
    RIGHT_UP("RU", 1, -1, 6), // 0110
    LEFT_DOWN("LD", -1, 1, 9), // 1001
    RIGHT_DOWN("RD", 1, 1, 10); // 1010

    private final String abbrev;
    protected final int x, y, directionInt;

    Direction(String abbrev, int x, int y, int directionInt) {
        this.abbrev = abbrev;
        this.x = x;
        this.y = y;
        this.directionInt = directionInt;
    }

    static Direction parseDirectionInt(int directionInt) {
        for (Direction direction: Direction.values()) {
            if (direction.directionInt == directionInt) {
                return direction;
            }
        }
        return null;
    }

    protected Image getImage(String prefix) {
        return Tools.getImage(prefix + abbrev + ".gif");
    }
}
