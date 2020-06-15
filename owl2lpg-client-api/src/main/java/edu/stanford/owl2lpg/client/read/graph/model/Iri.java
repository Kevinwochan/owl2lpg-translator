package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Required;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "IRI")
public class Iri extends GraphObject implements HasToOwlObject<IRI> {

  @Property
  @Required
  @Index
  private String iri;

  private Iri() {
  }

  public Iri(@Nonnull String iri) {
    this.iri = checkNotNull(iri);
  }

  @Nullable
  public String getIri() {
    return iri;
  }

  @Override
  public IRI toOwlObject(OWLDataFactory dataFactory, Session session) {
    if (iri == null) {
      var nodeEntity = reloadThisNodeEntity(session);
      return nodeEntity.toOwlObject(dataFactory, session);
    } else {
      return IRI.create(iri);
    }
  }

  private Iri reloadThisNodeEntity(Session session) {
    return session.load(getClass(), getId(), 0);
  }
}
