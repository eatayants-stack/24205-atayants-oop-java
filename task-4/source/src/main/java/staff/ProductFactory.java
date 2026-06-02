package staff;

@FunctionalInterface
public interface ProductFactory<T> {
    T create();
}