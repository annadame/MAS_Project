import genius.core.Bid;
import genius.core.bidding.BidDetails;
import genius.core.boaframework.*;

import java.util.Map;

public class Group6_AS extends AcceptanceStrategy {

    private Group6_SAS helper;
    private double thresholdUtility;
    private double concedingFactor;
    private Map<String, Double> parameters;

    @Override
    public void init(NegotiationSession negotiationSession, OfferingStrategy offeringStrategy,
                     OpponentModel opponentModel, Map<String, Double> parameters) throws Exception {
        super.init(negotiationSession, offeringStrategy, opponentModel, parameters);
        thresholdUtility = 1.0;
        concedingFactor = 0.08;
        this.parameters = parameters;
        this.helper = Group6_SAS.getInstance(negotiationSession, parameters);

    }

    @Override
    public Actions determineAcceptability() {
        double timePassed = negotiationSession.getTimeline().getTime();
        //TODO: change concedingFactor dynamically based on opponentmodel/strategy and time
        try {
            if (helper.getOpponentConcessionFactor().get1() > 0.01 /*TODO also check when opponent concessionfactor is reliable enough to use*/) {
                thresholdUtility -= concedingFactor * timePassed;
            }
        } catch (Exception e) {

        }

        if (timePassed > 0.95) {
            thresholdUtility = 0.5;
        } else if (timePassed > 0.9) {
            thresholdUtility = 0.7;
        }

        System.out.println("thresholdutility: " + thresholdUtility);

        BidDetails opponentsBid = negotiationSession.getOpponentBidHistory().getLastBidDetails();
        if (opponentsBid.getMyUndiscountedUtil() >= thresholdUtility) {
            return Actions.Accept;
        }
        return Actions.Reject;
    }

    @Override
    public String getName() {
        return "Group 6 Acceptance Strategy";
    }
}
