package common;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class MessageBox {
	
	public static ButtonType show(String title, String content) {
		return show(title, content, AlertType.INFORMATION, ButtonType.OK);
	}
	
	public static ButtonType show(String title, String content, AlertType type, ButtonType ...buttons) {
		
		Alert msg = new Alert(type, content, buttons );
		msg.setAlertType(type);
		msg.setHeaderText("");
		msg.setTitle(title);
		msg.setContentText(content);
		return msg.showAndWait().get();

	}

}
