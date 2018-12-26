package hudson.plugins.PerfPublisher.Report;

public class Success {

	private boolean passed;
	private float state;
	private boolean unstable;

	public Success() {
		passed = false;
		state = 0;
		unstable = false;
	}

	/**
	 * @return the passed
	 */
	public boolean isPassed() {
		return passed;
	}

	/**
	 * @param passed the passed to set
	 */
	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	/**
	 * @return the state
	 */
	public float getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(float state) {
		this.state = state;
	}

    public boolean isUnstable() {
        return unstable;
    }

    public void setUnstable(boolean unstable) {
        this.unstable = unstable;
    }
}
