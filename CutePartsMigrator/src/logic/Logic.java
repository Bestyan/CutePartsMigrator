package logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.Constructor;
import data.JavaClass;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import log.Log;
import util.Enums;
import util.Enums.Status;
import util.SpecificUtil;
import util.Util;

public class Logic {

	/**
	 * mappt {@literal <package>.<classname>} auf das zugehörige JavaClass Objekt
	 */
	private Map<String, JavaClass> masterMap = new TreeMap<>();
	/**
	 * mappt {@literal <package>.<classname>} auf das zugehörige JavaClass Objekt
	 */
	private Map<String, JavaClass> oldWorld = new TreeMap<>();
	/**
	 * mappt {@literal <package>.<classname>} auf das zugehörige JavaClass Objekt
	 */
	private Map<String, JavaClass> newWorld = new TreeMap<>();
	/**
	 * workspace-Ordner
	 */
	private File workspace;
	/**
	 * Map aller weder zur neuen noch zur alten Welt gehörenden geladenen Dateien
	 */
	private Map<File, String> loadedFiles = new TreeMap<>();
	/**
	 * geladene Dateien der alten Welt
	 */
	private Map<File, String> oldWorldFiles = new TreeMap<>();
	/**
	 * geladene Dateien der neuen Welt
	 */
	private Map<File, String> newWorldFiles	= new TreeMap<>();
	/**
	 * verwaltet die Status der GUI und den Fortschritt des gesamten Vorgangs
	 */
	private Map<Enums.Status, BooleanProperty> statusMap = new TreeMap<>();
	/**
	 * neue Id auf neuen Konstruktor gemappt
	 */
	private Map<Integer, Constructor> idToNewCon = new TreeMap<>();
	/**
	 * alte Id auf neue Id gemappt
	 */
	private Map<Integer, Integer> oldIdToNewId = new TreeMap<>();
	/**
	 * alter Konstruktor auf alte Id gemappt
	 */
	private Map<Constructor, Integer> oldConToId = new TreeMap<>();
	/**
	 * gefundene Klassen
	 */
	private ObservableList<JavaClass> searchResults = FXCollections.observableArrayList(new ArrayList<JavaClass>());
	/**
	 * Inhalt der ListView
	 */
	private ObservableList<String> stringSearchResults = FXCollections.observableArrayList(new ArrayList<String>());

	/**
	 * Logik der Operationen
	 * @param workspace
	 */
	public Logic(){
		super();
	}
	
	public boolean readFiles(){
		this.setStatus(Status.FILES_READ, false);
		boolean success = true;
		long timeStart = System.currentTimeMillis();
		List<File> allFiles = new ArrayList<>();
		try{
			Log.log("Loading Projects..", Log.Level.INFO);
			List<File> projects = this.loadAthosProjects();
			Log.log("Loading all sub files", Log.Level.INFO);
			for(File project : projects){
				List<File> projectFiles = Util.readAllSubfiles(project, ".java");
				allFiles.addAll(projectFiles);
			}
			for(File file : allFiles){
				if(SpecificUtil.isNewWorld(file)){
					this.getNewWorldFiles().put(file, Util.readFile(file));
				} else if(SpecificUtil.isOldWorld(file)){
					this.getOldWorldFiles().put(file, Util.readFile(file));
				} else{
					this.getLoadedFiles().put(file, Util.readFile(file));
				}
			}
		} catch(Exception e){
			Log.log(e);
			success = false;
		}
		double timeTaken = (System.currentTimeMillis() - timeStart) / 1000.0;
		Log.log(allFiles.size() + " files loaded (" + timeTaken + "s)", Log.Level.INFO);
		
		this.setStatus(Status.FILES_READ, success);
		return success;
	}


	/**
	 * lädt aus dem Workspace Ordner alle Ordner die mit com.athos. beginnen
	 * @return
	 */
	protected List<File> loadAthosProjects(){
		List<File> result = new ArrayList<File>();

		for(File project : this.getWorkspace().listFiles()){
			if(project.getName().startsWith("com.athos.")){
				result.add(project);
			}
		}
		return result;
	}

