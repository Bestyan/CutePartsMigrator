package logic;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.Constructor;
import data.JavaClass;
import log.Log;
import util.SpecificUtil;
import util.Util;

/**
 * ConstructorFactory<br/>
 * verwaltet das Mapping der alten Konstruktoren auf die neuen Konstruktoren
 * @author bschattenberg
 *
 */
public final class ConFactory {
	/**
	 * keine Instanzierung
	 */
	private ConFactory(){};
	
	/**
	 * sucht den zugehörigen "Konstruktor" aus der neuen Welt heraus (add-Methode)
	 * @param conCall Konstruktoraufruf
	 * @param javaClass zugehörige Javaklasse
	 * @return
	 */
	public static String getConstructorFor(String conCall, JavaClass javaClass, String fileText){
		String call = null;
		List<String> parameters = SpecificUtil.extractParams(conCall);
		String partName = parameters.get(0);
		switch(javaClass.getName()){
			case "FileUploadComponent":
				call = getFileUpload();
				break;
				
			case "WebActionComponent":
				call = getAction();
				break;
				
			case "WebButtonComponent":
				call = getButton();
				break;
				
			case "WebCheckBoxComponent":
				call = getCheckBox();
				break;
				
			case "WebClusterComponent":
				call = getCluster();
				break;
				
			case "WebDialogElementComponent":
				call = getDialogElementComponent(parameters);
				break;
				
			case "WebDialogListActionComponent":
				Log.log(new Exception("WebDialogListActionComponent has no known equivalent in the new world"));
				break;
				
			case "WebDialogListCheckBoxComponent":
				Log.log(new Exception("WebDialogListCheckBoxComponent has no known equivalent in the new world"));
				break;
				
			case "Web2ColumnDialogMaskComponent":
				Log.log(new Exception("Web2ColumnDialogMaskComponent has no known equivalent in the new world"));
				break;
				
			case "Web2RowTableListComponent":
				Log.log(new Exception("Web2RowTableListComponent has no known equivalent in the new world"));
				break;
				
			case "Web3RowTableListComponent":
				Log.log(new Exception("Web3RowTableListComponent has no known equivalent in the new world"));
				break;
				
			case "WebDialogListComponent":
				Log.log(new Exception("WebDialogListComponent has no known equivalent in the new world"));
				break;
				
			case "WebDialogListHeadComponent":
				Log.log(new Exception("WebDialogListHeadComponent has no known equivalent in the new world"));
				break;
				
			case "WebDialogListInputComponent":
				Log.log(new Exception("WebDialogListInputComponent has no known equivalent in the new world"));
				break;
				
			case "WebDialogListSelectComponent":
				Log.log(new Exception("WebDialogListSelectComponent has no known equivalent in the new world"));
				break;
				
			case "WebDialogListTextComponent":
				Log.log(new Exception("WebDialogListTextComponent has no known equivalent in the new world"));
				break;
				
			case "WebEmptyTableColumnComponent":
				Log.log(new Exception("WebEmptyTableColumnComponent has no known equivalent in the new world"));
				break;
				
			case "WebDialogMaskComponent":
				//WebDialogMask muss von Fall zu Fall durch Cluster / Dialogblock ersetzt werden. Dabei muss aber gedacht werden und denken kann dieses Tool nicht
				break;
				
			case "WebDialogXMLComponent":
				call = getDialogXMLComponent();
				break;
				
			case "WebDivisionComponent":
				call = getCluster();
				break;
				
			case "WebHRefComponent":
				call = getHref(parameters);
				break;
				
			case "WebHRefDialogElementComponent":
				call = getHrefDialogElement(parameters);
				break;
				
			case "WebIFrameContentComponent":
				call = getIFrame();
				break;
				
			case "WebImageMapAreaComponent":
				call = "WebImageMapAreaComponent[delete this]"; //wird nicht verwendet und soll nicht als Altlast mit übernommen werden
				break;
				
			case "WebInputComponent":
				call = getSLE(parameters);
			
			default:
				break;
		}
		
		if(call != null){
			call = call.replaceFirst("name", parameters.get(0));
			if(javaClass.hasExactlyOneConstructor(parameters.size())){
				Constructor con = javaClass.getConstructorForParamNumber(parameters.size());
				call = call + con.getParameterListForMigration(parameters);
				call = attachAtACInfo(partName, call, fileText);
			}
		} else{
			call = conCall;
		}
		return call;
	}
	
