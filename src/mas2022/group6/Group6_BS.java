package mas2022.group6;

import genius.core.bidding.BidDetails;
import genius.core.boaframework.*;
import genius.core.misc.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Group6_BS extends OfferingStrategy {

    private final int LAST_X_ROUNDS = 5;

    private SortedOutcomeSpace possibleAgentBids;
    private NoiseGenerator noiseGenerator;
    private BidDetails startingBid;
    private List<Double> estimatedUtilities;

    private double targetUtility;
    private double deviationRange;
    private double startingBidUtility;
    private double acceptanceFactor;
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

        this.estimatedUtilities = new ArrayList<>();

        // Constructor gets random seed
        this.noiseGenerator = new NoiseGenerator();

        // Determine all possible bids of the negotiation
        this.possibleAgentBids = new SortedOutcomeSpace(negotiationSession.getUtilitySpace());
        this.negotiationSession.setOutcomeSpace(possibleAgentBids);

        this.startingBidUtility = 1.0;
        this.targetUtility = 1.0;
        this.deviationRange = 0.025;
        this.acceptanceFactor = 0.95;
        this.stageOneAllowedTime = 0.2;
        this.stageTwoAllowedTime = 0.8;
        this.stageThreeAllowedTime = 0.825;
        this.stageThreeStarted = false;

        // Get starting bid by choosing a bid from all possible bids which is near desired starting utility
        this.startingBid = possibleAgentBids.getBidNearUtility(startingBidUtility);
    }

    @Override
    public BidDetails determineOpeningBid() {
        return startingBid;
    }

    @Override
    public BidDetails determineNextBid() {
        this.updateOpponentExpectedUtilityChange();

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
        return startingBid;
    }

    /**
     * In stage two, we calculate the amount of utility that we want to concede in this round, if this amount is higher
     * than the amount we estimate the opponent will concede, we adapt our concession to that of the opponent's.
     * Afterwards, to confuse the opponent an amount of Perlin noise is added, lulling the opponent
     * into thinking we are being more conceding/hardheaded than we are
     * @param timePassed amount of time that has passed since the beginning of the negotiation
     * @return The BidDetails of the selected bid
     */
    public BidDetails stageTwo(double timePassed) {
        double opponentExpectedUtilityChange = this.getOpponentExpectedUtilityChange();

        // Base concession factor on time
        double concessionDenominator = 1000D;
        double concessionFactor = (Math.pow(1.25, timePassed) - 1) / concessionDenominator;
        double prevConcessionFactor = (Math.pow(1.25, this.negotiationSession.getOwnBidHistory().getLastBidDetails().getTime()) - 1) / concessionDenominator;

        // If opponent is more hardheaded than us, then we get his concession factor decreased by 10%, to be even more hardheaded
        if (concessionFactor - prevConcessionFactor > opponentExpectedUtilityChange) {
            concessionFactor = opponentExpectedUtilityChange * 0.9;
        }

        // If opponent is increasing in utility we stay at the same utility
        if (opponentExpectedUtilityChange < 0) {
            concessionFactor = 0;
        }

        targetUtility -= concessionFactor;

        // Range of perlinNoise is about [-0.05,0.05] so it deviates the bid 5% in either direction
        double noise = targetUtility + noiseGenerator.noise(negotiationSession.getOwnBidHistory().size()) / 20.0D;
        if (noise > 1.0D) {
            noise = 1.0D;
        }

        BidDetails bid = omStrategy.getBid(possibleAgentBids.getBidsinRange(
                new Range(noise - deviationRange, noise + deviationRange)));

        return (bid == null) ? possibleAgentBids.getBidNearUtility(noise) : bid;
    }

    /**
     * In stage three, the agent calculates a utility value to use as a scare tactic based on the last utility
     * used in stage two, the agent then statically offers the bid closest to this utility
     * @return The BidDetails of the selected bid
     */
    public BidDetails stageThree() {
        if (!stageThreeStarted) {
            stageThreeStarted = true;

            // Calculate scareTacticUtility based on the last bid of stage two
            double lastBidOfStageTwoUtility = this.negotiationSession.getOwnBidHistory().getLastBidDetails().getMyUndiscountedUtil();
            scareTacticUtility = lastBidOfStageTwoUtility + (0.6 * (1 - lastBidOfStageTwoUtility));

            // Set targetUtility to scareTacticUtility
            targetUtility = scareTacticUtility;
        }

        return possibleAgentBids.getBidNearUtility(scareTacticUtility);
    }

    /**
     * In stage four, the agent returns to the targetUtility from stage 2 and finds a bid there,
     * if no bid is found in the determined range around the targetUtility, the nearest bid to the
     * targetUtility is used. If this bid has a lower utility for us than any other bid the opponent has made,
     * we just offer the opponent that bid. The targetUtility is also lowered for the next round, the amount it is
     * lowered by increases when the opponent is being very hardheaded
     * @param timePassed amount of time that has passed since the beginning of the negotiation
     * @return The BidDetails of the selected bid
     */
    public BidDetails stageFour(double timePassed) {
        // Stage 4
        BidDetails bestOpponentBid = omStrategy.getBid(possibleAgentBids.getBidsinRange(
                new Range(targetUtility - deviationRange, targetUtility + deviationRange)));
        BidDetails bestBidOpponentMade = negotiationSession.getOpponentBidHistory().getBestBidDetails();
        // No bid in range found, just get nearest to targetUtility
        if (bestOpponentBid == null) {
            bestOpponentBid = possibleAgentBids.getBidNearUtility(targetUtility);
        }

        if (bestBidOpponentMade.getMyUndiscountedUtil() > (negotiationSession.getOwnBidHistory().getLastBidDetails().getMyUndiscountedUtil() * acceptanceFactor)) {
            return bestBidOpponentMade;
        }

        // Make the change in utility larger when the opponent is not deviating a lot
        double divisionFactor = 3000D;
        if (this.getOpponentExpectedUtilityChange() < 0.0001) {
            divisionFactor = 1000D;
        }

        targetUtility -= (Math.pow(1.04, 100 - ((1 - timePassed) * 5000)) / divisionFactor);

        return bestOpponentBid;
    }

    /**
     * Add to the estimatedUtilities list if a bid was made
     */
    private void updateOpponentExpectedUtilityChange() {
        double opponentBidEvaluation = this.opponentModel.getBidEvaluation(this.negotiationSession.getOpponentBidHistory().getLastBid());
        if (opponentBidEvaluation != -1) {
            this.estimatedUtilities.add(opponentBidEvaluation);
        }
    }

    /**
     * Calculates the amount we expect the opponent to deviate, given our estimations of their utility over
     * LAST_X_ROUNDS compared to the last 100 rounds. The estimation is calculated with a weight that increases
     * towards LAST_X_ROUNDS The further the negotiation is in time
     * @return Percentage change the opponent is estimated to make
     */
    public double getOpponentExpectedUtilityChange() {
        // If no opponent bid has been made, their utility is not expected to change since no utility is known
        if (this.estimatedUtilities.isEmpty()) {
            return 0D;
        }

        // Determine weight based on opponent, in the beginning, the total negotiation is more important, at the end
        // the last x bids are more important
        double lastXWeight = this.negotiationSession.getTime() * 2;
        double totalWeight = 2 - lastXWeight;

        // If the amount of bids the opponent has made is lower than the amount of rounds to check for
        // take the amount of opponent bids instead
        int x = Math.min(LAST_X_ROUNDS, this.estimatedUtilities.size());
        int y = Math.min(100, this.estimatedUtilities.size());

        double lastXChange = getAverageUtilityChange(x);
        double totalChange = getAverageUtilityChange(y);

        // Get weighted expected utility change of opponent
        return (totalChange * totalWeight + lastXChange * lastXWeight) / 2;
    }

    /**
     * @param amountOfRounds for which the average should be calculated
     * @return average change in utility over the amount of rounds
     */
    private double getAverageUtilityChange(int amountOfRounds) {
        double firstUtility = (amountOfRounds == 0) ? estimatedUtilities.get(0) : estimatedUtilities.get(estimatedUtilities.size() - amountOfRounds);
        double lastUtility = estimatedUtilities.get(estimatedUtilities.size() - 1);

        if (amountOfRounds == 0) amountOfRounds = estimatedUtilities.size();

        return (lastUtility - firstUtility) / amountOfRounds;
    }

    @Override
    public String getName() {
        return "Group 6 - Roosevelt - Bidding Strategy";
    }
}
