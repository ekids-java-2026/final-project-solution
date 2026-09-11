package ge.edu.delivery;

/** შეკვეთის თვითმომსახურების ლოკერში მიტანა. */
public class LockerDelivery extends Delivery {

    private final String lockerId;
    private final boolean compartmentReady;

    public LockerDelivery(String trackingCode, String recipientName, String lockerId, boolean compartmentReady) {
        super(trackingCode, recipientName);
        if (lockerId == null || lockerId.isBlank()) {
            throw new IllegalArgumentException("ლოკერის კოდი ცარიელი არ უნდა იყოს");
        }
        this.lockerId = lockerId;
        this.compartmentReady = compartmentReady;
    }

    public String getLockerId() {
        return lockerId;
    }

    public boolean isCompartmentReady() {
        return compartmentReady;
    }

    @Override
    public DeliveryType getType() {
        return DeliveryType.LOCKER;
    }

    @Override
    protected boolean canPrepare() {
        return compartmentReady;
    }

    @Override
    protected String getDestinationLabel() {
        return "ლოკერი: " + lockerId;
    }
}
