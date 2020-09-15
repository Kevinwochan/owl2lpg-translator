package edu.stanford.owl2lpg.client.read.axiom.impl;

import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.HierarchyAxiomBySubjectAccessor;
import edu.stanford.owl2lpg.client.read.impl.NodeIndexImpl;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.AXIOM;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class HierarchyAxiomBySubjectAccessorImpl implements HierarchyAxiomBySubjectAccessor {

  private static final String SUB_CLASS_OF_AXIOMS_BY_SUB_CLASS_QUERY_FILE =
      "axioms/sub-class-of-axiom-by-sub-class.cpy";
  private static final String SUB_OBJECT_PROPERTY_OF_AXIOMS_BY_SUB_PROPERTY_QUERY_FILE =
      "axioms/sub-object-property-of-axiom-by-sub-property.cpy";
  private static final String SUB_DATA_PROPERTY_OF_AXIOMS_BY_SUB_PROPERTY_QUERY_FILE =
      "axioms/sub-data-property-of-axiom-by-sub-property.cpy";

  private static final String SUB_CLASS_OF_AXIOMS_BY_SUB_CLASS_QUERY =
      read(SUB_CLASS_OF_AXIOMS_BY_SUB_CLASS_QUERY_FILE);
  private static final String SUB_OBJECT_PROPERTY_OF_AXIOMS_BY_SUB_PROPERTY_QUERY =
      read(SUB_OBJECT_PROPERTY_OF_AXIOMS_BY_SUB_PROPERTY_QUERY_FILE);
  private static final String SUB_DATA_PROPERTY_OF_AXIOMS_BY_SUB_PROPERTY_QUERY =
      read(SUB_DATA_PROPERTY_OF_AXIOMS_BY_SUB_PROPERTY_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public HierarchyAxiomBySubjectAccessorImpl(@Nonnull Driver driver,
                                             @Nonnull NodeMapper nodeMapper) {
    this.driver = checkNotNull(driver);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Nonnull
  @Override
  public Set<OWLSubClassOfAxiom>
  getSubClassOfAxiomsBySubClass(@Nonnull OWLClass subClass,
                                @Nonnull ProjectId projectId,
                                @Nonnull BranchId branchId,
                                @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = createInputParams(subClass, projectId, branchId, ontoDocId);
    var nodeIndex = getNodeIndex(SUB_CLASS_OF_AXIOMS_BY_SUB_CLASS_QUERY, inputParams);
    return collectSubClassOfAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  @Override
  public Set<OWLSubObjectPropertyOfAxiom>
  getSubObjectPropertyOfAxiomsBySubProperty(@Nonnull OWLObjectProperty subProperty,
                                            @Nonnull ProjectId projectId,
                                            @Nonnull BranchId branchId,
                                            @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = createInputParams(subProperty, projectId, branchId, ontoDocId);
    var nodeIndex = getNodeIndex(SUB_OBJECT_PROPERTY_OF_AXIOMS_BY_SUB_PROPERTY_QUERY, inputParams);
    return collectSubObjectPropertyOfAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  @Override
  public Set<OWLSubDataPropertyOfAxiom>
  getSubDataPropertyOfAxiomsBySubProperty(@Nonnull OWLDataProperty subProperty,
                                          @Nonnull ProjectId projectId,
                                          @Nonnull BranchId branchId,
                                          @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = createInputParams(subProperty, projectId, branchId, ontoDocId);
    var nodeIndex = getNodeIndex(SUB_DATA_PROPERTY_OF_AXIOMS_BY_SUB_PROPERTY_QUERY, inputParams);
    return collectSubDataPropertyOfAxiomsFromIndex(nodeIndex);
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
  private Set<OWLSubClassOfAxiom> collectSubClassOfAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(AXIOM.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLSubClassOfAxiom.class))
        .collect(Collectors.toSet());
  }

  @Nonnull
  private Set<OWLSubObjectPropertyOfAxiom> collectSubObjectPropertyOfAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(AXIOM.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLSubObjectPropertyOfAxiom.class))
        .collect(Collectors.toSet());
  }

  @Nonnull
  private Set<OWLSubDataPropertyOfAxiom> collectSubDataPropertyOfAxiomsFromIndex(NodeIndex nodeIndex) {
    return nodeIndex.getNodes(AXIOM.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLSubDataPropertyOfAxiom.class))
        .collect(Collectors.toSet());
  }

  @Nonnull
  private static Value createInputParams(OWLEntity entity, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return Parameters.forEntityIri(entity.getIRI(), projectId, branchId, ontoDocId);
  }
}
