package ge.edu.delivery;

/** შეკვეთის მიმღების მისამართზე მიტანა. */
public class HomeDelivery extends Delivery {

    private final String address;
    private final boolean courierAssigned;

    public HomeDelivery(String trackingCode, String recipientName, String address, boolean courierAssigned) {
        super(trackingCode, recipientName);
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("მისამართი ცარიელი არ უნდა იყოს");
        }
        this.address = address;
        this.courierAssigned = courierAssigned;
    }

    public String getAddress() {
        return address;
    }

    public boolean isCourierAssigned() {
        return courierAssigned;
    }

    @Override
    public DeliveryType getType() {
        return DeliveryType.HOME;
    }

    @Override
    protected boolean canPrepare() {
        return courierAssigned;
    }

    @Override
    protected String getDestinationLabel() {
        return "მისამართი: " + address;
    }
}
