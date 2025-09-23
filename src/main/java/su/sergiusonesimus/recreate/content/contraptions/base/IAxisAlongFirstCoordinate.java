package su.sergiusonesimus.recreate.content.contraptions.base;

import su.sergiusonesimus.metaworlds.util.Direction;

public interface IAxisAlongFirstCoordinate {

    default boolean isAxisAlongFirstCoordinate(int meta) {
        return meta % 12 < 6;
    }

    default int getMetadata(Direction direction, boolean axisAlongFirstCoordinate) {
        return direction.get3DDataValue() + (axisAlongFirstCoordinate ? 0 : 6);
    }

    /**
     * Switches block metadata from one corresponding to axis along first coordinate to one corresponding to axis along
     * second coordinate and vice versa
     * 
     * @param meta - original block metadata
     * @return block metadata after switching to the second state
     */
    default int cycleMetadata(int meta) {
        return (meta + 6) % 12;
    }

}
