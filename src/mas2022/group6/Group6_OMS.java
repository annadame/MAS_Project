package mas2022.group6;

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

    /**
     * Gets bid in a range that we think is best for the opponent
     * @param bidsInRange range for bids
     * @return BidDetails of the best bid
     */
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
        // Model is always updated
        return true;
    }

    @Override
    public String getName() {
        return "Group 6 - Roosevelt - Opponent Model Strategy";
    }

}
