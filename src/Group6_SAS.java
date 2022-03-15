import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.SharedAgentState;
import genius.core.list.Tuple;

import java.util.ArrayList;

public class Group6_SAS extends SharedAgentState {

    NegotiationSession session;
    ArrayList<Double> estimatedUtilities;

    final int LAST_X_ROUNDS = 5;

    public Group6_SAS(NegotiationSession session) {
        this.session = session;
        this.estimatedUtilities = new ArrayList<>();
    }

    // Call every round from BiddingStrategy to keep track of estimated opponent utility over the negotiation
    public void update(double estimatedUtility) {
        this.estimatedUtilities.add(estimatedUtility);
    }

    public Tuple<Double, Double> getOpponentConcessionFactor() {
        // Calculate how much the estimated utility changes
        double utilityOverLastRounds = getAverageUtilityChange(LAST_X_ROUNDS);
        double overallUtility = getAverageUtilityChange(0);
        return new Tuple<>(utilityOverLastRounds, overallUtility / utilityOverLastRounds);
    }

    private double getAverageUtilityChange(int amountOfRounds) {
        double firstUtility = (amountOfRounds == 0) ? estimatedUtilities.get(0) : estimatedUtilities.get(estimatedUtilities.size() - amountOfRounds - 1);
        double lastUtility = estimatedUtilities.get(estimatedUtilities.size() - 1);
        return (lastUtility - firstUtility) / session.getOwnBidHistory().size();
    }
}
