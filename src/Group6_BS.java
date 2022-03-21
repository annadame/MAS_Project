import genius.core.bidding.BidDetails;
import genius.core.boaframework.*;
import genius.core.list.Tuple;
import genius.core.misc.Range;

import java.util.List;
import java.util.Map;

public class Group6_BS extends OfferingStrategy {
    private SortedOutcomeSpace possibleAgentBids;
    private Group6_SAS helper;
    private BidDetails startingBid;
    private double targetUtility;
    private double maxUtilityRange;
    private double startingBidUtility;
    private double stageOneAllowedTime;
    private double stageTwoAllowedTime;
    private double stageThreeAllowedTime;
    private double scareTacticUtility;

    @Override
    public void init(NegotiationSession negotiationSession, OpponentModel opponentModel, OMStrategy omStrategy,
                     Map<String, Double> parameters) throws Exception {
        // Call default init of super class
        super.init(negotiationSession, opponentModel, omStrategy, parameters);

        this.helper = Group6_SAS.getInstance(negotiationSession, parameters);
        // Determine all possible bids of the negotiation
        possibleAgentBids = new SortedOutcomeSpace(negotiationSession.getUtilitySpace());
        negotiationSession.setOutcomeSpace(possibleAgentBids);

        startingBidUtility = 1.0;
        targetUtility = 1.0;
        maxUtilityRange = 1.0;
        stageOneAllowedTime = 0.2;
        stageTwoAllowedTime = 0.8;
        stageThreeAllowedTime = 0.85;
        scareTacticUtility = 0.98;

        // Get starting bid by choosing a bid from all possible bids which is near desired starting utility
        startingBid = possibleAgentBids.getBidNearUtility(startingBidUtility);
    }

    @Override
    public BidDetails determineOpeningBid() {
        return startingBid;
    }

    @Override
    public BidDetails determineNextBid() {
        // Update SAS for concession factor with evaluation of last opponent bid
        double opponentBidEvaluation = opponentModel.getBidEvaluation(this.negotiationSession.getOpponentBidHistory().getLastBid());
        if (opponentBidEvaluation != -1) {
            helper.update(opponentBidEvaluation);
        }
        double timePassed = negotiationSession.getTimeline().getTime();

        if (timePassed < stageOneAllowedTime /* TODO add here when opponent model is accurate enough, discuss with Rick and Marije */) {
            // Stage 1
            System.out.println("Stage 1 = " + startingBid.getMyUndiscountedUtil());
            return startingBid;
        } else if (timePassed < stageTwoAllowedTime) {
            // Stage 2
            Tuple<Double, Double> opponentConcessionFactor = helper.getOpponentConcessionFactor();

            // Base concession factor on time
            double concessionFactor = (Math.pow(1.25, timePassed) - 1) / 100;

            if (true /*TODO add method for determining if opponent model is reliable and return bool*/) {
                // If opponent is more hardheaded than us, then we get his concession factor decreased by 10%, to be even more hardheaded
                if (opponentConcessionFactor.get1() < concessionFactor) {
                    concessionFactor = opponentConcessionFactor.get1() / 1.1;
                }
            }
            targetUtility = 1 - concessionFactor;

            // Add noise
            double noise = targetUtility + ((Math.sin(timePassed * 100) + Math.sin((timePassed * 100) / 3)) / 100);

            System.out.println("Stage 2 = " + possibleAgentBids.getBidNearUtility(noise).getMyUndiscountedUtil());
            return omStrategy.getBid(possibleAgentBids.getBidsinRange(new Range(noise, 1.0)));
        } else if (timePassed < stageThreeAllowedTime) {
            // Stage 3
            /*TODO possibly make scareTacticUtility a dynamic value*/
            System.out.println("Stage 3 = " + scareTacticUtility);
            return possibleAgentBids.getBidNearUtility(scareTacticUtility);
        } else {
            // Stage 4
            BidDetails bestOpponentBid = omStrategy.getBid(possibleAgentBids.getBidsinRange(new Range(targetUtility, maxUtilityRange)));
            maxUtilityRange = targetUtility;
            targetUtility = 1 - (Math.pow(1.04, 100 - ((1 - timePassed) * 1000)) / 100);

            System.out.println("Stage 4 = " + bestOpponentBid.getMyUndiscountedUtil());
            return bestOpponentBid;
        }
    }

    @Override
    public String getName() {
        return "Group 6 Bidding Strategy";
    }
}
