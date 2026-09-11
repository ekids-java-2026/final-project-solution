package ge.edu.delivery;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ჭკვიანი მიტანის ძირითადი დავალება")
class DeliveryExerciseTest {

    @Test
    @DisplayName("10 ქულა — enum-ებში ყველა აუცილებელი მნიშვნელობაა")
    void P10_TYPES_01_enumsContainRequiredValues() {
        assertEquals(5, DeliveryStatus.values().length, "DeliveryStatus-ში ზუსტად ხუთი მდგომარეობა უნდა იყოს");
        assertAll(
                () -> assertNotNull(status("CREATED")),
                () -> assertNotNull(status("READY")),
                () -> assertNotNull(status("IN_TRANSIT")),
                () -> assertNotNull(status("DELIVERED")),
                () -> assertNotNull(status("CANCELLED")),
                () -> assertNotNull(type("HOME")),
                () -> assertNotNull(type("LOCKER"))
        );
    }

    @Test
    @DisplayName("10 ქულა — Trackable სწორ კონტრაქტს აღწერს")
    void P10_TYPES_02_trackableDefinesContract() throws NoSuchMethodException {
        assertTrue(Trackable.class.isInterface(), "Trackable ინტერფეისი უნდა იყოს");
        assertDeclaredMethod(Trackable.class, "getStatus", DeliveryStatus.class);
        assertDeclaredMethod(Trackable.class, "getTrackingMessage", String.class);
        assertDeclaredMethod(Trackable.class, "isFinished", boolean.class);
    }

    @Test
    @DisplayName("10 ქულა — Delivery აბსტრაქტულია და ველები ინკაფსულირებულია")
    void P10_MODEL_01_deliveryIsAbstractAndEncapsulated() throws NoSuchMethodException {
        assertTrue(Modifier.isAbstract(Delivery.class.getModifiers()), "Delivery abstract კლასი უნდა იყოს");
        assertTrue(Trackable.class.isAssignableFrom(Delivery.class), "Delivery-მ Trackable უნდა განახორციელოს");
        assertAllFieldsPrivate(Delivery.class);
        assertAllFieldsPrivate(HomeDelivery.class);
        assertAllFieldsPrivate(LockerDelivery.class);

        assertProtectedAbstract("canPrepare", boolean.class);
        assertProtectedAbstract("getDestinationLabel", String.class);
        Method getType = Delivery.class.getDeclaredMethod("getType");
        assertTrue(Modifier.isPublic(getType.getModifiers()) && Modifier.isAbstract(getType.getModifiers()));
        assertEquals(DeliveryType.class, getType.getReturnType());
    }

    @Test
    @DisplayName("10 ქულა — კონსტრუქტორები ამოწმებს მონაცემებს და საწყისი სტატუსია CREATED")
    void P10_MODEL_02_constructorValidationAndInitialState() {
        HomeDelivery delivery = new HomeDelivery("PKG-101", "ნინო", "რუსთაველის 10", true);

        assertAll(
                () -> assertEquals("PKG-101", delivery.getTrackingCode()),
                () -> assertEquals("ნინო", delivery.getRecipientName()),
                () -> assertEquals("რუსთაველის 10", delivery.getAddress()),
                () -> assertTrue(delivery.isCourierAssigned()),
                () -> assertEquals(status("CREATED"), delivery.getStatus())
        );

        assertThrows(IllegalArgumentException.class,
                () -> new HomeDelivery(" ", "ნინო", "მისამართი", true));
        assertThrows(IllegalArgumentException.class,
                () -> new HomeDelivery("PKG", null, "მისამართი", true));
        assertThrows(IllegalArgumentException.class,
                () -> new HomeDelivery("PKG", "ნინო", "", true));
        assertThrows(IllegalArgumentException.class,
                () -> new LockerDelivery("PKG", "ნინო", " ", true));
    }

    @Test
    @DisplayName("10 ქულა — prepare ითვალისწინებს წინაპირობასა და მიმდინარე სტატუსს")
    void P10_STATE_01_prepareHonorsPreconditionAndState() {
        Delivery unavailable = new HomeDelivery("PKG-102", "გიორგი", "თავისუფლების 2", false);
        assertFalse(unavailable.prepare());
        assertEquals(status("CREATED"), unavailable.getStatus());

        Delivery ready = new HomeDelivery("PKG-103", "ანა", "ვაჟა-ფშაველას 8", true);
        assertTrue(ready.prepare());
        assertEquals(status("READY"), ready.getStatus());
        assertFalse(ready.prepare(), "READY მდგომარეობიდან ხელახლა მომზადება აკრძალულია");
        assertEquals(status("READY"), ready.getStatus());
    }

    @Test
    @DisplayName("10 ქულა — გაგზავნა და ჩაბარება მხოლოდ სწორი თანმიმდევრობით მუშაობს")
    void P10_STATE_02_dispatchAndDeliverFollowOrder() {
        Delivery delivery = new LockerDelivery("PKG-104", "საბა", "TB-07", true);

        assertFalse(delivery.dispatch());
        assertEquals(status("CREATED"), delivery.getStatus());
        assertTrue(delivery.prepare());
        assertTrue(delivery.dispatch());
        assertEquals(status("IN_TRANSIT"), delivery.getStatus());
        assertFalse(delivery.dispatch());
        assertTrue(delivery.markDelivered());
        assertEquals(status("DELIVERED"), delivery.getStatus());
        assertFalse(delivery.markDelivered());
        assertFalse(delivery.cancel());
        assertEquals(status("DELIVERED"), delivery.getStatus());
        assertTrue(delivery.isFinished());
    }

