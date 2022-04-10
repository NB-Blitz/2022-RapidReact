package org.team5148.lib.drivetrains;

import org.team5148.lib.motors.CANSparkMax;
import org.team5148.lib.motors.MotorController;
import org.team5148.lib.util.Vector2;
import org.team5148.lib.util.Vector3;

/**
 * Controls a mecanum drivetrain
 */
public class Mecanum extends Drivetrain {
    public MotorController m_frontLeftMotor;
    public MotorController m_frontRightMotor;
    public MotorController m_backLeftMotor;
    public MotorController m_backRightMotor;

    /**
     * Initializes a mecanum drivetrain using 4 CANSparkMaxs
     */
    public Mecanum() {
        this(
            new CANSparkMax(1),
            new CANSparkMax(2),
            new CANSparkMax(3),
            new CANSparkMax(4)
        );
    }

    /**
     * Initializes a mecanum drivetrain using 4 motor controllers
     * @param frontLeftMotor - Front Left Motor
     * @param frontRightMotor - Front Right Motor
     * @param backLeftMotor - Back Left Motor
     * @param backRightMotor - Back Right Motor
     */
    public Mecanum(
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
        drive(new Vector3(
            0,
            input.y,
            input.x
        ));
    }

    public void drive(Vector3 input) {
        double denominator = Math.max(Math.abs(input.x) + Math.abs(input.y) + Math.abs(input.z), 1);

        double frontLeft = (input.x + input.y + input.z) / denominator;
        double frontRight = (-input.x + input.y - input.z) / denominator;
        double backLeft = (-input.x + input.y + input.z) / denominator;
        double backRight = (input.x + input.y - input.z) / denominator;

        m_frontLeftMotor.set(frontLeft);
        m_frontRightMotor.set(frontRight);
        m_backLeftMotor.set(backLeft);
        m_backRightMotor.set(backRight);
    }
}
