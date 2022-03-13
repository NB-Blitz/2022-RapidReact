package org.team5148.rapidreact.subsystem;

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.team5148.rapidreact.NTManager;
import org.team5148.rapidreact.config.DefaultSpeed;
import org.team5148.rapidreact.config.MotorIDs;

public class Climber {
    private TalonSRX motor = new TalonSRX(MotorIDs.CLIMBER);
    private NTManager nt = NTManager.getInstance();

    /**
     * Runs the climber hook
     * @param speed - Speed of the hook [-1 - 1]
     */
    public void run(double speed) {
        double input = nt.climberSpeed.getDouble(DefaultSpeed.CLIMBER) * speed;
        motor.set(TalonSRXControlMode.PercentOutput, input);
    }
}
