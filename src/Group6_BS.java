import genius.core.bidding.BidDetails;
import genius.core.boaframework.*;
import genius.core.misc.Range;
import genius.core.utility.AbstractUtilitySpace;

import java.util.List;
import java.util.Map;

public class Group6_BS extends OfferingStrategy {
    private SortedOutcomeSpace possibleAgentBids;
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

        // Determine all possible bids of the negotiation
        possibleAgentBids = new SortedOutcomeSpace(negotiationSession.getUtilitySpace());
        negotiationSession.setOutcomeSpace(possibleAgentBids);

        startingBidUtility = 0.8; // Maybe change to 1
        //TODO make targetUtility value dynamical in between stages, dependent on concession factor
        targetUtility = 0.8;
        maxUtilityRange = 1.0;
        stageOneAllowedTime = 0.2;
        stageTwoAllowedTime = 0.8;
        stageThreeAllowedTime = 0.85;
        scareTacticUtility = 0.9;

        // Get starting bid by choosing a bid from all possible bids which is near desired starting utility
        startingBid = possibleAgentBids.getBidNearUtility(startingBidUtility);
    }

    @Override
    public BidDetails determineOpeningBid() {
        return startingBid;
    }

    @Override
    public BidDetails determineNextBid() {
        double timePassed = negotiationSession.getTimeline().getTime();
        System.out.println(timePassed); // TODO: Delete once not necessary anymore

        if (timePassed < stageOneAllowedTime /* TODO add here when opponent model is accurate enough, discuss with Rick and Marije */) {
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
            List<BidDetails> agentBidsInRange = possibleAgentBids.getBidsinRange(new Range(targetUtility, maxUtilityRange));
            AbstractUtilitySpace opponentPossibleBids = opponentModel.getOpponentUtilitySpace();
            double bestOpponentUtility = 0.0;
            BidDetails bestOpponentBid = null;
            for (BidDetails agentBid: agentBidsInRange) {
                // Get the utility of the opponent for these certain values of issues
                double opponentUtility = opponentPossibleBids.getUtility(agentBid.getBid());

                // Save the highest utility for opponent and corresponding bid
                if (opponentUtility > bestOpponentUtility) {
                    bestOpponentBid = agentBid;
                    bestOpponentUtility = opponentUtility;
                }
            }

            maxUtilityRange = targetUtility;
            targetUtility -= 0.05; //TODO: Make dynamically based on time
            return bestOpponentBid;
        }
    }

    @Override
    public String getName() {
        return "Group 6 Bidding Strategy";
    }
}
