import genius.core.Bid;
import genius.core.bidding.BidDetails;
import genius.core.boaframework.*;

import java.util.Map;

public class Group6_AS extends AcceptanceStrategy {
    private double thresholdUtility;
    private double concedingFactor;
    private Map<String, Double> parameters;

    @Override
    public void init(NegotiationSession negotiationSession, OfferingStrategy offeringStrategy,
                     OpponentModel opponentModel, Map<String, Double> parameters) throws Exception {
        super.init(negotiationSession, offeringStrategy, opponentModel, parameters);
        thresholdUtility = 1.0;
        concedingFactor = 0.02;
        this.parameters = parameters;
    }

    @Override
    public Actions determineAcceptability() {
        //TODO: change concedingFactor dynamically based on opponentmodel/strategy and time
        BidDetails opponentsBid = negotiationSession.getOpponentBidHistory().getLastBidDetails();
        if (opponentsBid.getMyUndiscountedUtil() >= thresholdUtility) {
            return Actions.Accept;
        }
        thresholdUtility -= concedingFactor;
        return Actions.Reject;
    }

    @Override
    public String getName() {
        return "Group 6 Acceptance Strategy";
    }
}
