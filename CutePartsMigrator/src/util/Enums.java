package util;

public final class Enums {
	/**
	 * keine Instanzierung
	 */
	private Enums(){};

	/**
	 * Enum f�r Sichtbarkeit von Klassen und Konstruktoren
	 * @author bschattenberg
	 *
	 */
	public enum Scope{
		PRIVATE, PROTECTED, PACKAGE, PUBLIC;
	}
}
