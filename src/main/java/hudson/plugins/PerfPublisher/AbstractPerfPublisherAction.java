package hudson.plugins.PerfPublisher;

import hudson.model.Action;
import hudson.model.Run;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

/**
 * Abstract class with functionality common to all PerfPublisher actions.
 *
 * @author Georges Bossert <georges.bossert@caps-entreprise.com>
 */
public class AbstractPerfPublisherAction implements Action {
   public String getIconFileName() {
      return PerfPublisherPlugin.ICON_FILE_NAME;
   }

   public String getDisplayName() {
      return PerfPublisherPlugin.DISPLAY_NAME;
   }

   public String getUrlName() {
      return PerfPublisherPlugin.URL;
   }

   protected boolean shouldReloadGraph(StaplerRequest request, StaplerResponse response, Run build) throws IOException {
      return !request.checkIfModified(build.getTimestamp(), response);
   }
}
