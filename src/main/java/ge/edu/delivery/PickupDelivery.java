package ge.edu.delivery;

/** შეკვეთის განსაზღვრული პიკაპის პუნქტიდან მიღება. */
public class PickupDelivery extends Delivery {

    private final String pickupPoint;
    private final boolean pickupCodeActive;

    public PickupDelivery(
            String trackingCode,
            String recipientName,
            String pickupPoint,
            boolean pickupCodeActive
    ) {
        super(trackingCode, recipientName);
        this.pickupPoint = pickupPoint;
        this.pickupCodeActive = pickupCodeActive;
    }

    @Override
    public DeliveryType getType() {
        return DeliveryType.PICKUP;
    }

    @Override
    protected boolean canPrepare() {
        return pickupCodeActive;
    }

    @Override
    protected String getDestinationLabel() {
        return "პიკაპის პუნქტი: " + pickupPoint;
    }
}
