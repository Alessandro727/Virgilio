package neuralNetwork;
import java.util.List;
import neuralNetwork.Connection;
/**
 * Calculates the weighted sums of the input neurons' outputs.
 */
public final class WeightedSumFunction implements InputSummingFunction {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double collectOutput(List<Connection> inputConnections) {
		double weightedSum = 0d;
		for (Connection connection : inputConnections) {
			weightedSum += connection.getWeight();
		}
		return weightedSum;
	}

	@Override
	public double getOutput(List<Connection> inputConnections) {
		// TODO Auto-generated method stub
		return 0;
	}
}