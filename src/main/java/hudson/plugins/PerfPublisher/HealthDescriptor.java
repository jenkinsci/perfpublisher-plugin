package hudson.plugins.PerfPublisher;

public class HealthDescriptor {

	private int minHealth;
	private int maxHealth;
	
	private boolean HealthAnalyse;
	
	private int unstableFailedHealth;
	private int unstableUnstableHealth;

	public HealthDescriptor() {
		this.maxHealth = 0;
		this.minHealth = 0;
		this.HealthAnalyse = false;
		this.unstableFailedHealth = -1;
		this.unstableUnstableHealth = -1;
	}

	/**
	 * @return the minHealth
	 */
	public int getMinHealth() {
		return minHealth;
	}

	/**
	 * @param minHealth the minHealth to set
	 */
	public void setMinHealth(int minHealth) {
		this.minHealth = minHealth;
	}

	/**
	 * @return the maxHealth
	 */
	public int getMaxHealth() {
		return maxHealth;
	}

	/**
	 * @param maxHealth the maxHealth to set
	 */
	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	/**
	 * @return the healthAnalyse
	 */
	public boolean isHealthAnalyse() {
		return HealthAnalyse;
	}

	/**
	 * @param healthAnalyse the healthAnalyse to set
	 */
	public void setHealthAnalyse(boolean healthAnalyse) {
		HealthAnalyse = healthAnalyse;
	}

	/**
	 * @return the unstableHealth
	 */
	public int getUnstableFailedHealth() {
		return unstableFailedHealth;
	}

	/**
	 * @param unstableHealth the unstableHealth to set
	 */
	public void setUnstableFailedHealth(int unstableHealth) {
		this.unstableFailedHealth = unstableHealth;
	}

	public int getUnstableUnstableHealth() {
		return unstableUnstableHealth;
	}

	public void setUnstableUnstableHealth(int unstableUnstableHealth) {
		this.unstableUnstableHealth = unstableUnstableHealth;
	}
}