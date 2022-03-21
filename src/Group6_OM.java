import genius.core.Bid;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.OpponentModel;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group6_OM extends OpponentModel {

    private ArrayList<Bid> bidHistory;

    // HashMap mapping issues to object containing information like values and frequencies
    private HashMap<Integer, IssueInformation> frequencyModel;

    @Override
    public void init(NegotiationSession negotiationSession,
                     Map<String, Double> parameters) {
        super.init(negotiationSession, parameters);

        bidHistory = new ArrayList<>();
        frequencyModel = new HashMap<>();

        // Fill FrequencyModel with the issues of the current negotiation
        for (Issue issue : this.negotiationSession.getIssues()) {
            IssueDiscrete issue2 = (IssueDiscrete) issue;
            frequencyModel.put(issue2.getNumber(), new IssueInformation(issue2.getValues()));
        }
    }

    @Override
    protected void updateModel(Bid bid, double time) {
        bidHistory.add(bid);
        updateFrequencyModel(bid);
        updatePreferences();
    }

    private void updateFrequencyModel(Bid bid) {
        HashMap<Integer, Value> currentIssues = bid.getValues();

        // For every issue in the bid, update the frequency in the frequencyModel
        currentIssues.forEach((issueId, value) -> frequencyModel.get(issueId).update((ValueDiscrete) value));
    }

    private void updatePreferences() {
        // Update value weights using frequencyModel, take highest frequency and divide it by amount of bids/rounds in total
        // Loop through frequencyModel, for each issue, sum up frequencies and keep track of highest frequency,
        // finally, divide highest frequency by total frequency
        double totalRelativeValue = 0D;
        for (Map.Entry<Integer, IssueInformation> entry : frequencyModel.entrySet()) {
            totalRelativeValue += entry.getValue().getHighestRelativeValue(bidHistory.size()).get2();
        }

        // Update issue weights by taking value weights of highest and divide by total value weights
        for (Map.Entry<Integer, IssueInformation> entry : frequencyModel.entrySet()) {
            entry.getValue().setWeight(entry.getValue().getHighestRelativeValue(bidHistory.size()).get2() / totalRelativeValue);
        }
    }

    @Override
    public double getBidEvaluation(Bid bid) {
        // In case we make first bid there is no bid to evaluate, return 1
        if (bidHistory.isEmpty()) {
            return 1.0D;
        }

        HashMap<Integer, Value> currentIssues = bid.getValues();
        double expectedUtility = 0D;
        double weight;
        double totalWeight = 0D;

        // Get TotalWeight from IssueInformation objects
        for (Map.Entry<Integer, IssueInformation> entry : frequencyModel.entrySet()) {
            totalWeight += entry.getValue().getWeight();
        }

        // Get RelativeValue for current Issue,
        for (Map.Entry<Integer, Value> entry : currentIssues.entrySet()) {
            weight = frequencyModel.get(entry.getKey()).getWeight();
            expectedUtility += (weight / totalWeight) * frequencyModel.get(entry.getKey()).getRelativeValue(entry.getValue(), bidHistory.size());
        }

        // Return estimated utility value for opponent bid
        return expectedUtility;
    }

    @Override
    public String getName() {
        return "Group 6 Opponent Model";
    }
}
