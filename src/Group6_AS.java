import genius.core.bidding.BidDetails;
import genius.core.boaframework.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Group6_AS extends AcceptanceStrategy {

    private double thresholdUtility;
    private double averageRoundTime;
    private double acceptanceFactor;
    private boolean doRoundTimeMeasure;
    private List<Double> timePassedList;

    @Override
    public void init(NegotiationSession negotiationSession, OfferingStrategy offeringStrategy,
                     OpponentModel opponentModel, Map<String, Double> parameters) throws Exception {
        super.init(negotiationSession, offeringStrategy, opponentModel, parameters);
        this.thresholdUtility = 1.0;
        this.averageRoundTime = 0.0;
        this.acceptanceFactor = 0.95;
        this.doRoundTimeMeasure = true;
        this.timePassedList = new ArrayList<>();
    }

    @Override
    public Actions determineAcceptability() {
        if (negotiationSession.getOpponentBidHistory().getBestBidDetails().getMyUndiscountedUtil() > thresholdUtility) {
            return Actions.Reject;
        }

        BidDetails opponentsBid = negotiationSession.getOpponentBidHistory().getLastBidDetails();
        if (opponentsBid.getMyUndiscountedUtil() >= thresholdUtility) {
            return Actions.Accept;
        }

        if (!negotiationSession.getOwnBidHistory().isEmpty()) {
            thresholdUtility = negotiationSession.getOwnBidHistory().getLastBidDetails().getMyUndiscountedUtil() * acceptanceFactor;
        }

        return Actions.Reject;
    }

    @Override
    public String getName() {
        return "Group 6 Acceptance Strategy";
    }
}
