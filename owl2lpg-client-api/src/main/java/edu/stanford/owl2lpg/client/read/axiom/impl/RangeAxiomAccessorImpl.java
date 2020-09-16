package edu.stanford.owl2lpg.client.read.axiom.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.GraphReader;
import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.RangeAxiomAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Value;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION_PROPERTY_RANGE;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_PROPERTY_RANGE;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_PROPERTY_RANGE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class RangeAxiomAccessorImpl implements RangeAxiomAccessor {

  private static final String OBJECT_PROPERTY_RANGE_AXIOM_QUERY_FILE = "axioms/object-property-range-axiom.cpy";
  private static final String DATA_PROPERTY_RANGE_AXIOM_QUERY_FILE = "axioms/data-property-range-axiom.cpy";
  private static final String ANNOTATION_PROPERTY_RANGE_AXIOM_QUERY_FILE = "axioms/annotation-property-range-axiom.cpy";

  private static final String OBJECT_PROPERTY_RANGE_AXIOM_QUERY = read(OBJECT_PROPERTY_RANGE_AXIOM_QUERY_FILE);
  private static final String DATA_PROPERTY_RANGE_AXIOM_QUERY = read(DATA_PROPERTY_RANGE_AXIOM_QUERY_FILE);
  private static final String ANNOTATION_PROPERTY_RANGE_AXIOM_QUERY = read(ANNOTATION_PROPERTY_RANGE_AXIOM_QUERY_FILE);

  @Nonnull
  private final GraphReader graphReader;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public RangeAxiomAccessorImpl(@Nonnull GraphReader graphReader,
                                @Nonnull NodeMapper nodeMapper) {
    this.graphReader = checkNotNull(graphReader);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLObjectPropertyRangeAxiom>
  getObjectPropertyRangeAxioms(@Nonnull OWLObjectProperty owlObjectProperty,
                               @Nonnull ProjectId projectId,
                               @Nonnull BranchId branchId,
                               @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = createInputParams(owlObjectProperty, projectId, branchId, ontoDocId);
    var nodeIndex = graphReader.getNodeIndex(OBJECT_PROPERTY_RANGE_AXIOM_QUERY, inputParams);
    return collectObjectPropertyRangeAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private ImmutableSet<OWLObjectPropertyRangeAxiom> collectObjectPropertyRangeAxiomsFromIndex(NodeIndex nodeIndex) {
    return nodeIndex.getNodes(OBJECT_PROPERTY_RANGE.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLObjectPropertyRangeAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLDataPropertyRangeAxiom>
  getDataPropertyRangeAxioms(@Nonnull OWLDataProperty owlDataProperty,
                             @Nonnull ProjectId projectId,
                             @Nonnull BranchId branchId,
                             @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = createInputParams(owlDataProperty, projectId, branchId, ontoDocId);
    var nodeIndex = graphReader.getNodeIndex(DATA_PROPERTY_RANGE_AXIOM_QUERY, inputParams);
    return collectDataPropertyRangeAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private ImmutableSet<OWLDataPropertyRangeAxiom> collectDataPropertyRangeAxiomsFromIndex(NodeIndex nodeIndex) {
    return nodeIndex.getNodes(DATA_PROPERTY_RANGE.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLDataPropertyRangeAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAnnotationPropertyRangeAxiom>
  getAnnotationPropertyRangeAxioms(@Nonnull OWLAnnotationProperty owlAnnotationProperty,
                                   @Nonnull ProjectId projectId,
                                   @Nonnull BranchId branchId,
                                   @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = createInputParams(owlAnnotationProperty, projectId, branchId, ontoDocId);
    var nodeIndex = graphReader.getNodeIndex(ANNOTATION_PROPERTY_RANGE_AXIOM_QUERY, inputParams);
    return collectAnnotationPropertyRangeAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private ImmutableSet<OWLAnnotationPropertyRangeAxiom> collectAnnotationPropertyRangeAxiomsFromIndex(NodeIndex nodeIndex) {
    return nodeIndex.getNodes(ANNOTATION_PROPERTY_RANGE.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAnnotationPropertyRangeAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private static Value createInputParams(OWLEntity entity, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return Parameters.forEntityIri(entity.getIRI(), projectId, branchId, ontoDocId);
  }
}
