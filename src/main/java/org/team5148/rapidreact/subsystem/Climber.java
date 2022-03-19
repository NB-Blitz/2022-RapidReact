package org.team5148.rapidreact.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.team5148.rapidreact.NTManager;
import org.team5148.rapidreact.config.DefaultSpeed;
import org.team5148.rapidreact.config.MotorIDs;

public class Climber {
    public static final double MAX_POSITION = -5;
    public static final double MIN_POSITION = -18;

    private CANSparkMax leftMotor = new CANSparkMax(MotorIDs.LEFT_CLIMBER, MotorType.kBrushless);
    private CANSparkMax rightMotor = new CANSparkMax(MotorIDs.RIGHT_CLIMBER, MotorType.kBrushless);
    private RelativeEncoder leftEncoder = leftMotor.getEncoder();
    private RelativeEncoder rightEncoder = rightMotor.getEncoder();
    private NTManager nt = NTManager.getInstance();

    public void reset() {
        leftEncoder.setPosition(0);
        rightEncoder.setPosition(0);
    }

    /**
     * Runs the climber hook
     * @param speed - Speed of the hook [-1 - 1]
     */
    public void run(double speed) {
        double leftPosition = leftEncoder.getPosition();
        double rightPosition = rightEncoder.getPosition();

        nt.climberLeftPos.setDouble(leftPosition);
        nt.climberRightPos.setDouble(rightPosition);

        if (speed > 0 && leftPosition >= MAX_POSITION)
            forceRun(0);
        else if (speed < 0 && leftPosition <= MIN_POSITION)
            forceRun(0);
        else
            forceRun(speed);
    }

    /**
     * Runs the climber hook, ignoring any encoder input
     * @param speed - Speed of the hook [-1 - 1]
     */
    public void forceRun(double speed) {
        double input = nt.climberSpeed.getDouble(DefaultSpeed.CLIMBER) * speed;
        leftMotor.set(input);
        rightMotor.set(input);
    }
}
