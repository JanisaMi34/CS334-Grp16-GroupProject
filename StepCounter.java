package edu.txst.project;

public class StepCounter {
	private int steps;

	public int getSteps() {
		return steps;
	}

	public void increaseSteps() {
		steps++;
	}

	public void resetSteps() {
		steps = 0;
	}

	public void decreaseSteps(int stepMinus) {  // Decrease steps by a specified amount ( used in the hacker cell )
		if (steps > 0) {
			steps -= stepMinus;
		}
	}

	public StepCounter() {
		steps = 0;
	}
}
