import genius.core.boaframework.NegotiationSession;
import genius.core.list.Tuple;

import java.util.ArrayList;
import java.util.Map;

// Does not extend SharedAgentState since SharedAgentState does not allow a private constructor
public class Group6_SAS {

    private static Group6_SAS INSTANCE;

    private NegotiationSession session;
    private Map<String, Double> parameters;
    private ArrayList<Double> estimatedUtilities;

    private final int LAST_X_ROUNDS = 5;

    private Group6_SAS(NegotiationSession session, Map<String, Double> parameters) {
        this.session = session;
        this.parameters = parameters;
        this.estimatedUtilities = new ArrayList<>();
        session.getOpponentBidHistory().size();
    }

    public static Group6_SAS getInstance(NegotiationSession session, Map<String, Double> parameters) {
        if (INSTANCE == null) {
            INSTANCE = new Group6_SAS(session, parameters);
        }

        return INSTANCE;
    }

    // Call every round from BiddingStrategy to keep track of estimated opponent utility over the negotiation
    public void update(double estimatedUtility) {
        this.estimatedUtilities.add(estimatedUtility);
    }

    public Tuple<Double, Double> getOpponentConcessionFactor() {
        // In case this is called when the opponent has not made any bids
        if (this.estimatedUtilities.isEmpty()) {
            return new Tuple<>(0.0D, 0.0D);
        }

        // If the amount of bids the opponent has made is lower than the amount of rounds to check for
        // take the amount of opponent bids instead
        int x = Math.min(LAST_X_ROUNDS, this.estimatedUtilities.size());

        // Calculate how much the estimated utility changes
        double utilityOverLastRounds = getAverageUtilityChange(x);
        double overallUtility = getAverageUtilityChange(0);

        // First number is the average change in estimated opponent utility over the selected amount of rounds
        // If this number is negative, the opponent is conceding. Lower = more conceding, above 0 = not conceding, being greedy
        // Number is in percentages, so 0.05 would mean that the estimated utility lowered on average 5 percent every round
        // The second number is the average utility of the whole negotiation divided by the current average utility.
        // This number falls in the range of when this number = 1, the strategy is not changing (will be the case in rounds 1...LAST_X_ROUNDS)
        // [0,0.9999] = conceding, lower = more conceding
        // [1,1] = not deviating
        // [1.00001, inf] = hardheaded, higher = more hardheaded
        return new Tuple<>(utilityOverLastRounds, overallUtility / utilityOverLastRounds);
    }

    private double getAverageUtilityChange(int amountOfRounds) {
        double firstUtility = (amountOfRounds == 0) ? estimatedUtilities.get(0) : estimatedUtilities.get(estimatedUtilities.size() - amountOfRounds - 1);
        double lastUtility = estimatedUtilities.get(estimatedUtilities.size() - 1);
        return (lastUtility - firstUtility) / estimatedUtilities.size();
    }
}
