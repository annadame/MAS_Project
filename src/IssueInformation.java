import genius.core.issue.Value;
import genius.core.list.Tuple;

import java.util.HashMap;
import java.util.Map;

public class IssueInformation {

    private HashMap<Value, Integer> valueFrequencies;
    private double weight;

    public IssueInformation() {
        this.valueFrequencies = new HashMap<>();
    }

    public void update(Value value) {
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

    public Tuple<Value, Double> getHighestRelativeValue(Integer amountOfRounds) {
        Map.Entry<Value, Integer> highestEntry = null;

        for (Map.Entry<Value, Integer> entry : valueFrequencies.entrySet())
        {
            if (highestEntry == null || entry.getValue() > highestEntry.getValue()) {
                highestEntry = entry;
            }
        }
        return new Tuple<Value, Double>(highestEntry.getKey(), (double) highestEntry.getValue() / amountOfRounds);
    }

    public Double getRelativeValue(Value value, Integer amountOfRounds) {
        return (double) valueFrequencies.get(value) / amountOfRounds;
    }

}
