package data;

import java.util.ArrayList;
import java.util.List;

import log.Log;
import util.Enums;
import util.Enums.Scope;
import util.Util;

public class Constructor {
	
	private static int NEXT_ID = 1;

	private String className;
	private List<String> parameterList;
	private Enums.Scope scope;
	private int id;

	public Constructor(String className, List<String> parameters, Enums.Scope scope){
		super();
		this.setClassName(className);
		this.setParameterList(parameters);
		this.setScope(scope);
		this.setId(NEXT_ID);
		NEXT_ID++;
	}

	protected void setParameterList(List<String> parameterList) {
		this.parameterList = parameterList;
	}
	protected void setScope(Enums.Scope scope) {
		this.scope = scope;
	}
	public List<String> getParameterList() {
		return parameterList;
	}
	public Enums.Scope getScope() {
		return scope;
	}

	public boolean isConflicting(Constructor constructor){
		if(constructor.getParameterList().size() == this.getParameterList().size()){
			for(int i = 0; i < constructor.getParameterList().size(); i++){
				String param1 = this.getParameterList().get(i);
				String param2 = constructor.getParameterList().get(i);
				if(!param1.equalsIgnoreCase(param2)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * erzeugt aus den übergebenen Parametern ein Constructor-Objekt
	 * @param parameters
	 * @param methodHead
	 * @return
	 */
	public static Constructor parse(String className, String methodHead, String parameters){
		String[] params = parameters.split(",");
		List<String> parameterList = new ArrayList<>();
		for(String param : params){
			param = param.trim();
			if(param.isEmpty()){
				continue;
			}
			param = param.substring(param.indexOf(" ") + 1).trim();
			parameterList.add(param);
		}
		String methodHeadWithoutParams = methodHead.substring(0, methodHead.indexOf("("));
		Enums.Scope scope;
		if(methodHeadWithoutParams.contains("private")){
			scope = Scope.PRIVATE;
		} else if(methodHeadWithoutParams.contains("public")){
			scope = Scope.PUBLIC;
		} else if(methodHeadWithoutParams.contains("protected")){
			scope = Scope.PROTECTED;
		} else{
			scope = Scope.PACKAGE;
		}
		
		return new Constructor(className, parameterList, scope);
	}
	
	@Override
	public String toString(){
		String result = "\t\t" + this.getScope().toString() + ";\r\n" +
				"\t\tID: " + this.getId() + ";\r\n";
		for(String param : this.getParameterList()){
			result += "\t\t" + param + " | \r\n";
		}
		if(!this.getParameterList().isEmpty()){
			result = result.substring(0, result.lastIndexOf("|")) + "\r\n";
		}
		return result;
	}

	protected String getClassName() {
		return className;
	}

	protected void setClassName(String className) {
		this.className = className;
	}

	protected int getId() {
		return id;
	}

	protected void setId(int id) {
		this.id = id;
	}
	
	public String getParameterListForMigration(List<String> parameterValues){
		if(parameterValues.size() == this.getParameterList().size()){
			String result = "[";
			for(int i = 0; i < parameterValues.size(); i++){
				String parameterName = this.getParameterList().get(i);
				String parameterValue = parameterValues.get(i);
				result += parameterName + " = " + parameterValue + ", ";
			}
			if(result.length() > 1){
				result = result.substring(0, result.length() - 2);
			}
			result += "]";
			return result;
		} else{
			Log.log("Parameter List in " + this.getClassName() + " does not match", Log.Level.WARN);
			return "[parameter list size doesnt match: " + Util.arrayListToString(parameterValues) + "]";
		}
	}
}
