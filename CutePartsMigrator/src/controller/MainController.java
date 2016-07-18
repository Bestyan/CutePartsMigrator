package controller;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import gui.Start;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableStringValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import log.Log;
import logic.Logic;
import util.Enums.Status;

public class MainController implements Initializable {
	public static final String DARKRED_STYLE = "-fx-background-color: #990000";
	public static final String DARKGREEN_STYLE = "-fx-background-color: #339933";
	

	@FXML
	protected Button btnParse, btnMigrate, btnSelectFolder;
	@FXML
	protected Button btnReadFiles, btnParseOldWorld, btnParseNewWorld;
	@FXML
	protected Button btnWriteOldWorld, btnWriteNewWorld, btnWriteRest;

	@FXML
	protected TextField tfClassToMigrate, tfWorkspace;

	@FXML
	protected Pane pnStatusReadFiles, pnParseOldWorldStatus, pnParseNewWorldStatus, pnParseStructureStatus, pnMigrationStatus;

	@FXML
	protected StackPane pnRoot;

	@FXML
	protected GridPane pnGrid;

	protected Stage stage;
	/**
	 * backend
	 */
	protected Logic logic;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.setLogic(new Logic());
		Platform.runLater(() -> {
			this.initBindings();
		});
	}
	
	/**
	 * initialisiert die PropertyBindings
	 * @param buttons
	 * @param panes
	 */
	public void initBindings(){
		final Map<Status, BooleanProperty> bindings = new TreeMap<>();
		final BooleanProperty filesRead = new SimpleBooleanProperty(false);
		final BooleanProperty oldWorldParsed = new SimpleBooleanProperty(false);
		final BooleanProperty newWorldParsed = new SimpleBooleanProperty(false);
		final BooleanProperty restParsed = new SimpleBooleanProperty(false);
		final BooleanProperty migrationComplete = new SimpleBooleanProperty(false);
		bindings.put(Status.FILES_READ, filesRead);
		bindings.put(Status.OLDWORLD_PARSED, oldWorldParsed);
		bindings.put(Status.NEWWORLD_PARSED, newWorldParsed);
		bindings.put(Status.STRUCTURE_PARSED, restParsed);
		bindings.put(Status.MIGRATION_COMPLETE, migrationComplete);
		this.getLogic().setStatusMap(bindings);
		
		//readFiles Button binden
		btnReadFiles.disableProperty().bind(Bindings.when(tfWorkspace.textProperty().isEmpty()).then(true).otherwise(false));
		
		//parse Button Properties binden
		btnParseOldWorld.disableProperty().bind(Bindings.not(filesRead));
		btnParseNewWorld.disableProperty().bind(Bindings.not(oldWorldParsed));
		btnParse.disableProperty().bind(Bindings.not(newWorldParsed));
		
		//write Button Properties binden
		btnWriteOldWorld.disableProperty().bind(Bindings.not(oldWorldParsed));
		btnWriteNewWorld.disableProperty().bind(Bindings.not(newWorldParsed));
		btnWriteRest.disableProperty().bind(Bindings.not(restParsed));
		
		//Migration class Textfield Property binden
		tfClassToMigrate.disableProperty().bind(Bindings.not(restParsed));
		
		//Anzeige Panes StyleProperties binden
		final ObservableStringValue readFilesStyle = Bindings.when(filesRead).then(MainController.DARKGREEN_STYLE).otherwise(MainController.DARKRED_STYLE);
		pnStatusReadFiles.styleProperty().bind(readFilesStyle);
		final ObservableStringValue oldWorldParsedStyle = Bindings.when(oldWorldParsed).then(MainController.DARKGREEN_STYLE).otherwise(MainController.DARKRED_STYLE);
		pnParseOldWorldStatus.styleProperty().bind(oldWorldParsedStyle);
		final ObservableStringValue newWorldParsedStyle = Bindings.when(newWorldParsed).then(MainController.DARKGREEN_STYLE).otherwise(MainController.DARKRED_STYLE);
		pnParseNewWorldStatus.styleProperty().bind(newWorldParsedStyle);
		final ObservableStringValue restParsedStyle = Bindings.when(restParsed).then(MainController.DARKGREEN_STYLE).otherwise(MainController.DARKRED_STYLE);
		pnParseStructureStatus.styleProperty().bind(restParsedStyle);
		final ObservableStringValue migrationCompleteStyle = Bindings.when(migrationComplete).then(MainController.DARKGREEN_STYLE).otherwise(MainController.DARKRED_STYLE);
		pnMigrationStatus.styleProperty().bind(migrationCompleteStyle);
		
		//Migrations Button Property binden
		final ObservableBooleanValue migrationActive = Bindings.createBooleanBinding(() -> {
			return restParsed.getValue() && !tfClassToMigrate.getText().trim().isEmpty();
			},
				tfClassToMigrate.textProperty(), restParsed);
		btnMigrate.disableProperty().bind(Bindings.not(migrationActive));
	}
	
	@FXML
	protected void readFiles(){
		this.getLogic().setWorkspace(tfWorkspace.getText());
		this.getLogic().readFiles();
	}

	@FXML
	protected void parseClassStructure(){
		this.getLogic().parseClassStructure();
	}

	@FXML
	protected void writeStructureToFile(){
		FileChooser dialog = new FileChooser();
		dialog.setTitle("Speichern");
		File file = dialog.showSaveDialog(this.getStage());
		this.getLogic().writeStructureToFile(file);
	}
	
	@FXML
	protected void writeOldWorldToFile(){
		FileChooser dialog = new FileChooser();
		dialog.setTitle("Speichern");
		File file = dialog.showSaveDialog(this.getStage());
		this.getLogic().writeOldWorldToFile(file);
	}
	
	@FXML
	protected void writeNewWorldToFile(){
		FileChooser dialog = new FileChooser();
		dialog.setTitle("Speichern");
		File file = dialog.showSaveDialog(this.getStage());
		this.getLogic().writeNewWorldToFile(file);
	}

	@FXML
	protected void migrateClasses(){
		try{
			this.getLogic().migrateClasses(tfClassToMigrate.getText());
		} catch(Exception e){
			Log.log(e);
			this.createPopup(e);
		}
	}
	
	@FXML
	protected void parseOldWorld(){
		this.getLogic().parseOldWorld();
	}
	
	@FXML
	protected void parseNewWorld(){
		this.getLogic().parseNewWorld();
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

	protected Logic getLogic() {
		return logic;
	}

	protected void setLogic(Logic logic) {
		this.logic = logic;
	}

	/**
	 * Erzeugt ein Popup mit der Message der exception
	 * @param exception
	 */
	protected void createPopup(Exception exception){
		try{
			final Stage stage = new Stage();

			//blockiert Eingaben ins andere Fenster
			stage.initModality(Modality.APPLICATION_MODAL);

			//fxml Struktur laden
			FXMLLoader loader = new FXMLLoader(Start.class.getResource("/view/Popup.fxml"));
			StackPane root = loader.load();

			//controller initialisieren
			PopupController controller = loader.getController();
			controller.setStage(stage);
			String errorText = exception.getMessage();
			controller.setErrorText(errorText.isEmpty() ? exception.toString() : errorText);

			//geladene fxml Struktur ins Fenster (stage) setzen
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("");
			stage.sizeToScene();
			stage.show();
		} catch(Exception e){
			Log.log(e, Log.Level.ERROR);
		}
	}
}
