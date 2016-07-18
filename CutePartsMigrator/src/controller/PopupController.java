package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PopupController implements Initializable{

	@FXML
	private Label lblError;

	@FXML
	private Button btnOk;

	private Stage stage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//nothing yet
	}

	@FXML
	protected void closePopup(){
		this.getStage().close();
		//void reference so garbage collection can kick in
		this.setStage(null);
	}

	protected Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setErrorText(String text){
		lblError.setText(text);
	}

}
