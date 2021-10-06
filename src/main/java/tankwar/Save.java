package tankwar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Save {

    private boolean gameContinued;
    private Position playerTankPosition;
    private List<Position> enemyTankPosition;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Position {
        private int x, y;
        private Direction direction;
    }

}
