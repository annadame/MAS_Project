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
        BidDetails opponentsBid = negotiationSession.getOpponentBidHistory().getLastBidDetails();
        double timePassed = negotiationSession.getTimeline().getTime();
        if (timePassed > 0.85 && doRoundTimeMeasure) {
            if (timePassedList.size() < 10) {
                timePassedList.add(timePassed);
            } else {
                averageRoundTime = (timePassedList.get(timePassedList.size() - 1) - timePassedList.get(0)) / timePassedList.size();
                doRoundTimeMeasure = false;
            }
        }

        if (1.0 - (averageRoundTime * 10) <= timePassed) {
            acceptanceFactor -= 0.05;
            //TODO check for best utility in opponents bids made previously (in bidding strategy)
        }

        if (opponentsBid.getMyUndiscountedUtil() >= thresholdUtility) {
            System.out.println(timePassed);
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
