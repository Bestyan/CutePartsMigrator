package controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import log.Log;

public class MainController implements Initializable {

	@FXML
	protected Button btnParse, btnWrite, btnMigrate, btnSelectFolder;

	@FXML
	protected TextField tfClassToMigrate, tfWorkspace;

	@FXML
	protected Pane pnParseStatus;

	@FXML
	protected StackPane pnRoot;

	@FXML
	protected GridPane pnGrid;

	protected Stage stage;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Platform.runLater(() -> {
			pnParseStatus.setStyle("-fx-background-color: #990000");
		});
	}

	@FXML
	protected void parseClassStructure(){
		List<File> projects = this.loadAthosProjects();
	}

	@FXML
	protected void writeStructureToFile(){

	}

	@FXML
	protected void migrateClasses(){

	}

	@FXML
	protected void selectWorkspaceFolder(){
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedDirectory = directoryChooser.showDialog(this.getStage());

		if(selectedDirectory != null){
			String path = selectedDirectory.getAbsolutePath();
			path = path.endsWith("\\") ? path : path + "\\";
			tfWorkspace.setText(path);
		}
	}

	protected Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * lädt aus dem Workspace Ordner alle Ordner die mit com.athos. beginnen
	 * @return
	 */
	protected List<File> loadAthosProjects(){
		List<File> result = new ArrayList<File>();

		File workspace = new File(tfWorkspace.getText());
		if(!workspace.exists() || !workspace.isDirectory()){
			Log.log("Workspace path doesn't point to a folder", Log.Level.WARN);
			return result;
		}

		for(File project : workspace.listFiles()){
			if(project.getName().startsWith("com.athos.")){
				result.add(project);
			}
		}
		return result;
	}

}