	protected Map<String, JavaClass> getMasterMap() {
		return masterMap;
	}
	protected void setMasterMap(Map<String, JavaClass> masterMap) {
		this.masterMap = masterMap;
	}
	protected File getWorkspace() {
		return workspace;
	}
	protected void setWorkspace(File workspace) {
		this.workspace = workspace;
	}
	public void setWorkspace(String workspace) {
		File file = new File(workspace);
		if(!file.exists()){
			throw new IllegalArgumentException("Workspace Location does not exist");
		}
		if(!file.isDirectory()){
			throw new IllegalArgumentException("Workspace Location is not a folder");
		}
		this.workspace = new File(workspace);
	}

	/**
	 * parst die gesamte Klassenstruktur des Workspace. Ergebnisse landen in der {@link #masterMap}
	 * @return
	 */
	public boolean parseClassStructure(){
		this.setStatus(Status.STRUCTURE_PARSED, false);
		boolean success = true;
		try{
			for(File file : this.getLoadedFiles().keySet()){
				String packageName = SpecificUtil.getPackageName(file);
				JavaClass javaClass = JavaClass.parseClassSimple(this.getLoadedFiles().get(file), packageName, file);
				this.getMasterMap().put(javaClass.getQualifiedName().getValue(), javaClass);
			}
		} catch(Exception e){
			Log.log(e);
			success = false;
		}
		this.setStatus(Status.STRUCTURE_PARSED, success);
		return success;
	}
	
	/**
	 * parst die Klassen der alten Welt in JavaClass-Objekte
	 * @return
	 */
	public boolean parseOldWorld(){
		this.setStatus(Status.OLDWORLD_PARSED, false);
		boolean success =  this.parseMappedFiles(this.getOldWorldFiles(), this.getOldWorld());
		this.setStatus(Status.OLDWORLD_PARSED, success);
		return success;
	}
	
	/**
	 * parst die Klassen der neuen Welt in JavaClass-Objekte
	 * @return
	 */
	public boolean parseNewWorld(){
		this.setStatus(Status.NEWWORLD_PARSED, false);
		boolean success = this.parseMappedFiles(this.getNewWorldFiles(), this.getNewWorld());
		this.setStatus(Status.NEWWORLD_PARSED, success);
		return success;
	}

	protected boolean parseMappedFiles(Map<File, String> files, Map<String, JavaClass> destination) {
		try{
			for(File file : files.keySet()){
				String packageName = SpecificUtil.getPackageName(file);
				String fileNameWithPackage = packageName + "." + file.getName();
				JavaClass javaClass = JavaClass.parseClassAdvanced(files.get(file), packageName, file);
				destination.put(fileNameWithPackage, javaClass);
			}
		} catch(Exception e){
			Log.log(e);
			return false;
		}
		return true;
	}

	public void writeStructureToFile(File destination){
		this.writeStructure(destination, this.getMasterMap());
	}
	public void writeOldWorldToFile(File destination){
		this.writeStructure(destination, this.getOldWorld());
	}
	public void writeNewWorldToFile(File destination){
		this.writeStructure(destination, this.getNewWorld());
	}
	protected void writeStructure(File destination, Map<String, JavaClass> source){
		String text = "";
		for(JavaClass javaClass : source.values()){
			text += javaClass.toString() + "\r\n\r\n";
		}
		Util.writeFile(destination, text);
	}
	public void migrateClasses(List<String> selectedClasses){
		this.setStatus(Status.MIGRATION_COMPLETE, false);
		
		boolean allSuccessful = true;
		for(JavaClass javaClass : this.getSearchResults()){
			if(!selectedClasses.contains(javaClass.getQualifiedName().getValue())){
				continue;
			}
			Log.log("migrating " + javaClass.getQualifiedName().getValue(), Log.Level.INFO);
			allSuccessful = allSuccessful && this.migrateClass(javaClass);
		}
		
		if(allSuccessful){
			this.setStatus(Status.MIGRATION_COMPLETE, true);
		} else{
			this.setStatus(Status.MIGRATION_COMPLETE, false);
		}
	}
	public void searchClasses(String qualifiedClassToMigrate) throws Exception{
		this.setStatus(Status.SEARCH_COMPLETE, false);
		
		JavaClass root = this.getMasterMap().get(qualifiedClassToMigrate);
		if(root == null){
			this.setStatus(Status.SEARCH_COMPLETE, false);
			throw new Exception("Class could not be found.\r\n"
					+ "Did you qualify it correctly (<package>.<class>)?");
		}
		
		List<JavaClass> concernedClasses = SpecificUtil.collectAllSubclasses(qualifiedClassToMigrate, this.getMasterMap());
		this.setSearchResults(FXCollections.observableArrayList(concernedClasses));
		if(concernedClasses.size() > 0){
			this.setStatus(Status.SEARCH_COMPLETE, true);
		} else{
			this.setStatus(Status.SEARCH_COMPLETE, false);
		}
	}
	
