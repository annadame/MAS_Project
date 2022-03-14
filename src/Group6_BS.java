import genius.core.Bid;
import genius.core.bidding.BidDetails;
import genius.core.boaframework.*;
import genius.core.utility.AbstractUtilitySpace;

import java.util.concurrent.TimeUnit;

import java.util.ArrayList;
import java.util.Map;

public class Group6_BS extends OfferingStrategy {
    private SortedOutcomeSpace possibleAgentBids;
    private BidDetails startingBid;
    private double tresholdUtility;
    private double startingBidUtility;
    private double stageOneAllowedTime;
    private double stageTwoAllowedTime;
    private double stageThreeAllowedTime;
    private double scareTacticUtility;

    @Override
    public void init(NegotiationSession negotiationSession, OpponentModel opponentModel, OMStrategy omStrategy,
                     Map<String, Double> parameters) throws Exception {
        super.init(negotiationSession, opponentModel, omStrategy, parameters);
        possibleAgentBids = new SortedOutcomeSpace(negotiationSession.getUtilitySpace());
        negotiationSession.setOutcomeSpace(possibleAgentBids);

        startingBidUtility = 0.8;
        //TODO make tresholdUtility value dynamical in between stages
        tresholdUtility = 0.8;
        stageOneAllowedTime = 0.2;
        stageTwoAllowedTime = 0.8;
        stageThreeAllowedTime = 0.85;
        scareTacticUtility = 0.9;


        startingBid = possibleAgentBids.getBidNearUtility(startingBidUtility);
    }

    @Override
    public BidDetails determineOpeningBid() {
        return startingBid;
    }

    @Override
    public BidDetails determineNextBid() {
        double timePassed = negotiationSession.getTimeline().getTime();
        System.out.println(timePassed);

        if (timePassed < stageOneAllowedTime /* TODO add here when opponentmodel is accurate enough, discuss with Rick and Marije */) {
            // stage 1 hardheaded bid while determining opponent model + strategy
            return startingBid;
        } else if (timePassed < stageTwoAllowedTime) {
            // stage 2 Perlin-noise tactic while conceding based on opponent conceding factor
            if (true /*TODO add method for determining if opponent model is reliable and return bool*/) {
                /*TODO discuss how we get concedingfactor of opponent (from opponent model strategy?)*/
            } else {
                /*TODO determine what to do if there is no (reliable) opponent model*/
            }
            return startingBid;
        } else if (timePassed < stageThreeAllowedTime) {
            // stage 3
            /*TODO possibly make scareTacticUtility a dynamic value*/
            return possibleAgentBids.getBidNearUtility(scareTacticUtility);
        } else {
            // stage 4
            //BidDetails bid = omStrategy.getBid(opponentModel.getOpponentUtilitySpace(), tresholdUtility);
            return null;
        }
    }

    @Override
    public String getName() {
        return "Group 6 Bidding Strategy";
    }
}
