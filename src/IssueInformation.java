import genius.core.issue.Objective;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.list.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IssueInformation {

    private HashMap<ValueDiscrete, Integer> valueFrequencies;
    private double weight;

    public IssueInformation(List<ValueDiscrete> values) {
        this.valueFrequencies = new HashMap<>();
        for (ValueDiscrete value : values) {
            valueFrequencies.put(value, 0);
        }
    }

    public void update(ValueDiscrete value) {
        if (!valueFrequencies.containsKey(value)) {
            valueFrequencies.put(value, 1);
        } else {
            valueFrequencies.put(value, valueFrequencies.get(value) + 1);
        }
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    // Returns the Value with the highest frequency and the relative importance of it, compared to the other values in the issue
    public Tuple<Value, Double> getHighestRelativeValue(Integer amountOfRounds) {
        Map.Entry<ValueDiscrete, Integer> highestEntry = null;

        for (Map.Entry<ValueDiscrete, Integer> entry : valueFrequencies.entrySet())
        {
            if (highestEntry == null || entry.getValue() > highestEntry.getValue()) {
                highestEntry = entry;
            }
        }
        return new Tuple<>(highestEntry.getKey(), (double) highestEntry.getValue() / amountOfRounds);
    }

    public Double getRelativeValue(Value value, Integer amountOfRounds) {
        return (double) valueFrequencies.get(value) / amountOfRounds;
    }

}
