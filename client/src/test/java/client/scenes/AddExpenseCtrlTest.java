package client.scenes;

import client.utils.ServerUtils;
import client.utils.UserConfig;
import jakarta.ws.rs.client.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddExpenseCtrlTest {
    @Mock
    private AddExpenseCtrl sut;
    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private UserConfig userConfig;
    @Mock
    private Client client;
    @InjectMocks
    private ServerUtils server;



    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        server.setServer("http://localhost:8080");
        sut = new AddExpenseCtrl(server, mainCtrl);

    }

    @Test
    void verifyPriceTest() {
        String testPrice1 = "0,005";
        String testPrice2 = "67.89";
        String testPrice3 = ".67";
        String testPrice4 = "67,935.99";
        assertTrue(sut.verifyPrice(testPrice1));
        assertTrue(sut.verifyPrice(testPrice2));

        AddExpenseCtrl addExpenseCtrl = spy(sut);

        doNothing().when(addExpenseCtrl).showAlert("Invalid price format", "Your price must start with a digit!");
        addExpenseCtrl.verifyPrice(testPrice3);
        verify(addExpenseCtrl, times(1)).showAlert("Invalid price format", "Your price must start with a digit!");

        doNothing().when(addExpenseCtrl).showAlert("Invalid price format", "Your price may not contain more than one period or comma!");
        addExpenseCtrl.verifyPrice(testPrice4);
        verify(addExpenseCtrl, times(1)).showAlert("Invalid price format", "Your price may not contain more than one period or comma!");

    }
}
