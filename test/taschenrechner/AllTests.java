package taschenrechner;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {
	public static Test suite() {
		TestSuite mySuite = new TestSuite("Taschenrechner TestSuite");
		mySuite.addTestSuite(taschenrechner.AdditionTest.class);
		return mySuite;
	}
}
