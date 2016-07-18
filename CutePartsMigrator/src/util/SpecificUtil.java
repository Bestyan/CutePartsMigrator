package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.JavaClass;

/**
 * projektspezifische Util-Klasse
 * @author bschattenberg
 *
 */
public final class SpecificUtil {
	//keine Instanzierung
	private SpecificUtil(){	};
	
	public static String getPackageName(File file){
		String packageName = null;
		String path = file.getAbsolutePath().replace("\\", ".").replace("/", ".");
		int packageStart = path.lastIndexOf("com.athos.");
		String packagePath = path.substring(packageStart);
		String packageType = null;
		if(packagePath.contains("com.athos.wastemanagement")){
			packageType = "com.athos.wastemanagement.";
		} else{
			packageType = "com.athos.";
		}
		int packageEnd = packagePath.replace(packageType, "").indexOf(".") + packageType.length();
		packageName = packagePath.substring(0, packageEnd);
		return packageName;
	}
	
	/**
	 * @param file
	 * @return ob die Datei im Package der alten Welt (= webcomponents) liegt
	 */
	public static boolean isOldWorld(File file){
		return file.getAbsolutePath().replace("\\", ".").replace("/", ".").toLowerCase().contains("com.athos.webcomponents");
	}
	
	/**
	 * @param file
	 * @return ob die Datei im Package der neuen Welt (= cutecomponents) liegt
	 */
	public static boolean isNewWorld(File file){
		return file.getAbsolutePath().replace("\\", ".").replace("/", ".").toLowerCase().contains("com.athos.cutecomponents");
	}
	
	public static String removeMethodBodies(String classBody){
		//alle Strings durch Leerstrings ersetzen
		classBody = classBody.replaceAll("\"[^\"]*\"", "\"\"");
		while(classBody.contains("{")){
			int methodBodyStart = classBody.indexOf("{");
			int methodBodyEnd = methodBodyStart + 1;
			String currentScope = classBody.substring(methodBodyStart + 1);
			
			for(int openBraces = 1; openBraces > 0;){
				boolean isOpenBracesBeforeClosingBraces = currentScope.indexOf("{") != -1 && currentScope.indexOf("{") < currentScope.indexOf("}");
				if(isOpenBracesBeforeClosingBraces){
					//falls als nächstes eine öffnende Klammer kommt, openBraces um 1 erhöhen und den currentScope auf das zeichen nach der { setzen
					methodBodyEnd += currentScope.indexOf("{") + 1;
					currentScope = currentScope.substring(currentScope.indexOf("{") + 1);
					openBraces++;
				} else{
					methodBodyEnd += currentScope.indexOf("}") + 1;
					currentScope = currentScope.substring(currentScope.indexOf("}") + 1);
					openBraces--;
				}
			}
			classBody = classBody.substring(0, methodBodyStart) + classBody.substring(methodBodyEnd);
		}
		return classBody;
	}
	
	/**
	 * sammelt alle Klassen, die von der übergebenen Klasse erben (direkt oder indirekt)
	 * @param className
	 * @param masterMap
	 * @return
	 */
	public static List<JavaClass> collectAllSubclasses(String className, Map<String, JavaClass> masterMap){
		List<String> classNames = new ArrayList<>();
		classNames.add(className);
		boolean foundNew;
		do{
			foundNew = false;
			for(JavaClass javaClass : masterMap.values()){
				if(classNames.contains(javaClass.getQualifiedName())){
					continue;
				}
				if(classNames.contains(javaClass.getSuperclassName())){
					classNames.add(javaClass.getQualifiedName());
					foundNew = true;
				}
			}
		} while(foundNew);
		
		List<JavaClass> results = new ArrayList<>();
		for(String name : classNames){
			results.add(masterMap.get(name));
		}
		return results;
	}
	
	public static int replaceLengthDifference(String text, String replace){
		return text.length() - text.replace(replace, "").length();
	}
	
	public static boolean containsEqualOpenClosedBrackets(String text){
		text = text.replaceAll("\".*?\"", "");
		int openBrackets = replaceLengthDifference(text, "(");
		int closedBrackets = replaceLengthDifference(text, ")");
		return openBrackets == closedBrackets;
	}
	
	/**
	 * extrahiert Parameter
	 * @param methodHead z.B. new DialogElement("asd", "(a, b, c)", (a+b));
	 * @return
	 */
	public static List<String> extractParams(String methodHead){
		String bracketContent = methodHead.substring(methodHead.indexOf("(") + 1, methodHead.lastIndexOf(")"));
		List<String> result = new ArrayList<>();
		if(bracketContent.isEmpty()){
			return result;
		}
		while(bracketContent.replaceAll("\"[\\s\\S]*?\"", "").contains(",")){
			String currentParam = bracketContent.substring(0, bracketContent.indexOf(","));
			bracketContent = bracketContent.substring(bracketContent.indexOf(",") + 1);
			while(replaceLengthDifference(currentParam, "\"") % 2 != 0 || !containsEqualOpenClosedBrackets(currentParam)){
				currentParam += "," + bracketContent.substring(0, bracketContent.indexOf(","));
				bracketContent = bracketContent.substring(bracketContent.indexOf(",") + 1);
			}
			result.add(currentParam.trim());
		}
		result.add(bracketContent);
		return result;
	}
	
	public static String isolateInitAttributeCons(String fileText){
		Pattern pattern = Pattern.compile("public\\s+?void\\s+?initAttributeConnections\\s+?\\(");
		Matcher matcher = pattern.matcher(fileText);
		if(!matcher.find()){
			return "";
		}
		int methodStart = matcher.start();
		String currentScope = fileText.substring(methodStart);
		
		int bodyStart = 0, bodyEnd = 0;
		bodyEnd = bodyStart = currentScope.indexOf("{") + 1 + methodStart;
		
		currentScope = currentScope.substring(currentScope.indexOf("{") + 1);
		for(int openBraces = 1; openBraces > 0;){
			boolean isOpenBracesBeforeClosingBraces = currentScope.indexOf("{") != -1 && currentScope.indexOf("{") < currentScope.indexOf("}");
			if(isOpenBracesBeforeClosingBraces){
				//falls als nächstes eine öffnende Klammer kommt, openBraces um 1 erhöhen und den currentScope auf das zeichen nach der { setzen
				bodyEnd += currentScope.indexOf("{") + 1;
				currentScope = currentScope.substring(currentScope.indexOf("{") + 1);
				openBraces++;
			} else{
				bodyEnd += currentScope.indexOf("}") + 1;
				currentScope = currentScope.substring(currentScope.indexOf("}") + 1);
				openBraces--;
			}
		}
		return fileText.substring(bodyStart, bodyEnd);
	}
	
}
