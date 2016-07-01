package data;

import java.util.List;
import java.util.Map;

public class JavaClass {

	/**
	 * Klassenname
	 */
	private String name;
	/**
	 * package
	 */
	private String packageName;
	/**
	 * Klasse von der geerbt wird
	 */
	private String superclassName;
	/**
	 * Konstruktoren
	 */
	private List<Constructor> constructors;

	/**
	 * Objekt für Java Klasse
	 * @param name
	 * @param packageName
	 * @param superclassName
	 * @param konstruktoren
	 */
	public JavaClass(String name, String packageName, String superclassName, List<Constructor> konstruktoren){
		super();
		this.setName(name);
		this.setPackageName(packageName);
		this.setSuperclassName(superclassName);
		this.setConstructors(konstruktoren);
	}

	public String getPackageName() {
		return packageName;
	}
	public String getSuperclassName() {
		return superclassName;
	}
	public List<Constructor> getConstructors() {
		return constructors;
	}
	protected void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	protected void setSuperclassName(String superclassName) {
		this.superclassName = superclassName;
	}
	protected void setConstructors(List<Constructor> constructors) {
		this.constructors = constructors;
	}
	public String getName() {
		return name;
	}
	protected void setName(String name) {
		this.name = name;
	}
	public JavaClass getSuperClass(Map<String, JavaClass> map){
		return map.get(this.getSuperclassName());
	}

	/**
	 * parst eine (kompilierbare) .java-Datei
	 * @param fileText
	 * @return
	 */
	public static JavaClass parseClass(String fileText){
		//TODO parseClass
		return null;
	}
}
