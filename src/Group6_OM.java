
import genius.core.Bid;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.OpponentModel;
import genius.core.issue.Issue;
import genius.core.issue.Value;
import genius.core.list.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group6_OM extends OpponentModel {

    private ArrayList<Bid> bidHistory;

    // Frequency model = HashMap, Key = Tuple<Issue, Value>, value = frequency
    // Counting per issue which category is voted, (<Issue, Value>, Freq)
    private HashMap<Tuple<Integer, Value>, Integer> frequencyModel;

    private HashMap<Tuple<Integer, Value>, Double> valueModel;
    private HashMap<Tuple<Integer, Value>, Double> issueModel;

    @Override
    public void init(NegotiationSession negotiationSession,
                     Map<String, Double> parameters) {
        super.init(negotiationSession, parameters);
        bidHistory = new ArrayList<>();
        frequencyModel = new HashMap<>();
        valueModel = new HashMap<>();
        issueModel = new HashMap<>();
    }

    @Override
    protected void updateModel(Bid bid, double time) {
        // Possibly add maximum amount of bids as parameter (100 in CUHK OM)
        bidHistory.add(bid);
        updateFrequencyModel(bid);
        updatePreferences(bid);
    }

    private void updateFrequencyModel(Bid bid) {
        HashMap<Integer, Value> currentIssues = bid.getValues();

        // For every issue in the bid, update the frequency in the frequencyModel
        currentIssues.forEach((issueId, value) -> updateSingularFrequency(issueId, value));
    }

    private void updateSingularFrequency(Integer issueId, Value value) {
        Tuple<Integer, Value> key = new Tuple<>(issueId, value);
        if (!frequencyModel.containsKey(key)) {
            // If the frequencyModel does not yet contain the issue/value combination, add it to the HashMap and set its value to 1
            frequencyModel.put(key, 1);
        } else {
            // If the frequencyModel does contain the issue/value combination, increase its frequency by one
            frequencyModel.put(key, frequencyModel.get(key) + 1);
        }
    }

    private void updatePreferences(Bid bid) {
        // Update value weights using frequencyModel, take highest frequency and divide it by amount of bids/rounds in total
        
        // Update issue weights take value weights of highest and divide by total value weights

    }

    @Override
    public double getBidEvaluation(Bid bid) {
        // Return estimated utility value for opponent bid
        return 0D;
    }
}
