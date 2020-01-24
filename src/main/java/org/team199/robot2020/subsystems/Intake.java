package org.team199.robot2020.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import org.team199.lib.MotorControllerFactory;
import org.team199.robot2020.Constants;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {
    private final WPI_TalonSRX intakeMotor = MotorControllerFactory.createTalon(Constants.Intake.INTAKE_MOTOR);
    private final DoubleSolenoid intakePistons = new DoubleSolenoid(Constants.Intake.INTAKE_PISTONS[0], Constants.Intake.INTAKE_PISTONS[1]);
    private final WPI_TalonSRX feederMotor = MotorControllerFactory.createTalon(Constants.Intake.FEEDER_MOTOR);

    public Intake(){
    }

    public void intake()
    {
        intakeMotor.set(Constants.Intake.INTAKE_SPEED);
        feederMotor.set(Constants.Intake.FEEDER_SPEED);
        intakePistons.set(DoubleSolenoid.Value.kForward); //TODO: Check if this should be kForward or kReverse
    }

    public void outtake()
    {
        intakeMotor.set(-Constants.Intake.INTAKE_SPEED);
        feederMotor.set(-Constants.Intake.FEEDER_SPEED);
        intakePistons.set(DoubleSolenoid.Value.kForward); //TODO: Check if this should be kForward or kReverse
    }
    
    public void stopIntake()
    {
        intakeMotor.set(0);
        feederMotor.set(0);
        intakePistons.set(DoubleSolenoid.Value.kReverse); //TODO: Check if this should be kForward or kReverse
    }
}