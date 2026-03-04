package su.sergiusonesimus.recreate.content.contraptions.relays.belt;

public enum BeltSlope {

    HORIZONTAL,
    UPWARD,
    DOWNWARD,
    VERTICAL,
    SIDEWAYS;

    public boolean isDiagonal() {
        return this == UPWARD || this == DOWNWARD;
    }
}
