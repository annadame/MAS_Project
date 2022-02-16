import genius.core.actions.Action;
import genius.core.parties.AbstractNegotiationParty;

import java.util.List;

public class TestAgent extends AbstractNegotiationParty {

    @Override
    public Action chooseAction(List<Class<? extends Action>> possibleActions) {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
