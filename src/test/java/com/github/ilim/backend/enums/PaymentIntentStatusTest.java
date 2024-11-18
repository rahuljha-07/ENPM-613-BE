package com.github.ilim.backend.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentIntentStatusTest {

    @Test
    void testEnumConstants() {
        assertNotNull(PaymentIntentStatus.SUCCEEDED, "Enum constant SUCCEEDED should exist");
        assertNotNull(PaymentIntentStatus.REQUIRES_ACTION, "Enum constant REQUIRES_ACTION should exist");
        assertNotNull(PaymentIntentStatus.REQUIRES_PAYMENT_METHOD, "Enum constant REQUIRES_PAYMENT_METHOD should exist");
        assertNotNull(PaymentIntentStatus.PROCESSING, "Enum constant PROCESSING should exist");
        assertNotNull(PaymentIntentStatus.CANCELED, "Enum constant CANCELED should exist");
        assertNotNull(PaymentIntentStatus.REQUIRES_CONFIRMATION, "Enum constant REQUIRES_CONFIRMATION should exist");
        assertNotNull(PaymentIntentStatus.REQUIRES_CAPTURE, "Enum constant REQUIRES_CAPTURE should exist");
        assertNotNull(PaymentIntentStatus.NULL, "Enum constant NULL should exist");
    }

    @Test
    void testFromMethod() {
        assertEquals(PaymentIntentStatus.SUCCEEDED, PaymentIntentStatus.from("succeeded"), "from(\"succeeded\") should return SUCCEEDED");
        assertEquals(PaymentIntentStatus.REQUIRES_ACTION, PaymentIntentStatus.from("requires_action"), "from(\"requires_action\") should return REQUIRES_ACTION");
        assertEquals(PaymentIntentStatus.REQUIRES_PAYMENT_METHOD, PaymentIntentStatus.from("requires_payment_method"), "from(\"requires_payment_method\") should return REQUIRES_PAYMENT_METHOD");
        assertEquals(PaymentIntentStatus.PROCESSING, PaymentIntentStatus.from("processing"), "from(\"processing\") should return PROCESSING");
        assertEquals(PaymentIntentStatus.CANCELED, PaymentIntentStatus.from("canceled"), "from(\"canceled\") should return CANCELED");
        assertEquals(PaymentIntentStatus.REQUIRES_CONFIRMATION, PaymentIntentStatus.from("requires_confirmation"), "from(\"requires_confirmation\") should return REQUIRES_CONFIRMATION");
        assertEquals(PaymentIntentStatus.REQUIRES_CAPTURE, PaymentIntentStatus.from("requires_capture"), "from(\"requires_capture\") should return REQUIRES_CAPTURE");
        assertEquals(PaymentIntentStatus.NULL, PaymentIntentStatus.from("unknown_status"), "from(\"unknown_status\") should return NULL");
    }

    @Test
    void testToStringMethod() {
        assertEquals("succeeded", PaymentIntentStatus.toString(PaymentIntentStatus.SUCCEEDED), "toString(SUCCEEDED) should return \"succeeded\"");
        assertEquals("requires_action", PaymentIntentStatus.toString(PaymentIntentStatus.REQUIRES_ACTION), "toString(REQUIRES_ACTION) should return \"requires_action\"");
        assertEquals("requires_payment_method", PaymentIntentStatus.toString(PaymentIntentStatus.REQUIRES_PAYMENT_METHOD), "toString(REQUIRES_PAYMENT_METHOD) should return \"requires_payment_method\"");
        assertEquals("processing", PaymentIntentStatus.toString(PaymentIntentStatus.PROCESSING), "toString(PROCESSING) should return \"processing\"");
        assertEquals("canceled", PaymentIntentStatus.toString(PaymentIntentStatus.CANCELED), "toString(CANCELED) should return \"canceled\"");
        assertEquals("requires_confirmation", PaymentIntentStatus.toString(PaymentIntentStatus.REQUIRES_CONFIRMATION), "toString(REQUIRES_CONFIRMATION) should return \"requires_confirmation\"");
        assertEquals("requires_capture", PaymentIntentStatus.toString(PaymentIntentStatus.REQUIRES_CAPTURE), "toString(REQUIRES_CAPTURE) should return \"requires_capture\"");
        assertNull(PaymentIntentStatus.toString(PaymentIntentStatus.NULL), "toString(NULL) should return null");
    }

    @Test
    void testOverriddenToStringMethod() {
        assertEquals("succeeded", PaymentIntentStatus.SUCCEEDED.toString(), "SUCCEEDED.toString() should return \"succeeded\"");
        assertEquals("requires_action", PaymentIntentStatus.REQUIRES_ACTION.toString(), "REQUIRES_ACTION.toString() should return \"requires_action\"");
        assertEquals("requires_payment_method", PaymentIntentStatus.REQUIRES_PAYMENT_METHOD.toString(), "REQUIRES_PAYMENT_METHOD.toString() should return \"requires_payment_method\"");
        assertEquals("processing", PaymentIntentStatus.PROCESSING.toString(), "PROCESSING.toString() should return \"processing\"");
        assertEquals("canceled", PaymentIntentStatus.CANCELED.toString(), "CANCELED.toString() should return \"canceled\"");
        assertEquals("requires_confirmation", PaymentIntentStatus.REQUIRES_CONFIRMATION.toString(), "REQUIRES_CONFIRMATION.toString() should return \"requires_confirmation\"");
        assertEquals("requires_capture", PaymentIntentStatus.REQUIRES_CAPTURE.toString(), "REQUIRES_CAPTURE.toString() should return \"requires_capture\"");
        assertNull(PaymentIntentStatus.NULL.toString(), "NULL.toString() should return null");
    }
}
