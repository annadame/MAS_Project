import genius.core.boaframework.NegotiationSession;
import genius.core.list.Tuple;

import java.util.ArrayList;
import java.util.Map;

// Does not extend SharedAgentState since it cant have a private constructor
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
