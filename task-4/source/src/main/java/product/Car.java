package product;

public class Car extends Product {
    private final Body body;
    private final Motor motor;
    private final Accessory accessory;

    public Car (long id, Body body, Motor motor, Accessory accessory){
        super(id);
        this.body = body;
        this.motor = motor;
        this.accessory = accessory;
    }
    public long getBodyId(){return body.getId();}
    public long getMotorId(){return motor.getId();}
    public long getAccessoryId(){return accessory.getId();}
}
