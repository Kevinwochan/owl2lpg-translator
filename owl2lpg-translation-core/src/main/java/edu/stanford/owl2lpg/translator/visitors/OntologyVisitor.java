package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import edu.stanford.owl2lpg.model.*;
import edu.stanford.owl2lpg.translator.*;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.*;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ONTOLOGY;

/**
 * A visitor that contains the implementation to translate the OWL 2 ontology.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@TranslationSessionScope
public class OntologyVisitor implements OWLNamedObjectVisitorEx<Translation> {

  @Nonnull
  private final NodeFactory nodeFactory;

  @Nonnull
  private final EdgeFactory edgeFactory;

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Nonnull
  private final EntityTranslator entityTranslator;

  @Nonnull
  private final AnnotationObjectTranslator annotationTranslator;

  @Inject
  public OntologyVisitor(@Nonnull NodeFactory nodeFactory,
                         @Nonnull EdgeFactory edgeFactory,
                         @Nonnull AxiomTranslator axiomTranslator,
                         @Nonnull EntityTranslator entityTranslator,
                         @Nonnull AnnotationObjectTranslator annotationTranslator) {
    this.nodeFactory = checkNotNull(nodeFactory);
    this.edgeFactory = checkNotNull(edgeFactory);
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.entityTranslator = checkNotNull(entityTranslator);
    this.annotationTranslator = checkNotNull(annotationTranslator);
  }

  @Nonnull
  public Translation visit(@Nonnull OWLOntology ontology) {
    var mainNode = nodeFactory.createNode(ontology, ONTOLOGY);
    var translations = new ImmutableList.Builder<Translation>();
    var edges = new ImmutableList.Builder<Edge>();
    var ontologyIdTranslation = createOntologyIdTranslation(ontology.getOntologyID());
    translations.add(ontologyIdTranslation);
    edges.add(edgeFactory.createEdge(mainNode,
        ontologyIdTranslation.getMainNode(),
        ONTOLOGY_IRI));
    var annotations = ontology.getAnnotations();
    for (var ann : annotations) {
      var translation = annotationTranslator.translate(ann);
      translations.add(translation);
      edges.add(edgeFactory.createEdge(mainNode,
          translation.getMainNode(),
          ONTOLOGY_ANNOTATION));
    }
    var axioms = ontology.getAxioms();
    for (var ax : axioms) {
      var translation = axiomTranslator.translate(ax);
      translations.add(translation);
      edges.add(edgeFactory.createEdge(mainNode,
          translation.getMainNode(),
          AXIOM));
    }
    return Translation.create(ontology, mainNode,
        edges.build(),
        translations.build());
  }

  protected Translation createOntologyIdTranslation(OWLOntologyID ontologyID) {
    return Translation.create(
        ontologyID,
        createOntologyIdNode(ontologyID),
        ImmutableList.of(),
        ImmutableList.of());
  }

  private Node createOntologyIdNode(@Nonnull OWLOntologyID ontologyId) {
    var ontologyIri = ontologyId.getOntologyIRI().isPresent() ? ontologyId.getOntologyIRI().toString() : "";
    var versionIri = ontologyId.getVersionIRI().isPresent() ? ontologyId.getVersionIRI().toString() : "";
    return nodeFactory.createNode(
        ontologyId,
        NodeLabels.ONTOLOGY_ID,
        Properties.create(ImmutableMap.of(
            PropertyFields.ONTOLOGY_IRI, ontologyIri,
            PropertyFields.ONTOLOGY_VERSION_IRI, versionIri
        )));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLClass clasz) {
    return entityTranslator.translate(clasz);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectProperty property) {
    return entityTranslator.translate(property);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataProperty property) {
    return entityTranslator.translate(property);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNamedIndividual individual) {
    return entityTranslator.translate(individual);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatype datatype) {
    return entityTranslator.translate(datatype);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationProperty property) {
    return entityTranslator.translate(property);
  }
}
