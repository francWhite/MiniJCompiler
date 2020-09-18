package ch.hslu.cobau.test.model;

public class TestResult {
    private final TestCase testCase;
    private final String actualOutput;
    private final TestStatus testStatus;
    private final long timeActual;

    public TestResult(TestCase testCase, String actualOutput, TestStatus testStatus, long timeActual) {
        this.testCase     = testCase;
        this.actualOutput = actualOutput;
        this.testStatus   = testStatus;
        this.timeActual   = timeActual;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public TestStatus getStatus() {
        return testStatus;
    }

    public String getActualOutput() {
        return actualOutput;
    }

    public long getActualExecutionTime() {
        return timeActual;
    }
}
