package test;

import util.SpecificUtil;
import util.Util;

public class Test {

	public static void main(String[] args) {
		System.out.println(Util.arrayListToString(SpecificUtil.extractParams("new WebDialogElementComponent(\"bla\", new ASD(new B(a, b), \"s\", \"d\"), \"(:\", (hi))")));
	}

}