	private static String attachAtACInfo(String partName, String call, String fileText){
		String initAttributeCons = fileText;
		//angehängter String mit Infos über die Attributverbindungen
		if(initAttributeCons.contains(partName)){
			Pattern pattern = Pattern.compile(".*? (.*?)\\s*?=.*?getComponentNamed\\s*?\\(.*?" + partName + ".*?\\)");
			Matcher matcher = pattern.matcher(initAttributeCons);
			if(matcher.find()){
				String varName = matcher.group(1);
				Pattern atacPattern = Pattern.compile("new\\s+?AttributeToAttributeConnection\\s*?\\(" + "[^\"]*?" + "(\"[^\"]+?\")" + "[^\"]*?" + varName + "[^\"]*?" + "(\"[^\"]+?\")" + ".*?\\)");
				Matcher atacMatcher = atacPattern.matcher(initAttributeCons.substring(matcher.start()));
				String atacInfo = "{";
				for(int i = 0; i < 2 && atacMatcher.find(); i++){
					System.out.println(atacMatcher.group());
					String businessCaseAttribute = atacMatcher.group(1);
					String partAttribute = atacMatcher.group(2).replace("\"", "");
					atacInfo += businessCaseAttribute + " = " + partAttribute + ", ";
				}
				if(atacInfo.length() > 1){
					atacInfo = atacInfo.substring(0, atacInfo.length() - 2) + "}";
					call += atacInfo;
				}
			}
		}
		return call;
	}
	
	private static String getFileUpload(){
		return "addFileUpload(name, acceptedMimeTypes, multipleFilesAllowed, source, valueAttribute, isGetSet)";
	}
	
	private static String getAction(){
		return "addAction(name, text)";
	}
	
	private static String getButton(){
		return "addButton(name, text, event, method)";
	}
	
	private static String getCheckBox(){
		return "addCheckBox(name, label, source, checkedAttribute, isLabelAttribute, isGetSet)";
	}
	
	private static String getCluster(){
		return "addCluster(name)";
	}
	
	private static String getDialogElementComponent(List<String> parameters){
		switch(parameters.size()){
			case 2:
				return "addDialogBlock(name, " + parameters.get(1) + ")";
			case 5:
				return "addText(name, " + parameters.get(1) + ")";
			case 7:{
				if(Util.listContainsContains(parameters, "checkbox")){
					return "addCheckBox(name, " + parameters.get(1) + ", source, checkedAttribute, isLabelAttribute, isGetSet)";
				} else if(Util.listContainsContains(parameters, "ME")){
					return "addMLE(name, " + parameters.get(1) + ", maxLength, source, textAttribute, isGetSet)";
				}
			}
			case 8:{
				if(parameters.get(6).toLowerCase().contains("select")){
					if(parameters.get(7).equals("null")){
						return "addComboBox(name, " + parameters.get(1) + ", source, itemsAttribute, itemSelectedAttribute, isGetSet)";
					} else{
						return "addComboBox(name, " + parameters.get(1) + ", source, itemsAttribute, itemSelectedAttribute, isGetSet, " + parameters.get(7) + ")";
					}
				} else if(parameters.get(6).toLowerCase().contains("radio")){
					String labelCode = "";
					if(!parameters.get(1).isEmpty()){ //besitzt ein Label?
						labelCode = ".setLabel(" + parameters.get(1) + ", false)";
					}
					if(parameters.get(7).equals("null")){
						return "addRadioGroup(name, source, itemsAttribute, itemSelectedAttribute, isGetSet)" + labelCode;
					} else{
						return "addRadioGroup(name, source, itemsAttribute, itemSelectedAttribute, isGetSet, " + parameters.get(7) + ")" + labelCode;
					}
				} else{
					return "addSLE(name, " + parameters.get(1) + ", false, " + parameters.get(6) + ", source, sourceAttribute, isGetSet)";
				}
			}
			default:
				throw new RuntimeException("no matches for WebDialogElementComponent");
		}
	}
	
	private static String getDialogXMLComponent(){
		return "addXmlPart(name)";
	}
	
	private static String getHref(List<String> parameters){
		return "addHref(name, text, " + parameters.get(1) + ")";
	}
	
	private static String getHrefDialogElement(List<String> parameters){
		return "addHref(name, " + parameters.get(1) + ", " + parameters.get(2) + ")";
	}
	
	private static String getIFrame(){
		return "addIFrame(name, source, contentAttribute, isGetSet)";
	}
	
	private static String getSLE(List<String> parameters){
		String call = "this.addSLE(name, label, isLabelAttribute, maxLength, source, sourceAttribute, isGetSet)";
		if(parameters.size() > 1){
			call = call.replace("maxLength", parameters.get(2));
		}
		return call;
	}
}
