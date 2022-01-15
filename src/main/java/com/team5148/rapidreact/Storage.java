package com.team5148.rapidreact;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Storage {
    CANSparkMax storageMotor = new CANSparkMax(7, MotorType.kBrushless);
    CANSparkMax intakeMotor = new CANSparkMax(8,MotorType.kBrushless);

    public void storage(double speed){
        storageMotor.set(speed);
        
    }
    public void intake(double speed){
        
        intakeMotor.set(speed);
    } 
}
