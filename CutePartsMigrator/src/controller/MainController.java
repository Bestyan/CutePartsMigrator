package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class MainController implements Initializable {

	@FXML
	protected Button btnParse, btnWrite, btnMigrate;

	@FXML
	protected TextField tfClassToMigrate;

	@FXML
	protected Pane pnParseStatus;

	@FXML
	protected StackPane pnRoot;

	@FXML
	protected GridPane pnGrid;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

}
