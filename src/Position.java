public class Position{

    private int x;
    private int y;
    public int getX() { return x; }
    public int getY() { return y; }

    public Position(int x,int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof  Position){
            if(((Position)obj).getX() != x){
                return false;
            }
            if(((Position)obj).getY() != y){
                return false;
            }
            return true;
        }
       return false;
    }

    @Override
    public int hashCode(){
        return x*1000 + y;
    }

}
