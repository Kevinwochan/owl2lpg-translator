package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;

import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "ObjectAllValuesFrom")
public class ObjectAllValuesFrom extends ClassExpression<OWLObjectAllValuesFrom> {

  @Relationship(type = "OBJECT_PROPERTY_EXPRESSION")
  private ObjectPropertyExpression property;

  @Relationship(type = "CLASS_EXPRESSION")
  private ClassExpression filler;

  private ObjectAllValuesFrom() {
  }

  @Nullable
  public ObjectPropertyExpression getProperty() {
    return property;
  }

  @Nullable
  public ClassExpression getFiller() {
    return filler;
  }

  @Override
  public OWLObjectAllValuesFrom toOwlObject(OWLDataFactory dataFactory, Session session) {
    if (property == null || filler == null) {
      var nodeEntity = reloadThisNodeEntity(session);
      return nodeEntity.toOwlObject(dataFactory, session);
    } else {
      return dataFactory.getOWLObjectAllValuesFrom(
          property.toOwlObject(dataFactory, session),
          filler.toOwlObject(dataFactory, session));
    }
  }

  private ObjectAllValuesFrom reloadThisNodeEntity(Session session) {
    return session.load(getClass(), getId(), 1);
  }
}
