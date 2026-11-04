
package hudson.plugins.PerfPublisher;

import java.io.Serializable;

public class HealthDescriptor implements Serializable {

    private static final long serialVersionUID = 1L;

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

    public int getMinHealth() {
        return minHealth;
    }

    public void setMinHealth(int minHealth) {
        this.minHealth = minHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public boolean isHealthAnalyse() {
        return HealthAnalyse;
    }

    public void setHealthAnalyse(boolean healthAnalyse) {
        HealthAnalyse = healthAnalyse;
    }

    public int getUnstableFailedHealth() {
        return unstableFailedHealth;
    }

    public void setUnstableFailedHealth(int unstableFailedHealth) {
        this.unstableFailedHealth = unstableFailedHealth;
    }

    public int getUnstableUnstableHealth() {
        return unstableUnstableHealth;
    }

    public void setUnstableUnstableHealth(int unstableUnstableHealth) {
        this.unstableUnstableHealth = unstableUnstableHealth;
    }
}
