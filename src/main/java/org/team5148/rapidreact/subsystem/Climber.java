package org.team5148.rapidreact.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.team5148.rapidreact.NTManager;
import org.team5148.rapidreact.config.DefaultSpeed;
import org.team5148.rapidreact.config.MotorIDs;

public class Climber {
    private static final double MAX_POSITION = 10000;
    private static final double MIN_POSITION = 0;

    private CANSparkMax motor = new CANSparkMax(MotorIDs.CLIMBER, MotorType.kBrushless);
    private RelativeEncoder encoder = motor.getEncoder();

    private NTManager nt = NTManager.getInstance();

    /**
     * Runs the climber hook
     * @param speed - Speed of the hook [-1 - 1]
     */
    public void run(double speed) {
        double position = encoder.getPosition();
        double input = nt.climberSpeed.getDouble(DefaultSpeed.CLIMBER) * speed;
        if (input > 0 && position >= MAX_POSITION)
            motor.set(0);
        else if (input < 0 && position <= MIN_POSITION)
            motor.set(0);
        else
            motor.set(input);
        nt.climberPos.setDouble(position);
    }
}
