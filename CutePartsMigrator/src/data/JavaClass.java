package data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import util.SpecificUtil;

public class JavaClass {

	/**
	 * Klassenname
	 */
	private StringProperty name;
	/**
	 * package
	 */
	private StringProperty packageName;
	/**
	 * Klasse von der geerbt wird
	 */
	private String superclassName;
	/**
	 * Konstruktoren
	 */
	private List<Constructor> constructors = new ArrayList<>();
	/**
	 * importierte Klassen
	 */
	private List<String> imports = new ArrayList<>();
	/**
	 * zugehörige .java-Datei
	 */
	private File file;

	/**
	 * Objekt für Java Klasse
	 * @param name
	 * @param packageName
	 * @param superclassName
	 * @param konstruktoren
	 */
	public JavaClass(String name, String packageName, String superclassName, List<Constructor> konstruktoren,
			List<String> imports, File file) {
		super();
		this.setName(name);
		this.setPackageName(packageName);
		this.setSuperclassName(superclassName);
		this.setConstructors(konstruktoren);
		this.setImports(imports);
		this.setFile(file);
	}

	public String getPackageName() {
		return packageName.getValue();
	}
	public String getSuperclassName() {
		return superclassName;
	}
	public List<Constructor> getConstructors() {
		return constructors;
	}
	protected void setPackageName(String packageName) {
		this.packageName.setValue(packageName);
	}
	protected void setSuperclassName(String superclassName) {
		if(superclassName != null){
			superclassName = superclassName.trim();
		}
		this.superclassName = superclassName;
	}
	protected void setConstructors(List<Constructor> constructors) {
		this.constructors = constructors;
	}
	public String getName() {
		return name.getValue();
	}
	protected void setName(String name) {
		this.name.setValue(name);
	}
	public JavaClass getSuperClass(Map<String, JavaClass> map){
		return map.get(this.getSuperclassName());
	}

	/**
	 * parst eine (kompilierbare) .java-Datei (inkl. Konstruktoren)
	 * @param fileText
	 * @return
	 */
	public static JavaClass parseClassAdvanced(String fileText, String packageName, File file){
		fileText = fileText.replaceAll("\\Q/*\\E[\\s\\S]*?\\Q*/\\E", ""); //Kommentare entfernen
		JavaClass result = parseClassSimple(fileText, packageName, file);
		result.setConstructors(extractConstructors(fileText, result.getName()));
		return result;
	}
	
	protected static List<Constructor> extractConstructors(String fileText, String className){
		String classBody = fileText.substring(fileText.indexOf("{") + 1, fileText.lastIndexOf("}"));
		classBody = SpecificUtil.removeMethodBodies(classBody);
		classBody = classBody.replaceAll("\\Q//\\E[\\s\\S]*?\\n", "");
		
		List<Constructor> results = new ArrayList<>();
		for(String method : classBody.split("\\)")){
			method += ")";
			if(method.matches("[\\s\\S]* \\Q" + className + "\\E\\s*\\([\\s\\S]*")){
				String parameters = method.substring(method.indexOf("(") + 1, method.indexOf(")"));
				Constructor con = Constructor.parse(className, method, parameters);
				results.add(con);
			}
		}
		return results;
	}
	
	/**
	 * parst eine (kompilierbare) .java-Datei (ohne Konstruktoren)
	 * @param fileText
	 * @param packageName
	 * @return
	 */
	public static JavaClass parseClassSimple(String fileText, String packageName, File file){
		fileText = fileText.replaceAll("\\Q/*\\E[\\s\\S]*?\\Q*/\\E", ""); //Kommentare entfernen
		ArrayList<String> imports = extractImports(fileText);
		String superClassName = getQualifiedClass(extractSuperClass(fileText), imports, packageName);
		String className = file.getName().replace(".java", "");
		return new JavaClass(className, packageName, superClassName, new ArrayList<Constructor>(), imports, file);
	}

	/**
	 * extrahiert die Namen der Imports
	 * @param fileText
	 * @return Liste der importierten Klassen
	 */
	protected static ArrayList<String> extractImports(String fileText) {
		ArrayList<String> imports = new ArrayList<String>();
		String importsPart = fileText.substring(0, fileText.indexOf("{"));
		String[] lines = importsPart.split("\n+");
		for(String line : lines){
			if(line.startsWith("import ")){
				String importedClass = line.substring(line.indexOf(" ") + 1, line.indexOf(";"));
				imports.add(importedClass);
			}
		}
		return imports;
	}
	
	/**
	 * extrahiert Oberklasse
	 * @param fileText
	 * @return
	 */
	protected static String extractSuperClass(String fileText){
		String classHead = fileText.substring(0, fileText.indexOf("{"));
		if(classHead.contains("extends")){
			if(classHead.contains("implements")){
				classHead = classHead.substring(0, classHead.indexOf("implements"));
			}
			String superClass = classHead.substring(classHead.indexOf("extends") + "extends".length()).trim();
			return superClass;
		} else{
			return null;
		}
	}
	
