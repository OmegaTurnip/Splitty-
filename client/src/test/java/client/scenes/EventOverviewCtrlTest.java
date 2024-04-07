package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.language.Language;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.Event;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.stomp.StompSession;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;

import static org.mockito.Mockito.*;

public class EventOverviewCtrlTest extends ApplicationTest {
    @InjectMocks
    private EventOverviewCtrl sut;
    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private ServerUtils server;
    Event event;

    @Override
    public void start(Stage stage) throws Exception {
        try (MockedStatic<UserConfig> userConfigMockedStatic = Mockito.mockStatic(UserConfig.class)) {
            try (MockedConstruction<ServerUtils> mockPaymentService = Mockito.mockConstruction(ServerUtils.class,(mock, context)-> {
                when(mock.connect(Mockito.anyString())).thenReturn(Mockito.mock(StompSession.class));
            })) {
                UserConfig userConfig = Mockito.mock(UserConfig.class);
                userConfigMockedStatic.when(UserConfig::get).thenReturn(userConfig);
                Mockito.when(userConfig.getUserLanguage()).thenReturn("eng");
                Language.fromLanguageFile(
                        "eng", new File("../includedLanguages/eng.properties")
                );
                Translator.setCurrentLanguage(Language.languages.get("eng"));
                Injector injector = Guice.createInjector(new MyModule());
                MyFXML FXML = new MyFXML(injector);

                Pair<EventOverviewCtrl, Parent> eventOverview = FXML.load(EventOverviewCtrl.class,
                        "client", "scenes", "EventOverview.fxml");

                this.sut = eventOverview.getKey();
                MockitoAnnotations.openMocks(this).close();
                Mockito.when(server.connect(Mockito.anyString())).thenReturn(Mockito.mock(StompSession.class));

                event = new Event("testEvent");
                Scene scene = new Scene(eventOverview.getValue());
                stage.setScene(scene);
                stage.show();
            }
        }
    }

    @BeforeAll
    public static void setupSpec() throws Exception {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "false");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "false");
    }
    @AfterEach
    void breakDown() {
        Mockito.reset(server );
        Mockito.reset(mainCtrl);
    }

    @Test
    void returnToOverviewTest() {
        MainCtrl mainCtrlMock = Mockito.mock(MainCtrl.class);
        sut.setMainCtrl(mainCtrlMock);
        doNothing().when(mainCtrlMock).showStartUp();
        clickOn("#rtoButton");
        clickOn("#returnToOverview");
        verify(mainCtrlMock, times(1)).showStartUp();
    }
}
