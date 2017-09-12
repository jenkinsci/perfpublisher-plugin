package hudson.plugins.PerfPublisher;
import java.io.IOException;
import java.io.PrintStream;

import hudson.model.Descriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;

public abstract class HealthPublisher extends Recorder implements SimpleBuildStep {
}
