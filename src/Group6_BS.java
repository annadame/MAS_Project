import genius.core.bidding.BidDetails;
import genius.core.boaframework.*;
import genius.core.misc.Range;

import java.util.Map;

public class Group6_BS extends OfferingStrategy {

    private SortedOutcomeSpace possibleAgentBids;
    private Group6_SAS helper;
    private NoiseGenerator noiseGenerator;
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

        // Constructor gets random seed
        this.noiseGenerator = new NoiseGenerator();

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
            return stageOne();
        } else if (timePassed < stageTwoAllowedTime) {
            return stageTwo(timePassed);
        } else if (timePassed < stageThreeAllowedTime) {
            return stageThree();
        } else {
            return stageFour(timePassed);
        }
    }

    public BidDetails stageOne() {
        System.out.println("Stage 1 = " + startingBid.getMyUndiscountedUtil());
        return startingBid;
    }

    public BidDetails stageTwo(double timePassed) {
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

        // Range of perlinNoise is about [-0.05,0.05] so it deviates the bid 5% in either direction
        double noise = targetUtility + noiseGenerator.noise(negotiationSession.getOwnBidHistory().size()) / 20.0D;
        if (noise > 1.0D) {
            noise = 1.0D;
        }

        System.out.println("Stage 2 = " + possibleAgentBids.getBidNearUtility(noise).getMyUndiscountedUtil());

        BidDetails bid = omStrategy.getBid(possibleAgentBids.getBidsinRange(
                new Range(noise - deviationRange, noise + deviationRange)));

        return (bid == null) ? negotiationSession.getOutcomeSpace().getBidNearUtility(noise) : bid;
    }

    public BidDetails stageThree() {
        if (!stageThreeStarted) {
            stageThreeStarted = true;

            // Calculate scareTacticUtility based on the last bid of stage two
            double lastBidOfStageTwoUtility = this.negotiationSession.getOwnBidHistory().getLastBidDetails().getMyUndiscountedUtil();
            scareTacticUtility = lastBidOfStageTwoUtility + (0.6 * (1 - lastBidOfStageTwoUtility));

            // Set targetUtility to scareTacticUtility
            targetUtility = scareTacticUtility;
        }

        System.out.println("Stage 3 = " + scareTacticUtility);

        return possibleAgentBids.getBidNearUtility(scareTacticUtility);
    }

    public BidDetails stageFour(double timePassed) {
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

        targetUtility -= (Math.pow(1.04, 100 - ((1 - timePassed) * 10000)) / divisionFactor);
        System.out.println("Stage 4 = " + targetUtility);
        return bestOpponentBid;
    }

    @Override
    public String getName() {
        return "Group 6 Bidding Strategy";
    }
}
