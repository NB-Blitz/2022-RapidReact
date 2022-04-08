package org.team5148.lib.drivetrains;

import org.team5148.lib.motors.CANSparkMax;
import org.team5148.lib.motors.MotorController;
import org.team5148.lib.util.Vector2;
import org.team5148.lib.util.Vector3;

/**
 * Controls a Tank drivetrain
 */
public class Tank extends Drivetrain {
    public MotorController m_frontLeftMotor;
    public MotorController m_frontRightMotor;
    public MotorController m_backLeftMotor;
    public MotorController m_backRightMotor;

    public Tank() {
        this(
            new CANSparkMax(1),
            new CANSparkMax(2),
            new CANSparkMax(3),
            new CANSparkMax(4)
        );
    }

    public Tank(
        MotorController frontLeftMotor,
        MotorController frontRightMotor,
        MotorController backLeftMotor,
        MotorController backRightMotor) {
        
        m_frontLeftMotor = frontLeftMotor;
        m_frontRightMotor = frontRightMotor;
        m_backLeftMotor = backLeftMotor;
        m_backRightMotor = backRightMotor;
    }

    public void setRampRate(double ramp) {
        m_frontLeftMotor.setRampRate(ramp);
        m_frontRightMotor.setRampRate(ramp);
        m_backLeftMotor.setRampRate(ramp);
        m_backRightMotor.setRampRate(ramp);
    }

    public void drive(Vector2 input) {
        m_frontLeftMotor.set(input.y - input.x);
        m_backLeftMotor.set(input.y - input.x);
        m_frontRightMotor.set(input.y + input.x);
        m_backRightMotor.set(input.y + input.x);
    }

    
    public void drive(Vector3 input) {
        drive((Vector2) input);
    }
}
