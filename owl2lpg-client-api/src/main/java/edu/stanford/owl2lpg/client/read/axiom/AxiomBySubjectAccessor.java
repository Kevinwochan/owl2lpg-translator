package edu.stanford.owl2lpg.client.read.axiom;

import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AxiomBySubjectAccessor {

  @Nonnull
  Set<OWLAxiom> getAxiomsForSubject(@Nonnull OWLClass subject,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  Set<OWLAxiom> getAxiomsForSubject(@Nonnull OWLNamedIndividual subject,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  Set<OWLAxiom> getAxiomsForSubject(@Nonnull OWLEntity entity,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  Set<OWLAxiom> getAxiomsForSubjects(@Nonnull Collection<OWLEntity> entities,
                                     @Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull OntologyDocumentId ontoDocId);
}