	/**
	 * @param classToMigrate
	 * @return success
	 */
	protected boolean migrateClass(JavaClass classToMigrate){
		boolean error = false;
		try{
			String fileText = this.getLoadedFiles().get(classToMigrate.getFile());
			//falls direkte Vererbung besteht auf StandardDialog umhängen
			fileText = fileText.replaceFirst("extends\\s+?StandardDialogComponent", "extends StandardDialog");
			
			//jede Klasse der alten Welt durchgehen
			for(JavaClass javaClass : this.getOldWorld().values()){
				//enthält die Klasse einen Konstruktoraufruf der entsprechenden Klasse?
				boolean containsSimpleConstructor = fileText.matches("[\\s\\S]*?\\s*new\\s*" + javaClass.getName() + "[\\s\\S]*?");
				boolean containsImport = classToMigrate.getImports().contains(javaClass.getQualifiedName().getValue());
				boolean containsConstructor = containsSimpleConstructor && containsImport;
				boolean containsQualifiedConstructor = fileText.matches("[\\s\\S]*?\\s*new\\s*" + javaClass.getQualifiedName() + "[\\s\\S]*?");
				if(containsConstructor || containsQualifiedConstructor){ //TODO pattern für qualifizierte Konstruktoraufrufe
					//Pattern matched "new <Klasse>(...);"
					Pattern pattern = Pattern.compile("new " + javaClass.getName() + "\\s*\\([\\s\\S]*?\\)\\s*?;");
					Matcher matcher = pattern.matcher(fileText);
					
					//StringBuffer, damit die appendReplacement-Funktion genutzt werden kann
					StringBuffer replacedCons = new StringBuffer();
					while(matcher.find()){
						String matchedText = matcher.group();
						String newCon = ConFactory.getConstructorFor(matchedText, javaClass, fileText);
						matcher.appendReplacement(replacedCons, newCon);
					}
					//restlichen Code anhängen
					matcher.appendTail(replacedCons);
					fileText = replacedCons.toString();
				}
			}
			this.writeResultToFile(fileText, classToMigrate);
		} catch(Exception e){
			Log.log(e);
			error = true;
		}
		if(error){
			Log.log("Migrating " + classToMigrate.getQualifiedName().getValue() + " failed", Log.Level.WARN);
		} else{
			Log.log("Migration successful (" + classToMigrate.getQualifiedName().getValue() + ")", Log.Level.INFO);
		}
		return !error;
	}
	protected Map<String, JavaClass> getOldWorld() {
		return oldWorld;
	}
	protected void setOldWorld(Map<String, JavaClass> oldWorld) {
		this.oldWorld = oldWorld;
	}
	protected Map<String, JavaClass> getNewWorld() {
		return newWorld;
	}
	protected void setNewWorld(Map<String, JavaClass> newWorld) {
		this.newWorld = newWorld;
	}
	protected Map<File, String> getLoadedFiles() {
		return loadedFiles;
	}
	protected void setLoadedFiles(Map<File, String> loadedFiles) {
		this.loadedFiles = loadedFiles;
	}
	protected Map<File, String> getOldWorldFiles() {
		return oldWorldFiles;
	}
	protected void setOldWorldFiles(Map<File, String> oldWorldFiles) {
		this.oldWorldFiles = oldWorldFiles;
	}
	protected Map<File, String> getNewWorldFiles() {
		return newWorldFiles;
	}
	protected void setNewWorldFiles(Map<File, String> newWorldFiles) {
		this.newWorldFiles = newWorldFiles;
	}
	protected Map<Enums.Status, BooleanProperty> getStatusMap() {
		return statusMap;
	}
	public void setStatusMap(Map<Enums.Status, BooleanProperty> statusMap) {
		this.statusMap = statusMap;
	}
	protected boolean getStatus(Enums.Status status){
		return this.getStatusMap().get(status).getValue();
	}
	protected void setStatus(Enums.Status status, boolean value){
		if(!value){
			switch(status){
				case FILES_READ:
					this.getStatusMap().get(Status.OLDWORLD_PARSED).set(false);
				case OLDWORLD_PARSED:
					this.getStatusMap().get(Status.NEWWORLD_PARSED).set(false);
				case NEWWORLD_PARSED:
					this.getStatusMap().get(Status.STRUCTURE_PARSED).set(false);
				case STRUCTURE_PARSED:
					this.getStatusMap().get(Status.SEARCH_COMPLETE).set(false);
				case SEARCH_COMPLETE:
					this.getStatusMap().get(Status.MIGRATION_COMPLETE).set(false);
				case MIGRATION_COMPLETE:
					break;
			}
		}
		this.getStatusMap().get(status).set(value);
	}

