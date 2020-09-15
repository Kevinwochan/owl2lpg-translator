package edu.stanford.owl2lpg.client.read.individual.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.hierarchy.ClassHierarchyRoot;
import edu.stanford.owl2lpg.client.read.axiom.ClassAssertionAxiomAccessor;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessor;
import edu.stanford.owl2lpg.client.read.individual.NamedIndividualAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.DataFactory.getOWLThing;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NamedIndividualAccessorImpl implements NamedIndividualAccessor {

  @Nonnull
  private final OWLClass root;

  @Nonnull
  private final EntityAccessor entityAccessor;

  @Nonnull
  private final ClassAssertionAxiomAccessor classAssertionAxiomAccessor;

  @Inject
  public NamedIndividualAccessorImpl(@Nonnull @ClassHierarchyRoot OWLClass root,
                                     @Nonnull EntityAccessor entityAccessor,
                                     @Nonnull ClassAssertionAxiomAccessor classAssertionAxiomAccessor) {
    this.root = checkNotNull(root);
    this.entityAccessor = checkNotNull(entityAccessor);
    this.classAssertionAxiomAccessor = checkNotNull(classAssertionAxiomAccessor);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLNamedIndividual> getAllIndividuals(@Nonnull ProjectId projectId,
                                                            @Nonnull BranchId branchId,
                                                            @Nonnull OntologyDocumentId ontoDocId) {
    return entityAccessor.getEntitiesByType(EntityType.NAMED_INDIVIDUAL, projectId, branchId, ontoDocId)
        .stream()
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLNamedIndividual> getIndividualsByType(@Nonnull OWLClass owlClass,
                                                               @Nonnull ProjectId projectId,
                                                               @Nonnull BranchId branchId,
                                                               @Nonnull OntologyDocumentId ontoDocId) {
    if (root.equals(getOWLThing()) && root.equals(owlClass)) {
      return getAllIndividuals(projectId, branchId, ontoDocId);
    } else {
      return classAssertionAxiomAccessor.getClassAssertions(owlClass, projectId, branchId, ontoDocId)
          .stream()
          .map(OWLClassAssertionAxiom::getIndividual)
          .filter(OWLIndividual::isNamed)
          .map(OWLIndividual::asOWLNamedIndividual)
          .collect(ImmutableSet.toImmutableSet());
    }
  }
}
