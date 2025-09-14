package su.sergiusonesimus.recreate.foundation.utility;

import su.sergiusonesimus.metaworlds.util.Direction;
import su.sergiusonesimus.metaworlds.util.Direction.Axis;

/**
 * A bunch of methods that got stripped out of Direction in 1.15
 * 
 * @author Mojang
 */
public class DirectionHelper {

    public static Direction rotateAround(Direction dir, Direction.Axis axis) {
        switch (axis) {
            case X:
                if (dir != Direction.WEST && dir != Direction.EAST) {
                    return rotateX(dir);
                }

                return dir;
            case Y:
                if (dir != Direction.UP && dir != Direction.DOWN) {
                    return dir.getClockWise();
                }

                return dir;
            case Z:
                if (dir != Direction.NORTH && dir != Direction.SOUTH) {
                    return rotateZ(dir);
                }

                return dir;
            default:
                throw new IllegalStateException("Unable to get CW facing for axis " + axis);
        }
    }

    public static Direction rotateX(Direction dir) {
        switch (dir) {
            case NORTH:
                return Direction.DOWN;
            case EAST:
            case WEST:
            default:
                throw new IllegalStateException("Unable to get X-rotated facing of " + dir);
            case SOUTH:
                return Direction.UP;
            case UP:
                return Direction.NORTH;
            case DOWN:
                return Direction.SOUTH;
        }
    }

    public static Direction rotateZ(Direction dir) {
        switch (dir) {
            case EAST:
                return Direction.DOWN;
            case SOUTH:
            default:
                throw new IllegalStateException("Unable to get Z-rotated facing of " + dir);
            case WEST:
                return Direction.UP;
            case UP:
                return Direction.EAST;
            case DOWN:
                return Direction.WEST;
        }
    }

    public static Direction getPositivePerpendicular(Axis horizontalAxis) {
        return horizontalAxis == Axis.X ? Direction.SOUTH : Direction.EAST;
    }

}
