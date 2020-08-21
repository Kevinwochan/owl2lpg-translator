package edu.stanford.owl2lpg.client.read.axiom;

import dagger.Binds;
import dagger.Module;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class RangeAxiomAccessorModule {

  @Binds
  public abstract RangeAxiomAccessor provideRangeAxiomAccessor(RangeAxiomAccessorImpl impl);
}
