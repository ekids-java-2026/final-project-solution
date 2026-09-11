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
        // TODO 3: ახალ მიტანას მიანიჭე საწყისი სტატუსი.
        this.status = null;
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
        // TODO 4: ნებადართულია მხოლოდ CREATED მდგომარეობიდან და canPrepare() == true-ისას.
        return false;
    }

    /** აგზავნის უკვე მომზადებულ მიტანას გზაში. */
    public boolean dispatch() {
        // TODO 5: READY მდგომარეობა შეცვალე IN_TRANSIT-ით.
        return false;
    }

    /** გზაში მყოფ მიტანას აღნიშნავს ჩაბარებულად. */
    public boolean markDelivered() {
        // TODO 6: IN_TRANSIT მდგომარეობა შეცვალე DELIVERED-ით.
        return false;
    }

    /** აუქმებს მიტანას მხოლოდ პროცესის დაწყებამდე. */
    public boolean cancel() {
        // TODO 7: გაუქმება ნებადართულია მხოლოდ CREATED ან READY მდგომარეობაში.
        return false;
    }

    @Override
    public boolean isFinished() {
        // TODO 8: DELIVERED და CANCELLED საბოლოო მდგომარეობებია.
        return false;
    }

    @Override
    public String getTrackingMessage() {
        // TODO 9: ააწყვე README-ში მოთხოვნილი ერთიანი ტექსტი.
        return "";
    }

    public abstract DeliveryType getType();

    protected abstract boolean canPrepare();

    protected abstract String getDestinationLabel();
}
