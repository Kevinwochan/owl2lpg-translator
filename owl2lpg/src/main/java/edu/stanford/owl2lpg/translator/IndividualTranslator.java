package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.datastructure.AnyNode;
import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.datastructure.Node;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyNames;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;

import static edu.stanford.owl2lpg.datastructure.GraphFactory.Node;

/**
 * The translator sub-module for translating the OWL 2 individual to labelled
 * property graphs.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class IndividualTranslator implements OWLIndividualVisitorEx<AnyNode> {

  @Override
  public Graph visit(@Nonnull OWLNamedIndividual individual) {
    return individual.accept(new EntityTranslator());
  }

  @Override
  public Node visit(@Nonnull OWLAnonymousIndividual individual) {
    return Node(NodeLabels.ANONYMOUS_INDIVIDUAL,
        PropertiesBuilder.create()
            .set(PropertyNames.NODE_ID, individual.getID())
            .build());
  }
}
