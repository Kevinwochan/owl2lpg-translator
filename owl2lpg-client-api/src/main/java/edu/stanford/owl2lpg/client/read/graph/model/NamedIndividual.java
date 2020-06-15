package edu.stanford.owl2lpg.client.read.graph.model;

import com.google.common.base.MoreObjects;
import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "NamedIndividual")
public class NamedIndividual extends Individual<OWLNamedIndividual>
    implements Entity<OWLNamedIndividual> {

  @Property
  @Required
  @Index
  private String iri;

  @Relationship(type = "ENTITY_IRI")
  private Iri entityIri;

  private NamedIndividual() {
  }

  public NamedIndividual(@Nonnull String iri,
                         @Nonnull Iri entityIri) {
    this.iri = checkNotNull(iri);
    this.entityIri = checkNotNull(entityIri);
  }

  @Nullable
  @Override
  public String getIri() {
    return iri;
  }

  @Nullable
  @Override
  public Iri getEntityIri() {
    return entityIri;
  }

  @Override
  public OWLNamedIndividual toOwlObject(OWLDataFactory dataFactory, Session session) {
    if (entityIri == null) {
      var nodeEntity = reloadThisNodeEntity(session);
      return nodeEntity.toOwlObject(dataFactory, session);
    } else {
      return dataFactory.getOWLNamedIndividual(entityIri.toOwlObject(dataFactory, session));
    }
  }

  private NamedIndividual reloadThisNodeEntity(Session session) {
    return session.load(getClass(), getId(), 1);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", getId())
        .add("iri", iri)
        .add("entityIri", entityIri)
        .toString();
  }
}
