package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.translator.visitors.VersionedOntologyVisitor;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class VersionedOntologyTranslator {

  @Nonnull
  private final Provider<VersionedOntologyVisitor> visitor;

  @Inject
  public VersionedOntologyTranslator(@Nonnull Provider<VersionedOntologyVisitor> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  public Translation translate(OWLOntology ontology) {
    checkNotNull(ontology);
    return ontology.accept(visitor.get());
  }
}
