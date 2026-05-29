package detail;

public class Car extends Detail{
    private final Body body;
    private final Motor motor;
    private final Accessory accessory;

    public Car (long id, Body body, Motor motor, Accessory accessory){
        super(id);
        this.body = body;
        this.motor = motor;
        this.accessory = accessory;
    }
    public long getBody(){return body.getId();}
    public long getMotor(){return motor.getId();}
    public long getAccessory(){return accessory.getId();}
}
