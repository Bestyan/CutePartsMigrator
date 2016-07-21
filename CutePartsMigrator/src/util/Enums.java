package util;

public final class Enums {
	/**
	 * keine Instanzierung
	 */
	private Enums(){};

	/**
	 * Enum für Sichtbarkeit von Klassen und Konstruktoren
	 * @author bschattenberg
	 *
	 */
	public enum Scope{
		PRIVATE, PROTECTED, PACKAGE, PUBLIC;
	}
	
	public enum Status{
		FILES_READ, OLDWORLD_PARSED, NEWWORLD_PARSED, STRUCTURE_PARSED, SEARCH_COMPLETE, MIGRATION_COMPLETE;
	}
}
