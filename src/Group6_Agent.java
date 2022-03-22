import negotiator.boaframework.agent.TheBOAagent;

public class Group6_Agent extends TheBOAagent {

    @Override
    public void agentSetup() {
        this.setDecoupledComponents(new Group6_AS(), new Group6_BS(), new Group6_OM(), new Group6_OMS());
    }

    @Override
    public String getName() {
        return "Group 6 Strategy";
    }
}