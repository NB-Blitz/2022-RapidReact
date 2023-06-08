package org.team5148.rapidreact.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.team5148.rapidreact.NTManager;
import org.team5148.rapidreact.config.DefaultSpeed;
import org.team5148.rapidreact.config.MotorIDs;

public class Climber {
    public static final double MAX_POSITION = 1000;
    public static final double MIN_POSITION = -1000;

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
     * Runs the left climber hook
     * @param speed - Speed of the left hook [-1 - 1]
     */
    public void runLeft(double speed) {
        double leftPosition = leftEncoder.getPosition();

        //nt.climberLeftPos.setDouble(leftPosition);

        if (speed > 0 && leftPosition >= MAX_POSITION)
            forceRunLeft(0);
        else if (speed < 0 && leftPosition <= MIN_POSITION)
            forceRunLeft(0);
        else
            forceRunLeft(speed);
    }

    /**
     * Runs the left climber hook, ignoring any encoder input
     * @param speed - Speed of the left hook [-1 - 1]
     */
    public void forceRunLeft(double speed) {
        //double input = nt.climberSpeed.getDouble(DefaultSpeed.CLIMBER) * speed;
        //leftMotor.set(input);
    }

    /**
     * Runs the right climber hook
     * @param speed - Speed of the right hook [-1 - 1]
     */
    public void runRight(double speed) {
        double rightPosition = rightEncoder.getPosition();

        //nt.climberRightPos.setDouble(rightPosition);

        if (speed > 0 && rightPosition >= MAX_POSITION)
            forceRunRight(0);
        else if (speed < 0 && rightPosition <= MIN_POSITION)
            forceRunRight(0);
        else
            forceRunRight(speed);
    }

    /**
     * Runs the right climber hook, ignoring any encoder input
     * @param speed - Speed of the right hook [-1 - 1]
     */
    public void forceRunRight(double speed) {
        //double input = nt.climberSpeed.getDouble(DefaultSpeed.CLIMBER) * speed;
        //rightMotor.set(input);
    }
}
