package detail;

public abstract class Detail {
    private final long id;
    public Detail(long id){
        this.id = id;
    }
    public long getId(){
        return id;
    }
}
