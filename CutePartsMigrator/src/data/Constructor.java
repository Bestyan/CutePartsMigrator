package data;

import java.util.List;

import util.Enums;

public class Constructor {

	private JavaClass javaClass;
	private List<String> parameterList;
	private Enums.Scope scope;

	public Constructor(JavaClass javaClass, List<String> parameters, Enums.Scope scope){
		super();
		this.setJavaClass(javaClass);
		this.setParameterList(parameters);
		this.setScope(scope);
	}

	protected void setJavaClass(JavaClass javaClass) {
		this.javaClass = javaClass;
	}
	protected void setParameterList(List<String> parameterList) {
		this.parameterList = parameterList;
	}
	protected void setScope(Enums.Scope scope) {
		this.scope = scope;
	}
	public JavaClass getJavaClass() {
		return javaClass;
	}
	public List<String> getParameterList() {
		return parameterList;
	}
	public Enums.Scope getScope() {
		return scope;
	}

	public boolean isConflicting(Constructor constructor){
		boolean sameClass = constructor.getJavaClass().equals(this.getJavaClass());
		boolean sameNumberOfParams = constructor.getParameterList().size() == this.getParameterList().size();
		return sameClass && sameNumberOfParams;
	}
}
