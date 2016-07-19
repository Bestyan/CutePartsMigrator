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
				call = getButtonColumn(parameters);
				break;
				
			case "WebDialogListCheckBoxComponent":
				call = getCheckboxColumn(parameters);
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
				call = getFlexibleList();
				break;
				
			case "WebDialogListHeadComponent":
				Log.log(new Exception("WebDialogListHeadComponent has no known equivalent in the new world"));
				break;
				
			case "WebDialogListInputComponent":
				call = getSLEColumn(parameters);
				break;
				
			case "WebDialogListSelectComponent":
				call = getSelectColumn(parameters);
				break;
				
			case "WebDialogListTextComponent":
				call = getTextColumn(parameters);
				break;
				
			case "WebEmptyTableColumnComponent":
				Log.log("WebEmptyTableColumnComponent has no known equivalent in the new world", Log.Level.INFO);
				break;
				
			case "WebDialogMaskComponent":
				//WebDialogMask muss von Fall zu Fall durch Cluster / Dialogblock ersetzt werden. Dabei muss aber nachgedacht werden und denken kann dieses Tool nicht
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
				call = conCall + "[delete this]"; //wird nicht verwendet und soll nicht als Altlast mit übernommen werden
				break;
				
			case "WebInputComponent":
				call = getSLE(parameters);
				break;
				
			case "WebLIComponent":
				call = "addItem(name)";
				break;
				
			case "WebLinkListComponent": //wird nur in StandardMenuSelectionComponent verwendet -> StandardMenu in neuer Welt
				break;
				
			case "WebListContainerElement":
				Log.log(new Exception("WebListContainerElement muss manuell umgestellt werden. Koordination mit Martin"));
				break;
				
			case "WebLiveSearchDialogElement":
				Log.log(new Exception("WebLiveSearchDialogElement muss manuell umgestellt werden. Koordination mit Martin"));
				break;
				
			case "WebMapAreaDescription"://wird nicht verwendet, gehört zu WebImageMapAreaComponent
				break;
				
			case "WebOLComponent":
				call = "addItemList(name, true)";
				break;
				
			case "WebRadioGroupComponent":
				call = "addRadioGroup(name, source, itemsAttribute, itemSelectedAttribute, isGetSet)";
				break;
				
			case "WebSelectComponent":
				call = "addComboBox(name, label, source, itemsAttribute, itemSelectedAttribute, isGetSet)";
				break;
			
			case "WebSimpleDisplayListComponent": //Liste von Strings wird innerhalb eines divs in p-Tags dargestellt -> neue Welt: ul / li / text
				call = getTextItemList(parameters);
				break;
				
			case "WebSpanComponent":
				call = getText(parameters);
				break;
				
			case "WebSysdataComponent":
				call = getSysData(parameters);
				break;
				
			case "WebTableColumnCheckBoxComponent":
				call = getColumnCheckBox(parameters);
				break;
				
			case "WebTableColumnComponent":
				call = getColumn(parameters);
				break;
				
			case "WebTableColumnInputComponent":
				call = getTableSLEColumn(parameters);
				break;
				
			case "WebTableColumnSelectComponent":
				call = getTableSelectColumn(parameters);
				break;
				
			case "WebTableComponent":
			case "WebTableRowComponent":
			case "WebTableDataComponent":
				call = conCall + "[manuell umstellen]";
				break;
				
			case "WebTableListComponent":
				call = getTableList();
				break;
				
			case "WebTextAreaComponent":
				call = getTextArea();
				break;
				
			case "WebTextComponent":
				call = getText(parameters) + ".setOutputEscaping(false)";
				break;
				
			case "WebTextblockComponent":
				call = "addTextblock(name)";
				break;
				
			case "WebULComponent":
				call = "addItemList(name, false)";
				break;
				
			case "WebXMLContentComponent":
				Log.log(new IllegalStateException("workspace search found no matches for that one.. wtf"));
				break;
				
			case "WebXMLDocumentComponent":
				call = "addSpecialData(name, source, rootAttribute, isGetSet)";
				
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
	
	private static String getTextArea() {
		return "addMLE(name, label, maxLength, source, textAttribute, isGetSet)";
	}

	private static String getTableList(){
		return "addTableList(name, source, itemsAttribute, isGetSet)";
	}
	
	private static String getTableSelectColumn(List<String> parameters){
		boolean isCombobox = parameters.get(6).toLowerCase().contains("select");
		switch(parameters.size()){
			case 8:
				if(isCombobox){
					return "addComboBoxColumn(name, " + parameters.get(1) + ", " + parameters.get(7) + ", " + parameters.get(6) + ")";
				} else{
					return "addRadioGroupColumn(name, " + parameters.get(1) + ", " + parameters.get(7) + ", " + parameters.get(6) + ")";
				}
			case 9:
				if(isCombobox){
					return "addComboBoxColumn(name, " + parameters.get(1) + ", " + parameters.get(7) + ", " + parameters.get(6) + ", " + parameters.get(8) + ")";
				} else{
					return "addRadioGroupColumn(name, " + parameters.get(1) + ", " + parameters.get(7) + ", " + parameters.get(6) + ", " + parameters.get(8) + ")";
				}
			default:
				Log.log(new Exception("Unknown Constructor"));
				return null;
		}
	}
	
	private static String getColumn(List<String> parameters) {
		switch(parameters.size()){
			case 6:
				return "addTextColumn(name, headline, " + parameters.get(5) + ", true)";
			case 8:
				if(parameters.get(7).toLowerCase().contains("button")){
					return "addButtonColumn(name, " + parameters.get(1) + ", " + parameters.get(6) + ", event, method)";
				} else{
					return "addTextColumn(name, " + parameters.get(1) + ", " + parameters.get(6) + ", true)";
				}
			case 7:
				if(parameters.get(6).toLowerCase().contains("button")){
					return "addButtonColumn(name, null, " + parameters.get(5) + ", event, method)";
				} else if(parameters.get(6).toLowerCase().contains("text")){
					return "addTextColumn(name, null, " + parameters.get(5) + ", true)";
				} else{
					return "addTextColumn(name, " + parameters.get(1) + ", " + parameters.get(6) + ", true)";
				}
			default:
				Log.log(new Exception("Unknown constructor for WebTableColumnComponent found. Please check"));
				return null;
		}
	}

	private static String getColumnCheckBox(List<String> parameters) {
		boolean hasLabel = parameters.size() == 7 && !parameters.get(1).equals("\"\"");
		return "addCheckBoxColumn(name, headline, " + (hasLabel ? parameters.get(1) : "null") + ", " + parameters.get(parameters.size() - 1) + ", false)";
	}

	private static String getSysData(List<String> parameters) {
		return "addSysdata(name, " + parameters.get(1) + ", source, valueAttribute, isGetSet)";
	}

	private static String getText(List<String> parameters) {
		return "addText(name, " + parameters.get(1) + ")";
	}

	private static String getTextItemList(List<String> parameters){
		return "addFlexibleList(name, source, " + parameters.get(2) + ", isGetSet).addTextColumn(name, null, \"toString\", true)";
	}
	
	private static String getTextColumn(List<String> parameters) {
		switch(parameters.size()){
			case 7:
				boolean hasLabel = !parameters.get(1).equals("\"\"");
				return "addTextColumn(name, headline, " + parameters.get(6) + (hasLabel ? "[requires edit of getter method]" : "") + ", true)";
			case 6: //wird laut workspace Suche nicht verwendet
			case 8: //wird laut workspace Suche nicht verwendet
			default:
				Log.log(new Exception("Code that was assumed not to exist was found. Please check"));
				return null;
		}
	}

	private static String getSelectColumn(List<String> parameters) {
		String labelCode = "";
		if(!parameters.get(1).equals("\"\"")){
			labelCode = ".setLabel(" + parameters.get(1) + ")";
		}
		
		boolean isCombobox = parameters.get(5).contains("select");
		switch(parameters.size()){
			case 8:{
				if(isCombobox){
					return "addComboBoxColumn(name, headline, " + parameters.get(7) + ", " + parameters.get(6) + ")" + labelCode;
				} else{
					if(!labelCode.isEmpty()){
						Log.log(new Exception("Label Code found but ColumnRadioGroup cannot have a Label - what now?"));
					}
					return "addRadioGroupColumn(name, headline, " + parameters.get(7) + ", " + parameters.get(6) + ")";
				}
			}
			case 9:{
				if(isCombobox){
					return "addComboBoxColumn(name, headline, " + parameters.get(7) + ", " + parameters.get(6) + ", " + parameters.get(8) + ")" + labelCode;
				} else{
					if(!labelCode.isEmpty()){
						Log.log(new Exception("Label Code found but ColumnRadioGroup cannot have a Label - what now?"));
					}
					return "addRadioGroupColumn(name, headline, " + parameters.get(7) + ", " + parameters.get(6) + ", " + parameters.get(8) + ")";
				}
			}
		}
		return null;
	}

	private static String getSLEColumn(List<String> parameters) {
		if(parameters.get(1).equals("\"\"")){
			return "addSLEColumn(name, headline, " + parameters.get(8) + ", " + parameters.get(6) + ")";
		} else{
			return "addSLEColumn(name, headline, " + parameters.get(1) + ", false, " + parameters.get(8) + ", " + parameters.get(6) + ")";
		}
	}
	
	private static String getTableSLEColumn(List<String> parameters) {
		return "addSLEColumn(name, " + parameters.get(1) + ", " + parameters.get(8) + ", " + parameters.get(6) + ")";
	}

	private static String getCheckboxColumn(List<String> parameters) {
		return "addCheckBoxColumn(name, headline, " + parameters.get(1) + ", " + parameters.get(6) + ", false)";
	}

	private static String getButtonColumn(List<String> parameters) {
		if(!parameters.get(6).contains("button")){
			Log.log(new Exception("Unknown Action Type: " + parameters.get(6)));
		}
		return "addButtonColumn(name, headline, " + parameters.get(5) + ", event, method)";
	}

	private static String getFlexibleList() {
		return "addFlexibleList(name, source, listSource, isGetSet)";
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
