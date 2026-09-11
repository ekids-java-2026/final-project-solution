package ge.edu.delivery;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@Tag("bonus")
@DisplayName("არჩევითი bonus დავალება")
class BonusExerciseTest {

    @Test
    @DisplayName("+10 ქულა — PickupDelivery ერთიან მოდელში მუშაობს")
    void PB10_BONUS_01_pickupDeliveryJoinsTheModel() throws Exception {
        Class<?> pickupClass = Class.forName("ge.edu.delivery.PickupDelivery");
        assertEquals(Delivery.class, pickupClass.getSuperclass());

        Constructor<?> constructor = pickupClass.getConstructor(
                String.class, String.class, String.class, boolean.class);
        Object object = constructor.newInstance("PICK-10", "ნატა", "STORE-5", true);
        assertInstanceOf(Delivery.class, object);

        Delivery delivery = (Delivery) object;
        assertEquals("PICKUP", delivery.getType().name());
        assertTrue(delivery.prepare());
        assertEquals("PICK-10 | PICKUP | READY | პიკაპის პუნქტი: STORE-5",
                delivery.getTrackingMessage());
    }
}
