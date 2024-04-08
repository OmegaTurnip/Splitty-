package client.scenes;


import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertWrapper {
    /**
     * Empty constructor
     */
    public AlertWrapper(){

    }

    /**
     * Shows an alert
     * @param type alert type
     * @param title title of alert
     * @param content content of alert
     */
    public void showAlert(Alert.AlertType type, String title, String content){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Shows an alert
     * @param type alert type
     * @param title title of alert
     * @param content content of alert
     * @return result of button
     */
    public ButtonType showAlertButton(Alert.AlertType type
            , String title, String content) throws RuntimeException{
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        var result = alert.showAndWait();
        if (result.isEmpty()){
            throw new RuntimeException();
        }
        return result.get();
    }
}
