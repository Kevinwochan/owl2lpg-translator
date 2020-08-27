package edu.stanford.owl2lpg.client.read.axiom.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomBySubjectAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.NodeID;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION_ASSERTION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.CLASS_ASSERTION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_PROPERTY_ASSERTION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_PROPERTY_ASSERTION;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AssertionAxiomBySubjectAccessorImpl implements AssertionAxiomBySubjectAccessor {

  private static final String CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE =
      "axioms/class-assertion-axiom-by-individual.cpy";
  private static final String CLASS_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE =
      "axioms/class-assertion-axiom-by-anonymous-individual.cpy";
  private static final String OBJECT_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE =
      "axioms/object-property-assertion-axiom-by-individual.cpy";
  private static final String OBJECT_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE =
      "axioms/object-property-assertion-axiom-by-anonymous-individual.cpy";
  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE =
      "axioms/data-property-assertion-axiom-by-individual.cpy";
  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE =
      "axioms/data-property-assertion-axiom-by-anonymous-individual.cpy";
  private static final String ANNOTATION_ASSERTION_AXIOM_BY_IRI_QUERY_FILE =
      "axioms/annotation-assertion-axiom-by-iri.cpy";
  private static final String ANNOTATION_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE =
      "axioms/annotation-assertion-axiom-by-anonymous-individual.cpy";

  private static final String CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY =
      read(CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE);
  private static final String CLASS_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY =
      read(CLASS_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE);
  private static final String OBJECT_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY =
      read(OBJECT_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE);
  private static final String OBJECT_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY =
      read(OBJECT_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE);
  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY =
      read(DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE);
  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY =
      read(DATA_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE);
  private static final String ANNOTATION_ASSERTION_AXIOM_BY_IRI_QUERY =
      read(ANNOTATION_ASSERTION_AXIOM_BY_IRI_QUERY_FILE);
  private static final String ANNOTATION_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY =
      read(ANNOTATION_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public AssertionAxiomBySubjectAccessorImpl(@Nonnull Driver driver,
                                             @Nonnull NodeMapper nodeMapper) {
    this.driver = checkNotNull(driver);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Nonnull
  @Override
  public Set<OWLClassAssertionAxiom> getClassAssertionsForSubject(OWLIndividual owlIndividual,
                                                                  AxiomContext context) {
    var nodeIndex = (owlIndividual.isNamed()) ?
        getNodeIndex(CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLNamedIndividual().getIRI(), context)) :
        getNodeIndex(CLASS_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLAnonymousIndividual().getID(), context));
    return collectClassAssertionAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  @Override
  public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyAssertionsForSubject(OWLIndividual owlIndividual,
                                                                                    AxiomContext context) {
    var nodeIndex = (owlIndividual.isNamed()) ?
        getNodeIndex(OBJECT_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLNamedIndividual().getIRI(), context)) :
        getNodeIndex(OBJECT_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLAnonymousIndividual().getID(), context));
    return collectObjectPropertyAssertionAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  @Override
  public Set<OWLDataPropertyAssertionAxiom> getDataPropertyAssertionsForSubject(OWLIndividual owlIndividual,
                                                                                AxiomContext context) {
    var nodeIndex = (owlIndividual.isNamed()) ?
        getNodeIndex(DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLNamedIndividual().getIRI(), context)) :
        getNodeIndex(DATA_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLAnonymousIndividual().getID(), context));
    return collectDataPropertyAssertionAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  @Override
  public Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionsForSubject(OWLAnnotationSubject owlAnnotationSubject,
                                                                            AxiomContext context) {
    var nodeIndex = (owlAnnotationSubject.isIRI()) ?
        getNodeIndex(ANNOTATION_ASSERTION_AXIOM_BY_IRI_QUERY,
            createInputParams((IRI) owlAnnotationSubject, context)) :
        getNodeIndex(ANNOTATION_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY,
            createInputParams(((OWLAnonymousIndividual) owlAnnotationSubject).getID(), context));
    return collectAnnotationAssertionAxiomsFromIndex(nodeIndex);
  }

  private NodeIndex getNodeIndex(String queryString, Value inputParams) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var result = tx.run(queryString, inputParams);
        var nodeIndexBuilder = new NodeIndexImpl.Builder();
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("p")) {
              var path = (Path) column.getValue();
              if (path != null) {
                path.spliterator().forEachRemaining(nodeIndexBuilder::add);
              }
            }
          }
        }
        return nodeIndexBuilder.build();
      });
    }
  }

  @Nonnull
  private Set<OWLClassAssertionAxiom> collectClassAssertionAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(CLASS_ASSERTION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLClassAssertionAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private Set<OWLObjectPropertyAssertionAxiom> collectObjectPropertyAssertionAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(OBJECT_PROPERTY_ASSERTION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLObjectPropertyAssertionAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private Set<OWLDataPropertyAssertionAxiom> collectDataPropertyAssertionAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(DATA_PROPERTY_ASSERTION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLDataPropertyAssertionAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private Set<OWLAnnotationAssertionAxiom> collectAnnotationAssertionAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(ANNOTATION_ASSERTION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAnnotationAssertionAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private static Value createInputParams(IRI entityIri, AxiomContext context) {
    return Parameters.forEntityIri(entityIri, context.getProjectId(), context.getBranchId(), context.getOntologyDocumentId());
  }

  @Nonnull
  private static Value createInputParams(NodeID nodeId, AxiomContext context) {
    return Parameters.forNodeId(nodeId, context.getProjectId(), context.getBranchId(), context.getOntologyDocumentId());
  }
}
