package mas2022.group6;

import genius.core.bidding.BidDetails;
import genius.core.boaframework.*;

import java.util.Map;

public class Group6_AS extends AcceptanceStrategy {

    private double thresholdUtility;
    private double acceptanceFactor;
    private int timesRejected;

    @Override
    public void init(NegotiationSession negotiationSession, OfferingStrategy offeringStrategy,
                     OpponentModel opponentModel, Map<String, Double> parameters) throws Exception {
        super.init(negotiationSession, offeringStrategy, opponentModel, parameters);
        this.thresholdUtility = 1.0;
        this.acceptanceFactor = 0.95;
        this.timesRejected = 0;
    }

    /**
     * The acceptability is determined firstly by checking if the opponent has made an earlier bid that is better for us
     * than the utility we would currently accept, if this is the case, we reject the offer. We expect the
     * Bidding Strategy to find this bid and offer it. Otherwise, we accept any offer that is larger than 95%
     * of the utility of our own last bid
     * @return The Action to undertake
     */
    @Override
    public Actions determineAcceptability() {
        if (!negotiationSession.getOwnBidHistory().isEmpty()) {
            thresholdUtility = negotiationSession.getOwnBidHistory().getLastBidDetails().getMyUndiscountedUtil() * acceptanceFactor;
        }

        // Stop rejecting based on this rule after the acceptance strategy has rejected it twice to
        // ensure compatibility with agents that have the same strategy
        if (negotiationSession.getOpponentBidHistory().getBestBidDetails().getMyUndiscountedUtil() >= thresholdUtility && timesRejected < 2) {
            timesRejected++;
            return Actions.Reject;
        }

        BidDetails opponentsBid = negotiationSession.getOpponentBidHistory().getLastBidDetails();
        if (opponentsBid.getMyUndiscountedUtil() >= thresholdUtility) {
            return Actions.Accept;
        }

        return Actions.Reject;
    }

    @Override
    public String getName() {
        return "Group 6 - Roosevelt - Acceptance Strategy";
    }
}
