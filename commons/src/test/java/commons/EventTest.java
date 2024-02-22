package commons;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void generateInviteCodeTest() {
        String test1 = Event.generateInviteCode();
        String test2 = Event.generateInviteCode();
        assertFalse(Objects.equals(test1, test2));
    }
}