	/**
	 * qualifiziert Superklasse mit package Namen
	 * @param superClass
	 * @param imports
	 * @param packageName
	 * @return
	 */
	protected static String getQualifiedClass(String superClass, ArrayList<String> imports, String packageName){
		if(superClass == null){
			return superClass;
		}
		
		//ist schon qualifiziert?
		if(superClass.contains(".")){
			return superClass;
		}
		
		//ist importiert?
		for(String item : imports){
			if(item.endsWith(superClass)){
				return item;
			}
		}
		
		//muss dann wohl im selben package liegen
		return packageName + "." + superClass;
	}

	public List<String> getImports() {
		return imports;
	}
	protected void setImports(List<String> imports) {
		this.imports = imports;
	}
	
	@Override
	public String toString(){
		String result = "[\r\n" +
				"package : " + this.getPackageName() + ";\r\n" +
				"name : " + this.getName() + ";\r\n" +
				"superclass : " + this.getSuperclassName() + ";\r\n" +
				"imports : \r\n";
		for(String importClass : this.getImports()){
			result += "\t" + importClass + " | \r\n";
		}
		if(!this.getImports().isEmpty()){
			result = result.substring(0, result.lastIndexOf("|")) + "\r\n";
		}
		if(this.isOldWorld() || this.isNewWorld()){
			result += "constructors : \r\n";
			for(Constructor con : this.getConstructors()){
				boolean conflicting = this.hasConflictingConstructors(con);
				result += "\t(\r\n" + con.toString()
						+ (conflicting ? ";\r\n\tCONFLICTING" : "")
						+ "\t)\r\n";
			}
		}
		result += "]";
		return result;
	}
	
	public boolean isOldWorld(){
		return this.getPackageName().equalsIgnoreCase("com.athos.webComponents");
	}
	
	public boolean isNewWorld(){
		return this.getPackageName().equalsIgnoreCase("com.athos.cutecomponents");
	}
	
	public StringProperty getQualifiedName(){
		StringProperty qualifiedNameProperty = new SimpleStringProperty();
		qualifiedNameProperty.bind(Bindings.concat(this.getPackageNameProperty(), ".", this.getNameProperty()));
		return qualifiedNameProperty;
	}
	
	public StringProperty getPackageNameProperty(){
		return this.packageName;
	}
	
	public StringProperty getNameProperty(){
		return this.name;
	}
	
	protected boolean hasConflictingConstructors(Constructor master){
		for(Constructor con : this.getConstructors()){
			if(con != master && con.isConflicting(master)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * darf auch mehr Konstruktoren haben, wenn alle Parameter gleich heißen
	 * @param paramNumber
	 * @return
	 */
	public boolean hasExactlyOneConstructor(int paramNumber){
		List<Constructor> consFound = new ArrayList<>();
		for(Constructor con : this.getConstructors()){
			if(con.getParameterList().size() == paramNumber){
				consFound.add(con);
			}
		}
		if(consFound.size() > 1){
			Constructor master = consFound.get(0);
			for(Constructor con : consFound){
				if(con.isConflicting(master)){
					return false;
				}
			}
		}
		return consFound.size() >= 1;
	}
	
	/**
	 * gibt den Parameter für die angegebene Anzahl Parameter zurück<br/>
	 * bei Verwendung vorher sicherstellen dass ein solcher existiert (s. {@link #hasExactlyOneConstructor(int)})
	 * @param number
	 * @return
	 */
	public Constructor getConstructorForParamNumber(int number){
		for(Constructor con : this.getConstructors()){
			if(con.getParameterList().size() == number){
				return con;
			}
		}
		throw new IllegalStateException("no " + this.getName() + " Constructor with " + number + " parameters was found. Check hasExactlyOneConstructor()");
	}

	public File getFile() {
		return file;
	}

	protected void setFile(File file) {
		this.file = file;
	}
	
	public Constructor getConstructorFor(String matchedText){
		String parameters = matchedText.substring(matchedText.indexOf("(") + 1, matchedText.lastIndexOf(")")).trim();
		int paramNumber;
		if(parameters.isEmpty()){
			paramNumber = 0;
		} else{
			String textWithoutIllegalCommas = matchedText.replaceAll("\"[\\s\\S]*?\"", "quote").replaceAll("\\([^,]*\\)", "");
			String[] parts = textWithoutIllegalCommas.split(",");
			paramNumber = parts.length;
		}
		
		if(this.hasExactlyOneConstructor(paramNumber)){
			return this.getConstructorForParamNumber(paramNumber);
		}
		return null;
	}
}
