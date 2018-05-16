package de.promotos.mm.scene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainSceneController {

	@FXML
	private Button btnAction;
	
    @FXML protected void btnActionClick(ActionEvent event) {
        System.out.println("button pressed");
    }
    
    public void update(String s) {
    	btnAction.setText(s);
    }
}
