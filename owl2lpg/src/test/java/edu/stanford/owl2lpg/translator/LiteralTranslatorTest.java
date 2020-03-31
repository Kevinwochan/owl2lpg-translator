package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyNames;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;

import static edu.stanford.owl2lpg.datastructure.GraphFactory.*;
import static edu.stanford.owl2lpg.translator.PropertiesFactory.Properties;
import static edu.stanford.owl2lpg.translator.vocab.PropertyNames.IRI;
import static edu.stanford.owl2lpg.translator.vocab.PropertyNames.LEXICAL_FORM;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Datatype;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.IRI;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Literal;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.PlainLiteral;

public class LiteralTranslatorTest {

  private LiteralTranslator translator;

  @Before
  public void createTranslator() {
    translator = new LiteralTranslator();
  }

  @Test
  public void shouldTranslateDatatype() {
    OWLDatatype dt = Datatype(IRI("http://www.w3.org/2001/XMLSchema#string"));
    Graph actualGraph = dt.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.DATATYPE),
            Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/2001/XMLSchema#string")),
            EdgeLabels.ENTITY_IRI));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslatePlainLiteral() {
    OWLLiteral lt = PlainLiteral("abc");
    Graph actualGraph = lt.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "abc")),
            Graph(
                Edge(
                    Node(NodeLabels.DATATYPE),
                    Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.DATATYPE),
        Edge(
            Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "abc")),
            Node(NodeLabels.LANGUAGE_TAG, Properties(PropertyNames.LANGUAGE, "")),
            EdgeLabels.LANGUAGE_TAG));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslatePlainLiteralWithLanguage() {
    OWLLiteral lt = Literal("abc", "en");
    Graph actualGraph = lt.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "abc")),
            Graph(
                Edge(
                    Node(NodeLabels.DATATYPE),
                    Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.DATATYPE),
        Edge(
            Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "abc")),
            Node(NodeLabels.LANGUAGE_TAG, Properties(PropertyNames.LANGUAGE, "en")),
            EdgeLabels.LANGUAGE_TAG));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateStringTypedLiteral() {
    OWLLiteral lt = Literal("abc");
    Graph actualGraph = lt.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "abc")),
            Graph(
                Edge(
                    Node(NodeLabels.DATATYPE),
                    Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/2001/XMLSchema#string")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.DATATYPE));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateIntegerTypedLiteral() {
    OWLLiteral lt = Literal(22);
    Graph actualGraph = lt.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "22")),
            Graph(
                Edge(
                    Node(NodeLabels.DATATYPE),
                    Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/2001/XMLSchema#integer")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.DATATYPE));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateFloatTypedLiteral() {
    OWLLiteral lt = Literal(22.22f);
    Graph actualGraph = lt.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "22.22")),
            Graph(
                Edge(
                    Node(NodeLabels.DATATYPE),
                    Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/2001/XMLSchema#float")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.DATATYPE));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateDoubleTypedLiteral() {
    OWLLiteral lt = Literal(22.22d);
    Graph actualGraph = lt.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "22.22")),
            Graph(
                Edge(
                    Node(NodeLabels.DATATYPE),
                    Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/2001/XMLSchema#double")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.DATATYPE));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateBooleanTypedLiteral() {
    OWLLiteral lt = Literal(false);
    Graph actualGraph = lt.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, "false")),
            Graph(
                Edge(
                    Node(NodeLabels.DATATYPE),
                    Node(NodeLabels.IRI, Properties(IRI, "http://www.w3.org/2001/XMLSchema#boolean")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.DATATYPE));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateAnyTypedLiteral() {
    OWLLiteral lt = Literal("(650) 123-4567", Datatype(IRI("http://example.org/phoneNumber")));
    Graph actualGraph = lt.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.LITERAL, PropertiesBuilder.create()
                .set(LEXICAL_FORM, "(650) 123-4567").build()),
            Graph(
                Edge(
                    Node(NodeLabels.DATATYPE),
                    Node(NodeLabels.IRI, Properties(IRI, "http://example.org/phoneNumber")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.DATATYPE));
    assertThat(actualGraph, equalTo(expectedGraph));
  }
}