	protected Map<Integer, Constructor> getIdToNewCon() {
		return idToNewCon;
	}

	protected void setIdToNewCon(Map<Integer, Constructor> idToNewCon) {
		this.idToNewCon = idToNewCon;
	}

	protected Map<Integer, Integer> getOldIdToNewId() {
		return oldIdToNewId;
	}

	protected void setOldIdToNewId(Map<Integer, Integer> oldIdToNewId) {
		this.oldIdToNewId = oldIdToNewId;
	}

	protected Map<Constructor, Integer> getOldConToId() {
		return oldConToId;
	}

	protected void setOldConToId(Map<Constructor, Integer> oldConToId) {
		this.oldConToId = oldConToId;
	}
	
	protected Constructor getNewConstructorFor(Constructor oldCon){
		try{
			Integer idOld = this.getOldConToId().get(oldCon);
			Integer newId = this.getOldIdToNewId().get(idOld);
			Constructor newCon = this.getIdToNewCon().get(newId);
			return newCon;
		} catch(Exception e){
			Log.log("Alter Konstruktor besitzt kein Äquivalent in der neuen Welt (oder es besteht ein Konflikt)", Log.Level.INFO);
			return null;
		}
	}

	public ObservableList<JavaClass> getSearchResults() {
		return searchResults;
	}

	protected void setSearchResults(ObservableList<JavaClass> searchResults) {
		this.searchResults = searchResults;
		List<String> stringSearchResults = new ArrayList<>();
		for(JavaClass javaClass : searchResults){
			stringSearchResults.add(javaClass.getQualifiedName().getValue());
		}
		this.setStringSearchResults(stringSearchResults);
	}

	public ObservableList<String> getStringSearchResults() {
		return stringSearchResults;
	}

	protected void setStringSearchResults(List<String> stringSearchResults) {
		this.getStringSearchResults().clear();
		this.getStringSearchResults().addAll(stringSearchResults);
	}
	
	protected void writeResultToFile(String fileText, JavaClass javaClass){
		String path = javaClass.getFile().getAbsolutePath();
		String newPath = path.substring(0, path.length() - 5); //.java entfernen
		newPath += "Cute.java";
		Util.writeFile(new File(newPath), fileText);
	}
}
