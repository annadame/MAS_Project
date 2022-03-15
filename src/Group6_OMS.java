import genius.core.bidding.BidDetails;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.OMStrategy;
import genius.core.boaframework.OpponentModel;
import genius.core.boaframework.SharedAgentState;

import java.util.List;
import java.util.Map;

public class Group6_OMS extends OMStrategy {

    @Override
    public void init(NegotiationSession negotiationSession, OpponentModel model, Map<String, Double> parameters) {

    }

    @Override
    public BidDetails getBid(List<BidDetails> bidsInRange) {
        return null;
    }

    @Override
    public boolean canUpdateOM() {
        return false;
    }

    @Override
    public String getName() {
        return "Group 6 Opponent Model Strategy";
    }
}
