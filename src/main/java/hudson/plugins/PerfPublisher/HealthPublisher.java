package hudson.plugins.PerfPublisher;
import java.io.IOException;
import java.io.PrintStream;

import hudson.model.Descriptor;
import hudson.tasks.Publisher;
import hudson.model.AbstractBuild;
import hudson.Launcher;
import hudson.model.BuildListener;

public abstract class HealthPublisher extends Publisher {

	public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
        return true;
    }
}
