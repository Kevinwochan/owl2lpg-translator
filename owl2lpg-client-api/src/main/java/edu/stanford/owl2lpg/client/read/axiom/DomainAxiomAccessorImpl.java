package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.Parameters;
import org.neo4j.driver.Driver;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_PROPERTY_DOMAIN;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DomainAxiomAccessorImpl implements DomainAxiomAccessor {

  private static final String OBJECT_PROPERTY_DOMAIN_AXIOM_QUERY_FILE = "axioms/object-property-domain-axiom.cpy";

  private static final String OBJECT_PROPERTY_DOMAIN_AXIOM_QUERY = read(OBJECT_PROPERTY_DOMAIN_AXIOM_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public DomainAxiomAccessorImpl(@Nonnull Driver driver,
                                 @Nonnull NodeMapper nodeMapper) {
    this.driver = checkNotNull(driver);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Nonnull
  @Override
  public Set<OWLObjectPropertyDomainAxiom> getObjectPropertyDomainAxioms(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    var nodeIndex = getNodeIndex(context, owlObjectProperty, OBJECT_PROPERTY_DOMAIN_AXIOM_QUERY);
    return collectObjectPropertyDomainAxiomsFromIndex(nodeIndex);
  }

  private Set<OWLObjectPropertyDomainAxiom> collectObjectPropertyDomainAxiomsFromIndex(NodeIndex nodeIndex) {
    return nodeIndex.getNodes(OBJECT_PROPERTY_DOMAIN.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLObjectPropertyDomainAxiom.class))
        .collect(Collectors.toSet());
  }

  @Nonnull
  @Override
  public Set<OWLDataPropertyDomainAxiom> getDataPropertyDomainAxioms(OWLDataProperty owlDataProperty, AxiomContext context) {
    return ImmutableSet.of();
  }

  @Nonnull
  @Override
  public Set<OWLAnnotationPropertyDomainAxiom> getAnnotationPropertyDomainAxioms(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context) {
    return ImmutableSet.of();
  }

  private NodeIndex getNodeIndex(AxiomContext context, OWLEntity entity, String queryString) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var args = Parameters.forEntity(context, entity);
        var result = tx.run(queryString, args);
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
}
