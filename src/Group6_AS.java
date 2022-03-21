import genius.core.bidding.BidDetails;
import genius.core.boaframework.*;

import java.util.Map;

public class Group6_AS extends AcceptanceStrategy {

    private Group6_SAS helper;
    private double thresholdUtility;
    private Map<String, Double> parameters;

    @Override
    public void init(NegotiationSession negotiationSession, OfferingStrategy offeringStrategy,
                     OpponentModel opponentModel, Map<String, Double> parameters) throws Exception {
        super.init(negotiationSession, offeringStrategy, opponentModel, parameters);
        thresholdUtility = 1.0; // TODO: test if 1 works better
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

        if (timePassed > 0.85) {
            thresholdUtility = 1 - (Math.pow(1.045, 100 - ((1 - timePassed) * 1000)) / 100);
        }
        else if (timePassed > 0.5) {
            thresholdUtility = 0.8;
        }

        System.out.println("Threshold utility = " + thresholdUtility);
        return Actions.Reject;
    }

    @Override
    public String getName() {
        return "Group 6 Acceptance Strategy";
    }
}
