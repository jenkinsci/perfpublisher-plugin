package hudson.plugins.PerfPublisher;

/**
 * Exception used to show PerfPublisher parsing has failed.
 *
 * @author Georges Bossert
 */
public class PerfPublisherParseException extends RuntimeException {
   public PerfPublisherParseException(String msg, Exception e) {
      super(msg, e);
   }

   public PerfPublisherParseException(String msg) {
      super(msg);
   }
}
