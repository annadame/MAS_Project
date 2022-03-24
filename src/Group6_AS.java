import genius.core.bidding.BidDetails;
import genius.core.boaframework.*;

import java.util.Map;

public class Group6_AS extends AcceptanceStrategy {

    private Group6_SAS helper;
    private double thresholdUtility;
    private Map<String, Double> parameters;
    private double exponent = 5.0D;

    @Override
    public void init(NegotiationSession negotiationSession, OfferingStrategy offeringStrategy,
                     OpponentModel opponentModel, Map<String, Double> parameters) throws Exception {
        super.init(negotiationSession, offeringStrategy, opponentModel, parameters);
        thresholdUtility = 1.0;
        this.parameters = parameters;
        this.helper = Group6_SAS.getInstance(negotiationSession, parameters);
    }

    @Override
    public Actions determineAcceptability() {
        BidDetails opponentsBid = negotiationSession.getOpponentBidHistory().getLastBidDetails();
        double timePassed = negotiationSession.getTimeline().getTime();
        if (opponentsBid.getMyUndiscountedUtil() >= thresholdUtility) {
            System.out.println(timePassed);
            return Actions.Accept;
        }

        // TODO: Look into rejecting if opponent is being conceding (based on time or other variables)
        if (!negotiationSession.getOwnBidHistory().isEmpty()) {
            thresholdUtility = negotiationSession.getOwnBidHistory().getLastBidDetails().getMyUndiscountedUtil() * 0.95;
        }

        System.out.println("Threshold utility = " + thresholdUtility);
        return Actions.Reject;
    }

    @Override
    public String getName() {
        return "Group 6 Acceptance Strategy";
    }
}
