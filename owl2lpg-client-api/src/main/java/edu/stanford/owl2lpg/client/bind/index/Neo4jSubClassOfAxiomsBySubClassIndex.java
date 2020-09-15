package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.SubClassOfAxiomsBySubClassIndex;
import edu.stanford.owl2lpg.client.read.axiom.HierarchyAxiomBySubjectAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jSubClassOfAxiomsBySubClassIndex implements SubClassOfAxiomsBySubClassIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final HierarchyAxiomBySubjectAccessor hierarchyAxiomBySubjectAccessor;

  @Inject
  public Neo4jSubClassOfAxiomsBySubClassIndex(@Nonnull ProjectId projectId,
                                              @Nonnull BranchId branchId,
                                              @Nonnull OntologyDocumentId ontoDocId,
                                              @Nonnull HierarchyAxiomBySubjectAccessor hierarchyAxiomBySubjectAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.hierarchyAxiomBySubjectAccessor = checkNotNull(hierarchyAxiomBySubjectAccessor);
  }

  @Override
  public Stream<OWLSubClassOfAxiom> getSubClassOfAxiomsForSubClass(@Nonnull OWLClass subClass,
                                                                   @Nonnull OWLOntologyID owlOntologyID) {
    return hierarchyAxiomBySubjectAccessor
        .getSubClassOfAxiomsBySubClass(subClass, projectId, branchId, ontoDocId)
        .stream();
  }
}
