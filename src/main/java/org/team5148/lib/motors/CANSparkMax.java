package org.team5148.lib.motors;

/**
 * SparkMax wrapper that decreases CAN bus traffic
 */
public class CANSparkMax extends com.revrobotics.CANSparkMax implements MotorController {
    private double m_lastSpeed = Double.NaN;

    /**
     * Creates a brushless Spark Max
     * @param id - CAN ID of the Spark Max
     */
    public CANSparkMax(int id) {
        super(id, MotorType.kBrushless);
    }

    /**
     * Creates a brushed or brushless Spark Max
     * @param id - CAN ID of the Spark Max
     * @param motorType - Brush or Brushless type
     */
    public CANSparkMax(int id, MotorType motorType) {
        super(id, motorType);
    }

    @Override
    public void set(double speed) {
        if (speed != m_lastSpeed) {
            m_lastSpeed = speed;
            super.set(speed);
        }
    }

    public void setRampRate(double ramp) {
        this.setOpenLoopRampRate(ramp);
    }
}
