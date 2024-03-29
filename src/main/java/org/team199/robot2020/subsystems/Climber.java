package org.team199.robot2020.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;

import org.team199.robot2020.Constants;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.lib.MotorControllerFactory;

public class Climber extends SubsystemBase {
    private static final double kLiftConversionFactor = 43.98 / 256; // TODO: confirm numbers (inches)
    private static final double kWinchConversionFactor = 26.39 / 256; // TODO: confirm numbers (inches)

    // TODO: find good values and then set to final
    public static double kLiftDeploySpeed = 0.9; // TODO: set correct speed
    public static double kWinchDeploySpeed = 0.5; // TODO: set correct speed
    public static double kLiftKeepSpeed = 0.2; // TODO: set correct speed
    public static double kLiftRetractSpeed = -0.5; // TODO: set correct speed
    public static double kWinchRetractSpeed = 0.8; // TODO: set correct speed
    public static double kLiftAdjustSpeed = 0.1; // TODO: set correct speed
    
    public static final double kLiftHeight = 18.8;
    public static final double kWinchEndHeight = 76.2;
    public static final double kWinchStartHeight = -90;

    private final CANSparkMax liftMotor = MotorControllerFactory.createSparkMax(Constants.Drive.kClimberLift);
    private final CANEncoder liftEncoder = liftMotor.getEncoder();
    private final CANSparkMax winchMotor = MotorControllerFactory.createSparkMax(Constants.Drive.kClimberWinch);
    private final CANEncoder winchEncoder = winchMotor.getEncoder();

    public Climber(){
        liftEncoder.setPositionConversionFactor(kLiftConversionFactor);
        winchEncoder.setPositionConversionFactor(kWinchConversionFactor);
        winchEncoder.setPosition(kWinchStartHeight);

        SmartDashboard.putNumber("Climber.kLiftDeploySpeed", kLiftDeploySpeed);
        SmartDashboard.putNumber("Climber.kWinchDeploySpeed", kWinchDeploySpeed);
        SmartDashboard.putNumber("Climber.kLiftKeepSpeed", kLiftKeepSpeed);
        SmartDashboard.putNumber("Climber.kLiftRetractSpeed", kLiftRetractSpeed);
        SmartDashboard.putNumber("Climber.kWinchRetractSpeed", kWinchRetractSpeed);
        SmartDashboard.putNumber("Climber.kLiftAdjustSpeed", kLiftAdjustSpeed);
    }

    public void periodic()  {
        kLiftDeploySpeed = SmartDashboard.getNumber("Climber.kLiftDeploySpeed", kLiftDeploySpeed);
        kWinchDeploySpeed = SmartDashboard.getNumber("Climber.kWinchDeploySpeed", kWinchDeploySpeed);
        kLiftKeepSpeed = SmartDashboard.getNumber("Climber.kLiftKeepSpeed", kLiftKeepSpeed);
        kLiftRetractSpeed = SmartDashboard.getNumber("Climber.kLiftRetractSpeed", kLiftRetractSpeed);
        kWinchRetractSpeed = SmartDashboard.getNumber("Climber.kWinchRetractSpeed", kWinchRetractSpeed);
        kLiftAdjustSpeed = SmartDashboard.getNumber("Climber.kLiftAdjustSpeed", kLiftAdjustSpeed);
    }

    public void runLift(double speed) {
        liftMotor.set(speed);
    }

    public void runWinch(double speed) {
        winchMotor.set(Math.abs(speed));
    }

    public double getLiftHeight() {
        return liftEncoder.getPosition();
    }

    public double getWinchHeight() {
        return winchEncoder.getPosition();
    }
}