package org.team5148.lib.drivetrains;

import org.team5148.lib.motors.CANSparkMax;
import org.team5148.lib.motors.MotorController;
import org.team5148.lib.util.Vector2;
import org.team5148.lib.util.Vector3;

/**
 * Controls a tank drivetrain
 */
public class Tank extends Drivetrain {
    public MotorController m_frontLeftMotor;
    public MotorController m_frontRightMotor;
    public MotorController m_backLeftMotor;
    public MotorController m_backRightMotor;

    /**
     * Initializes a tank drivetrain using 4 CANSparkMaxs
     */
    public Tank() {
        this(
            new CANSparkMax(1),
            new CANSparkMax(2),
            new CANSparkMax(3),
            new CANSparkMax(4)
        );
    }

    /**
     * Initializes a tank drivetrain using 4 motor controllers
     * @param frontLeftMotor - Front Left Motor
     * @param frontRightMotor - Front Right Motor
     * @param backLeftMotor - Back Left Motor
     * @param backRightMotor - Back Right Motor
     */
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
