package reaction_time_tester;

public class Result {
    private String mode;
    private double averageTime;
    private double fastestTime;
    private int attempts;

    public Result(String mode, double averageTime, double fastestTime, int attempts) {
        this.mode = mode;
        this.averageTime = averageTime;
        this.fastestTime = fastestTime;
        this.attempts = attempts;
    }

    public String getMode() { return mode; }
    public double getAverageTime() { return averageTime; }
    public double getFastestTime() { return fastestTime; }
    public int getAttempts() { return attempts; }
}

