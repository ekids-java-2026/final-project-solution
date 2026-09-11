package ge.edu.delivery;

/** კონტრაქტი ობიექტისთვის, რომლის გზის თვალყურის დევნებაც შეიძლება. */
public interface Trackable {

    DeliveryStatus getStatus();

    String getTrackingMessage();

    boolean isFinished();
}
