package org.tutske.tunit;

import java.io.PrintStream;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;


public class TestListener extends RunListener {

	private static final String TEST_PASSED_MARKER = ".";
	private static final String ASSUMPTION_FAILED_MARKER = "-";
	private static final String TEST_IGNORED_MARKER = "?";
	private static final String TEST_FAILED_MARKER = "F";
	private static final String TEST_ERROR_MARKER = "E";

	private StringBuilder builder = new StringBuilder ();
	private PrintStream printer;
	private boolean lastPrinted = false;
	private int failedAssumptions = 0;
	private int testsInError = 0;

	public TestListener (PrintStream printer) {
		this.printer = printer;
	}

	@Override
	public void testAssumptionFailure (Failure failure) {
		super.testAssumptionFailure (failure);
		failedAssumptions += 1;
		lastPrinted = true;
		produceMarker (ASSUMPTION_FAILED_MARKER);
	}

	@Override
	public void testFailure (Failure failure) throws Exception {
		super.testFailure (failure);
		lastPrinted = true;
		if ( failure.getException () instanceof AssertionError ) {
			produceMarker (TEST_FAILED_MARKER);
		} else {
			testsInError += 1;
			produceMarker (TEST_ERROR_MARKER);
		}
	}

	@Override
	public void testStarted (Description description) throws Exception {
		super.testStarted (description);
	}

	@Override
	public void testFinished (Description description) throws Exception {
		super.testFinished (description);
		if ( lastPrinted ) { lastPrinted = false; }
		else { produceMarker (TEST_PASSED_MARKER); }
	}

	@Override
	public void testIgnored (Description description) throws Exception {
		super.testIgnored (description);
		lastPrinted = true;
		produceMarker (TEST_IGNORED_MARKER);
	}

	@Override
	public void testRunStarted (Description description) throws Exception {
		super.testRunStarted (description);
		printer.println ();
		printer.print ("Testing: ");
	}

	@Override
	public void testRunFinished (Result result) throws Exception {
		super.testRunFinished (result);
		printer.print (" done!");
		printFailures (result);
		printer.println ();
		printFailureLine (result);
		printReport (result);
		printer.println ();
	}

	private void produceMarker (String marker) {
		builder.append (marker);
		printer.print (marker);
	}

	private void printFailures (Result result) throws Exception {
		for (Failure failure : result.getFailures ()) {
			printer.println ();
			printer.println (failure);
			failure.getException ().printStackTrace (printer);
		}
	}

	private void printFailureLine (Result result) throws Exception {
		if ( ! result.wasSuccessful () ) {
			printer.println ("THERE WHERE FAILURES!");
			printer.println (builder.toString ());
		}
	}

	private void printReport (Result result) {
		String report = String.format (
			"Tests run: %d, Failures: %d, Errors: %d, Skipped: %d, Assumptions: %d",
			result.getRunCount (), result.getFailureCount () - testsInError,
			testsInError, result.getIgnoreCount (), failedAssumptions
		);
		printer.println (report);
	}

}
