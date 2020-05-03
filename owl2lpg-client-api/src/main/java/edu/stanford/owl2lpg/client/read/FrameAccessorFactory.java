package edu.stanford.owl2lpg.client.read;

import edu.stanford.owl2lpg.client.Database;
import org.neo4j.driver.Session;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface FrameAccessorFactory<T> {

  FrameAccessor<T> initialize(Database database, Session session);

  boolean isAccessorFor(Class<T> frameClass);
}
