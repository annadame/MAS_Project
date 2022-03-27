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
    private double deviationRange;
    private double startingBidUtility;
    private double stageOneAllowedTime;
    private double stageTwoAllowedTime;
    private double stageThreeAllowedTime;
    private double scareTacticUtility;
    private boolean stageThreeStarted;

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
        deviationRange = 0.025;
        stageOneAllowedTime = 0.2;
        stageTwoAllowedTime = 0.8;
        stageThreeAllowedTime = 0.825;
        stageThreeStarted = false;

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

        if (timePassed < stageOneAllowedTime) {
            // Stage 1
            System.out.println("Stage 1 = " + startingBid.getMyUndiscountedUtil());
            return startingBid;
        } else if (timePassed < stageTwoAllowedTime) {
            // Stage 2
            double opponentExpectedUtilityChange = helper.getOpponentExpectedUtilityChange();

            // Base concession factor on time
            double concessionDenominator = 1000D;
            double concessionFactor = (Math.pow(1.25, timePassed) - 1) / concessionDenominator;
            double prevConcessionFactor = (Math.pow(1.25, this.negotiationSession.getOwnBidHistory().getLastBidDetails().getTime()) - 1) / concessionDenominator;

            // If opponent is more hardheaded than us, then we get his concession factor decreased by 10%, to be even more hardheaded
            if (concessionFactor - prevConcessionFactor > opponentExpectedUtilityChange) {
                concessionFactor = opponentExpectedUtilityChange * 0.9;
            }
            if (opponentExpectedUtilityChange < 0) {
                concessionFactor = 0;
            }

            targetUtility -= concessionFactor;

            // Add noise (deviated from -1.54% to +1.54%)
            double noise = targetUtility + ((Math.sin(timePassed * 100) + Math.sin((timePassed * 100) / 3)) / 100);
            if (noise > 1.0) {
                noise = 1.0D;
            }

            System.out.println("Stage 2 = " + possibleAgentBids.getBidNearUtility(targetUtility).getMyUndiscountedUtil());
            BidDetails bid = omStrategy.getBid(possibleAgentBids.getBidsinRange(new Range(targetUtility - deviationRange, targetUtility + deviationRange)));
            return (bid == null) ? negotiationSession.getOutcomeSpace().getBidNearUtility(targetUtility) : bid;
        } else if (timePassed < stageThreeAllowedTime) {
            // Stage 3
            if (!stageThreeStarted) {
                stageThreeStarted = true;
                // Calculate scareTacticUtility based on the last bid of stage two
                double lastBidOfStageTwoUtility = this.negotiationSession.getOwnBidHistory().getLastBidDetails().getMyUndiscountedUtil();
                scareTacticUtility = lastBidOfStageTwoUtility + (0.6 * (1 - lastBidOfStageTwoUtility));
                targetUtility = scareTacticUtility;
            }

            System.out.println("Stage 3 = " + scareTacticUtility);


            return possibleAgentBids.getBidNearUtility(scareTacticUtility);
        } else {
            // Stage 4
            BidDetails bestOpponentBid = omStrategy.getBid(possibleAgentBids.getBidsinRange(new Range(targetUtility - deviationRange, targetUtility + deviationRange)));
            if (bestOpponentBid == null) {
                negotiationSession.getOutcomeSpace().getBidNearUtility(targetUtility);
            }

            // Make the change in utility larger when the opponent is not deviating a lot
            double divisionFactor = 3000D;
            if (helper.getOpponentExpectedUtilityChange() < 0.0001) {
                divisionFactor = 1000D;
            }

            targetUtility -= (Math.pow(1.04, 100 - ((1 - timePassed) * 1000)) / divisionFactor);
            System.out.println("Stage 4 = " + targetUtility);
            return bestOpponentBid;
        }
    }

    @Override
    public String getName() {
        return "Group 6 Bidding Strategy";
    }
}
