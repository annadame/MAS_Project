import genius.core.bidding.BidDetails;
import genius.core.boaframework.*;

import java.util.Map;

public class Group6_AS extends AcceptanceStrategy {

    private double thresholdUtility;

    @Override
    public void init(NegotiationSession negotiationSession, OfferingStrategy offeringStrategy,
                     OpponentModel opponentModel, Map<String, Double> parameters) throws Exception {
        super.init(negotiationSession, offeringStrategy, opponentModel, parameters);
        this.thresholdUtility = 1.0;
    }

    @Override
    public Actions determineAcceptability() {
        BidDetails opponentsBid = negotiationSession.getOpponentBidHistory().getLastBidDetails();
        double timePassed = negotiationSession.getTimeline().getTime();
        if (opponentsBid.getMyUndiscountedUtil() >= thresholdUtility) {
            System.out.println(timePassed);
            return Actions.Accept;
        }

        if (!negotiationSession.getOwnBidHistory().isEmpty()) {
            thresholdUtility = negotiationSession.getOwnBidHistory().getLastBidDetails().getMyUndiscountedUtil() * 0.95;
        }

        return Actions.Reject;
    }

    @Override
    public String getName() {
        return "Group 6 Acceptance Strategy";
    }
}
