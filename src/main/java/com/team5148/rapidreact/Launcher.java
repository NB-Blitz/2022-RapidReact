package com.team5148.rapidreact;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Launcher {
    CANSparkMax topMotor = new CANSparkMax(5, MotorType.kBrushless);
    CANSparkMax bottomMotor = new CANSparkMax(6,MotorType.kBrushless);



public void rev(double speed){
    topMotor.set(speed);
    bottomMotor.set(speed);
}
}
