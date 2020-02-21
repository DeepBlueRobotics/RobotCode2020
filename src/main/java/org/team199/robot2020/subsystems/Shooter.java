package org.team199.robot2020.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;

import com.revrobotics.CANPIDController;

import com.revrobotics.CANEncoder;

import org.team199.lib.MotorControllerFactory;
import org.team199.lib.logging.Log;
import org.team199.robot2020.Constants;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.wpilibj.SpeedController;
//import java.lang.AutoCloseable;

public class Shooter extends SubsystemBase {
    private static double kV = 0.002062;
    private static double kS = 0.105;
    private static final double kP = 0.00007;
    private static final double kI = 0.0000004;
    private static final double kD = 0.005;
    private static double iZone = 50;

    private double kTargetSpeed = 4200;

    private final CANSparkMax master = MotorControllerFactory.createSparkMax(Constants.Drive.kShooterMaster);
    private final CANSparkMax slave = MotorControllerFactory.createSparkMax(Constants.Drive.kShooterSlave);
    private final CANPIDController pidController = master.getPIDController();

    private final CANEncoder masterEncoder = master.getEncoder();
    private final CANEncoder slaveEncoder = slave.getEncoder();

    public Shooter() {
        master.setSmartCurrentLimit(40);
        slave.setSmartCurrentLimit(40);
        SmartDashboard.putNumber("Shooter.kTargetSpeed", kTargetSpeed);
        SmartDashboard.putNumber("Shooter.kP", kP);
        SmartDashboard.putNumber("Shooter.kI", kI);
        SmartDashboard.putNumber("I Zone", iZone);
        SmartDashboard.putNumber("Shooter.kD", kD);
        SmartDashboard.putNumber("Shooter.kV", kV);
        SmartDashboard.putNumber("Shooter.kS", kS);
        
        slave.follow(master, true);
        master.setInverted(true);
        Log.registerDoubleVar("Spark Max Port 2 Speed", () -> masterEncoder.getVelocity());
        Log.registerDoubleVar("Spark Max Port 4 Speed", () -> slaveEncoder.getVelocity());
    }

    public void periodic()  {
        double p = SmartDashboard.getNumber("Shooter.kP", kP);
        double i = SmartDashboard.getNumber("Shooter.kI", kI);
        double zone = SmartDashboard.getNumber("I Zone", iZone);
        double d = SmartDashboard.getNumber("Shooter.kD", kD);

        kV = SmartDashboard.getNumber("Shooter.kV", kV);
        kS = SmartDashboard.getNumber("Shooter.kS", kS);
        iZone = SmartDashboard.getNumber("I Zone", iZone);
        setSpeed(SmartDashboard.getNumber("Shooter.kTargetSpeed", kTargetSpeed));

        if (p != pidController.getP()) pidController.setP(p);
        if (i != pidController.getI()) pidController.setI(i);   
        if (d != pidController.getD()) pidController.setD(d);
        if (zone != pidController.getIZone()) pidController.setIZone(zone);
        pidController.setReference(getTargetSpeed(), ControlType.kVelocity, 0, calculateFeedForward(getTargetSpeed()));

        SmartDashboard.putNumber("Speed Spark Max Port 2", masterEncoder.getVelocity());
        SmartDashboard.putNumber("Speed Spark Max Port 4", slaveEncoder.getVelocity());
    }

    public void setSpeed(double speed) {
        kTargetSpeed = speed;
    }

    public double getTargetSpeed() {
        return kTargetSpeed;
    }

    public double calculateFeedForward(double velocity) {
        return kS * Math.signum(velocity) + kV * velocity;
    }
}