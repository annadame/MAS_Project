import genius.core.Bid;
import genius.core.bidding.BidDetails;
import genius.core.boaframework.*;
import java.util.concurrent.TimeUnit;

import java.util.ArrayList;
import java.util.Map;

public class Group6_BS extends OfferingStrategy {
    private SortedOutcomeSpace possibleAgentBids;
    private BidDetails startingBid;

    @Override
    public void init(NegotiationSession negotiationSession, OpponentModel opponentModel, OMStrategy omStrategy,
                     Map<String, Double> parameters) throws Exception {
        super.init(negotiationSession, parameters);
        possibleAgentBids = new SortedOutcomeSpace(negotiationSession.getUtilitySpace());
        negotiationSession.setOutcomeSpace(possibleAgentBids);

        startingBid = possibleAgentBids.getBidNearUtility(0.8);
    }

    @Override
    public BidDetails determineOpeningBid() {
        return startingBid;
    }

    @Override
    public BidDetails determineNextBid() {
        double timePassed = negotiationSession.getTimeline().getTime();
        System.out.println(timePassed);

        if (timePassed < 0.2){
            // stage 1 hardheaded bid while determining opponent model + strategy
            return startingBid;
        } else if (timePassed < 0.5){
            // stage 2 Perlin-noise tactic while conceding based on opponent conceding factor
            return null;
        } else if (timePassed < 0.8) {
            // stage 3
            return null;
        } else {
            // stage 4
            return null;
        }
    }

    @Override
    public String getName() {
        return "Group 6 Bidding Strategy";
    }
}
