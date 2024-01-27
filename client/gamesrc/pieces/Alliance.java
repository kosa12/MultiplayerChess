package gamesrc.pieces;

public enum Alliance {
    WHITE{
        @Override
        public int getDirection(){
            return -1;
        }
        @Override
        public boolean isBlack(){
            return false;
        }
        @Override
        public boolean isWhite(){
            return true;
        }
    }, 
    
    BLACK{
        @Override
        public int getDirection(){
            return 1;
        }
        @Override
        public boolean isBlack(){
            return true;
        }
        @Override
        public boolean isWhite(){
            return false;
        }
    };

    public abstract int getDirection();
    public abstract boolean isWhite();
    public abstract boolean isBlack();
}
