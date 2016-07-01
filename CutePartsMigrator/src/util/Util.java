package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import log.Log;

public class Util {
	/**
	 * 
	 * @param list
	 * @param target
	 * @return ob in der Liste ein regulärer Ausdruck {@code regex} vorhanden ist, für den gilt: <p>{@code target.matches(regex) == true}</p>
	 */
	public static boolean containsRegexMatch(ArrayList<String> list, String target){
		for(String regex : list){
			if(target.matches(regex)){
				return true;
			}
		}
		return false;
	}

	public static String readFile(File f){
		String result = "";
		try(BufferedReader reader = new BufferedReader(new FileReader(f))){
			String line = reader.readLine();
			while(line != null){
				result += line;
				line = reader.readLine();
				if(line != null){
					line = "\r\n" + line;
				}
			}
			Log.log("read File " + f.getName(), Log.Level.DEBUG);
		} catch(Exception e){
			Log.log("Exception while reading file " + f.getAbsolutePath(), Log.Level.ERROR);
			Log.log(e);
		}
		return result;
	}

	/**
	 * überschreibt ggf. die vorher vorhandene Datei
	 * @param f
	 * @param text
	 */
	public static void writeFile(File f, String text){
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(f, false))){
			writer.write(text);
			Log.log("wrote File " + f.getName(), Log.Level.DEBUG);
		} catch(Exception e){
			Log.log("Exception while writing file " + f.getAbsolutePath(), Log.Level.ERROR);
			Log.log(e);
		}
	}

	/**
	 * überschreibt ggf. die vorher vorhandene Datei
	 * @param f
	 * @param text
	 */
	public static void appendFile(File f, String text){
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(f, true))){
			writer.write(text);
		} catch(Exception e){
			Log.log("Exception während Util.appendFile(" + f.getAbsolutePath() + ", " + text + ")", Log.Level.ERROR);
			Log.log(e);
		}
	}

	public static String[] getKundenListe(){
		return new String[]{
				"Ansbach",
				"Augsburg",
				"Bamberg",
				"Bergstrasse",
				"Bielefeld",
				"Bonn",
				"Breisgau",
				"Dieburg",
				"Donauwald",
				"Emsland",
				"Fuerstenwalde",
				"Fuerth",
				"Giessen",
				"Hoexter",
				"Kiel",
				"Kronach",
				"Ludwigsburg",
				"Ludwigslust",
				"Lueneburg",
				"Meinhardt",
				"Miltenberg",
				"Neustadt",
				"Nienburg",
				"Offenbach",
				"Osnabrueck",
				"Pfaffenhofen",
				"Pforzheim",
				"Pinneberg",
				"Ravensburg",
				"Remsmurr",
				"Rheinhunsrueck",
				"Rheinpfalz",
				"Saarbruecken",
				"Schaumburg",
				"Schwarzwaldbaar",
				"Starnberg",
				"Stuttgart",
				"SuedwestPfalz",
				"UDB",
				"Verden",
				"XmlServiceTest"
		};
	}

	public static String[] getModifiedKundenListe(String append){
		String[] list = getKundenListe();
		for(int i = 0; i < list.length; i++){
			list[i] = (list[i] + append).toLowerCase();
		}
		return list;
	}

	/**
	 * gibt das Projektverzeichnis zurück (ohne {@code \} am Ende)
	 * @param file
	 * @return
	 */
	public static String getProjectDirectory(File file){
		String path = file.getAbsolutePath();
		int workspaceEnd = path.indexOf("workspace") + "workspace\\".length();
		String pathStartingWithProject = path.substring(workspaceEnd);
		int projectEnd = pathStartingWithProject.indexOf("\\");
		if(projectEnd == -1){
			projectEnd = path.length();
		}
		String result = path.substring(0, projectEnd + workspaceEnd);
		return result;
	}

	/**
	 * gibt nur den Projektnamen zurück (com.athos.wastemanagement.<kunde>)
	 * @param file
	 * @return
	 */
	public static String getProjectName(File file){
		String projectPath = getProjectDirectory(file);
		String result = projectPath.substring(projectPath.lastIndexOf("\\") + 1);
		return result;
	}

	public static Document parseXML(File file){
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			return doc;
		} catch (Exception e) {
			Log.log(e);
		}
		return null;
	}

	/**
	 * 
	 * @param list
	 * @param master
	 * @return {@code true} wenn für mindestens ein Element {@code element} aus {@code list} gilt: <br/>
	 * {@code master.contains(element) == true}
	 */
	public static boolean containsContainsMatch(ArrayList<String> list, String master){
		for(String element : list){
			if(master.contains(element)){
				return true;
			}
		}
		return false;
	}

	public static boolean containsContainsRegexMatch(ArrayList<String> list, String realFile){
		for(String element : list){
			Pattern pattern = Pattern.compile(element);
			Matcher matcher = pattern.matcher(realFile);
			if(matcher.find()){
				return true;
			}
		}
		return false;
	}

	public static ArrayList<String> splitFilenames(String text){
		ArrayList<String> result = new ArrayList<>();
		if((text.length() - text.replace("\"", "").length()) % 2 == 1){
			Log.log("Ungerade Anzahl Anführungszeichen in Dateiname", Log.Level.ERROR);
			return result;
		}
		while(text.contains("\"")){
			int quoteStart = text.indexOf("\"");
			String textAfterQuote = text.substring(quoteStart + 1);
			int quoteEnd = textAfterQuote.indexOf("\"") + quoteStart + 1 + 1;
			String filename = textAfterQuote.substring(0, textAfterQuote.indexOf("\"")).trim();
			result.add(filename);
			text = text.substring(0, quoteStart) + text.substring(quoteEnd);
		}
		String[] filenames = text.replaceAll("\\s+", " ").split(" ");
		for(String filename : filenames){
			result.add(filename);
		}
		return result;
	}

	public static String arrayListToString(ArrayList<String> list){
		String result = "[";
		for(String s : list){
			result += s + ", ";
		}
		result = result.substring(0, result.length() - 2) + "]";
		return result;
	}

	public static ArrayList<String> toLowerCase(ArrayList<String> list){
		ArrayList<String> result = new ArrayList<>();
		for(String s : list){
			result.add(s.toLowerCase());
		}
		return result;
	}

	public static boolean fileContains(File file, String target, boolean regex){
		if(!file.exists() || target == null){
			return false;
		}
		String fileText = readFile(file);
		if(regex){
			Pattern pattern = Pattern.compile(target);
			Matcher matcher = pattern.matcher(fileText);
			return matcher.find();
		} else{
			return fileText.contains(target);
		}
	}

	public static boolean fileStartsWith(File file, String target, boolean regex){
		if(!file.exists() || target == null){
			return false;
		}
		String fileText = readFile(file);
		if(regex){
			Pattern pattern = Pattern.compile("\\A" + target); //\A = regex für start of content
			Matcher matcher = pattern.matcher(fileText);
			return matcher.find();
		} else{
			return fileText.startsWith(target);
		}
	}

	public static boolean fileEndsWith(File file, String target, boolean regex){
		if(!file.exists() || target == null){
			return false;
		}
		String fileText = readFile(file);
		if(regex){
			Pattern pattern = Pattern.compile(target + "\\z"); //\z = regex für end of content
			Matcher matcher = pattern.matcher(fileText);
			return matcher.find();
		} else{
			return fileText.endsWith(target);
		}
	}

	public static boolean isNullOrEmpty(String s){
		return s == null || s.isEmpty();
	}

	public static boolean containsEqualNames(ArrayList<File> files){
		for(File master : files){
			for(File slave : files){
				if(master.getName().equals(slave.getName()) && master != slave){
					return true;
				}
			}
		}
		return false;
	}

	public static String getStackTraceAsString(Exception e){
		StringWriter stringwriter = new StringWriter();
		PrintWriter printwriter = new PrintWriter(stringwriter);
		e.printStackTrace(printwriter);
		return stringwriter.toString();
	}
}
