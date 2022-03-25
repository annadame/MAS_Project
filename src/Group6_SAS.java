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

    public double getOpponentExpectedUtilityChange() {
        // If no opponent bid has been made, their utility is not expected to change since no utility is known
        if (this.estimatedUtilities.isEmpty()) {
            return 0D;
        }

        // Determine weight based on opponent, in the beginning, the total negotiation is more important, at the end
        // the last x bids are more important
        double lastXWeight = this.session.getTime() * 2;
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

    private double getAverageUtilityChange(int amountOfRounds) {
        double firstUtility = (amountOfRounds == 0) ? estimatedUtilities.get(0) : estimatedUtilities.get(estimatedUtilities.size() - amountOfRounds);
        double lastUtility = estimatedUtilities.get(estimatedUtilities.size() - 1);

        if (amountOfRounds == 0) amountOfRounds = estimatedUtilities.size();

        return (lastUtility - firstUtility) / amountOfRounds;
    }
}
