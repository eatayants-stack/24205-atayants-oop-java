package factory;

public record FactoryStatistics(
        int queueSize,
        int bodiesInStock, int bodiesProduced,
        int motorsInStock, int motorsProduced,
        int accessoriesInStock, int accessoriesProduced,
        int carsInStock, int carsProduced
) {}