import genius.core.bidding.BidDetails;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.OMStrategy;
import genius.core.boaframework.OpponentModel;

import java.util.List;
import java.util.Map;

public class Group6_OMS extends OMStrategy {

    @Override
    public void init(NegotiationSession negotiationSession, OpponentModel model, Map<String, Double> parameters) {
        super.init(negotiationSession, model, parameters);
    }

    @Override
    public BidDetails getBid(List<BidDetails> bidsInRange) {
        // TODO: Place part of bidding strategy in here that determines the bid from a range of bids
        return null;
    }

    @Override
    public boolean canUpdateOM() {
        // Possibly add maximum amount of bids as parameter (100 in CUHK OM, maybe have it depend on time passed in negotiation)
        return true;
    }

    @Override
    public String getName() {
        return "Group 6 Opponent Model Strategy";
    }

}
