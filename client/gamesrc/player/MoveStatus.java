package gamesrc.player;

public enum MoveStatus {
    DONE {
        @Override
        boolean isDone() {
            return true;
        }
    };
    abstract boolean isDone();
}
