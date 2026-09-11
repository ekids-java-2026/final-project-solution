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
        // TODO 13: დააბრუნე ლოკერში მიტანის ტიპი.
        return null;
    }

    @Override
    protected boolean canPrepare() {
        // TODO 14: ლოკერში მიტანისთვის უჯრა მზად უნდა იყოს.
        return false;
    }

    @Override
    protected String getDestinationLabel() {
        // მინიშნება: ზუსტი ფორმატი ნახე README-ში და დაასრულე ეს უკანასკნელი override.
        return "";
    }
}
