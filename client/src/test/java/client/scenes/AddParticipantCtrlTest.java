package client.scenes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddParticipantCtrlTest {

    @Test
    void isValidEmail() {
        assertFalse(AddParticipantCtrl.isValidEmail("test"));
        assertFalse(AddParticipantCtrl.isValidEmail("test@"));
        assertFalse(AddParticipantCtrl.isValidEmail("@test"));

        assertTrue(AddParticipantCtrl.isValidEmail("test@test"));
        assertTrue(AddParticipantCtrl.isValidEmail("test@test.com"));
    }

    @Test
    void isValidIban() {
        assertFalse(AddParticipantCtrl.isValidIban("test"));
        assertFalse(AddParticipantCtrl.isValidIban("AB12 1234 1234 1234 123"));
        assertFalse(AddParticipantCtrl.isValidIban("AB12 1234 1234 1234 1234 123"));
        assertFalse(AddParticipantCtrl.isValidIban("1234 1234 1234 1234 1234"));

        assertTrue(AddParticipantCtrl.isValidIban("AB12 1234 1234 1234 1234"));
        assertTrue(AddParticipantCtrl.isValidIban("AB12 1234 1234 1234 1234 1"));
        assertTrue(AddParticipantCtrl.isValidIban("AB12 1234 1234 1234 1234 12"));
        assertTrue(AddParticipantCtrl.isValidIban("AB123456789012345678"));
        assertTrue(AddParticipantCtrl.isValidIban("AB1234567890123456789"));
        assertTrue(AddParticipantCtrl.isValidIban("AB12345678901234567890"));
    }

    @Test
    void isValidBic() {
        assertFalse(AddParticipantCtrl.isValidBic("test"));
        assertFalse(AddParticipantCtrl.isValidBic("ABCD"));
        assertFalse(AddParticipantCtrl.isValidBic("ABCD12"));
        assertFalse(AddParticipantCtrl.isValidBic("ABCD12ABCD"));

        assertTrue(AddParticipantCtrl.isValidBic("INGBNL2A"));
        assertTrue(AddParticipantCtrl.isValidBic("RABONL2U"));
        assertTrue(AddParticipantCtrl.isValidBic("0000AA00"));
        assertTrue(AddParticipantCtrl.isValidBic("0000AA00111"));

    }
}