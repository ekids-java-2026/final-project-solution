package ge.edu.delivery;

/**
 * სახლში და ლოკერში მიტანის საერთო მშობელი კლასი.
 * აქ ინახება პროცესის მდგომარეობა და მდგომარეობებს შორის გადასვლის წესები.
 */
public abstract class Delivery implements Trackable {

    private final String trackingCode;
    private final String recipientName;
    private DeliveryStatus status;

    protected Delivery(String trackingCode, String recipientName) {
        if (trackingCode == null || trackingCode.isBlank()) {
            throw new IllegalArgumentException("თვალთვალის კოდი ცარიელი არ უნდა იყოს");
        }
        if (recipientName == null || recipientName.isBlank()) {
            throw new IllegalArgumentException("მიმღების სახელი ცარიელი არ უნდა იყოს");
        }

        this.trackingCode = trackingCode;
        this.recipientName = recipientName;
        this.status = DeliveryStatus.CREATED;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public String getRecipientName() {
        return recipientName;
    }

    @Override
    public DeliveryStatus getStatus() {
        return status;
    }

    /** ამზადებს მიტანას, თუ მისი კონკრეტული ტიპის წინაპირობა შესრულებულია. */
    public boolean prepare() {
        if (status != DeliveryStatus.CREATED || !canPrepare()) {
            return false;
        }

        status = DeliveryStatus.READY;
        return true;
    }

    /** აგზავნის უკვე მომზადებულ მიტანას გზაში. */
    public boolean dispatch() {
        if (status != DeliveryStatus.READY) {
            return false;
        }

        status = DeliveryStatus.IN_TRANSIT;
        return true;
    }

    /** გზაში მყოფ მიტანას აღნიშნავს ჩაბარებულად. */
    public boolean markDelivered() {
        if (status != DeliveryStatus.IN_TRANSIT) {
            return false;
        }

        status = DeliveryStatus.DELIVERED;
        return true;
    }

    /** აუქმებს მიტანას მხოლოდ პროცესის დაწყებამდე. */
    public boolean cancel() {
        if (status != DeliveryStatus.CREATED && status != DeliveryStatus.READY) {
            return false;
        }

        status = DeliveryStatus.CANCELLED;
        return true;
    }

    @Override
    public boolean isFinished() {
        return status == DeliveryStatus.DELIVERED
                || status == DeliveryStatus.CANCELLED;
    }

    @Override
    public String getTrackingMessage() {
        return trackingCode
                + " | " + getType()
                + " | " + status
                + " | " + getDestinationLabel();
    }

    public abstract DeliveryType getType();

    protected abstract boolean canPrepare();

    protected abstract String getDestinationLabel();
}
