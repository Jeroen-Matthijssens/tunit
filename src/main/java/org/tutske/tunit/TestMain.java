package org.tutske.tunit;

import java.io.PrintStream;

import org.junit.runner.JUnitCore;
import org.junit.runner.notification.RunListener;


public class TestMain {

	protected static Class<?> [] TEST_CLASSES;

	private PrintStream out;

	public TestMain () { this (System.out); }
	public TestMain (PrintStream out) { this.out = out; }

	public static void main (String [] args) {
		new TestMain ().run (TEST_CLASSES);
	}

	public void run (Class<?> ... classes) {
		JUnitCore junit = new JUnitCore ();
		RunListener listener = new TestListener (out);
		junit.addListener (listener);

		junit.run (classes);
		out.println ();
	}

}
