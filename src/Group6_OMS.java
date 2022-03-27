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
        double bestOpponentUtility = 0.0;
        BidDetails bestOpponentBid = null;
        for (BidDetails agentBid: bidsInRange) {
            // Get the utility of the opponent for these certain values of issues
            double opponentUtility = model.getBidEvaluation(agentBid.getBid());

            // Save the highest utility for opponent and corresponding bid
            if (opponentUtility > bestOpponentUtility) {
                bestOpponentBid = agentBid;
                bestOpponentUtility = opponentUtility;
            }
        }

        return bestOpponentBid;
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
