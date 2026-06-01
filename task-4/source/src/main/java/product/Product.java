package product;

public abstract class Product {
    private final long id;
    public Product(long id){
        this.id = id;
    }
    public long getId(){
        return id;
    }
}