    @Test
    @DisplayName("10 ქულა — გაუქმება შესაძლებელია მხოლოდ გზაში გასვლამდე")
    void P10_STATE_03_cancelOnlyBeforeTransit() {
        Delivery created = new HomeDelivery("PKG-105", "მარი", "ჭავჭავაძის 4", true);
        assertTrue(created.cancel());
        assertEquals(status("CANCELLED"), created.getStatus());
        assertTrue(created.isFinished());
        assertAll(
                () -> assertFalse(created.prepare()),
                () -> assertFalse(created.dispatch()),
                () -> assertFalse(created.markDelivered())
        );

        Delivery ready = new LockerDelivery("PKG-106", "დათა", "BT-03", true);
        assertTrue(ready.prepare());
        assertTrue(ready.cancel());
        assertEquals(status("CANCELLED"), ready.getStatus());

        Delivery inTransit = new LockerDelivery("PKG-107", "ელენე", "KT-12", true);
        assertTrue(inTransit.prepare());
        assertTrue(inTransit.dispatch());
        assertFalse(inTransit.cancel());
        assertEquals(status("IN_TRANSIT"), inTransit.getStatus());
        assertFalse(inTransit.isFinished());
    }

    @Test
    @DisplayName("10 ქულა — HomeDelivery სწორად override-ავს ტიპსა და მზადყოფნას")
    void P10_POLYMORPHISM_01_homeDeliveryOverridesRules() {
        assertEquals(Delivery.class, HomeDelivery.class.getSuperclass());

        Delivery unavailable = new HomeDelivery("HOME-1", "ლუკა", "აღმაშენებლის 5", false);
        Delivery available = new HomeDelivery("HOME-2", "თაკო", "აღმაშენებლის 6", true);

        assertEquals(type("HOME"), unavailable.getType());
        assertFalse(unavailable.prepare());
        assertTrue(available.prepare());
        assertEquals("HOME-2 | HOME | READY | მისამართი: აღმაშენებლის 6",
                available.getTrackingMessage());
    }

    @Test
    @DisplayName("10 ქულა — LockerDelivery სწორად override-ავს ტიპსა და მზადყოფნას")
    void P10_POLYMORPHISM_02_lockerDeliveryOverridesRules() {
        assertEquals(Delivery.class, LockerDelivery.class.getSuperclass());

        LockerDelivery unavailable = new LockerDelivery("LOCK-1", "ირაკლი", "TB-01", false);
        LockerDelivery available = new LockerDelivery("LOCK-2", "ქეთი", "TB-02", true);

        assertAll(
                () -> assertEquals("TB-01", unavailable.getLockerId()),
                () -> assertFalse(unavailable.isCompartmentReady()),
                () -> assertEquals(type("LOCKER"), unavailable.getType()),
                () -> assertFalse(unavailable.prepare()),
                () -> assertTrue(available.prepare()),
                () -> assertEquals("LOCK-2 | LOCKER | READY | ლოკერი: TB-02",
                        available.getTrackingMessage())
        );
    }

    @Test
    @DisplayName("10 ქულა — Trackable reference იყენებს კონკრეტული კლასის ქცევას")
    void P10_TRACKING_01_trackableUsesPolymorphicData() {
        Trackable trackable = new LockerDelivery("PKG-202", "ლიზი", "TB-07", true);
        Delivery delivery = (Delivery) trackable;

        assertFalse(trackable.isFinished());
        assertTrue(delivery.prepare());
        assertTrue(delivery.dispatch());
        assertEquals("PKG-202 | LOCKER | IN_TRANSIT | ლოკერი: TB-07",
                trackable.getTrackingMessage());
        assertTrue(delivery.markDelivered());
        assertTrue(trackable.isFinished());
        assertEquals(status("DELIVERED"), trackable.getStatus());
    }

    private static DeliveryStatus status(String name) {
        return DeliveryStatus.valueOf(name);
    }

    private static DeliveryType type(String name) {
        return DeliveryType.valueOf(name);
    }

    private static void assertDeclaredMethod(Class<?> owner, String name, Class<?> returnType)
            throws NoSuchMethodException {
        Method method = owner.getDeclaredMethod(name);
        assertEquals(returnType, method.getReturnType());
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertTrue(Modifier.isAbstract(method.getModifiers()));
    }

    private static void assertProtectedAbstract(String name, Class<?> returnType)
            throws NoSuchMethodException {
        Method method = Delivery.class.getDeclaredMethod(name);
        assertEquals(returnType, method.getReturnType());
        assertTrue(Modifier.isProtected(method.getModifiers()));
        assertTrue(Modifier.isAbstract(method.getModifiers()));
    }

    private static void assertAllFieldsPrivate(Class<?> owner) {
        for (Field field : owner.getDeclaredFields()) {
            assertTrue(Modifier.isPrivate(field.getModifiers()),
                    owner.getSimpleName() + "-ის ყველა ველი private უნდა იყოს");
        }
    }
}
