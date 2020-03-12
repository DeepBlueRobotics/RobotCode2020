package org.team199.robot2020.subsystems;

import com.revrobotics.CANSparkMax;

import org.team199.lib.MotorControllerFactory;
import org.team199.robot2020.Constants;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import org.team199.robot2020.subsystems.Feeder2;

public class Intake2 extends SubsystemBase {
    private static double kIntakeSpeed = .5;
    public static double kTimeToDeploy = 2;

    private final CANSparkMax rollerMotor = MotorControllerFactory.createSparkMax(Constants.Drive.kIntakeRoller);
    private final DoubleSolenoid intakePistons1 = new DoubleSolenoid(Constants.Drive.kIntakePistons[0], Constants.Drive.kIntakePistons[1]);
    private final DoubleSolenoid intakePistons2 = new DoubleSolenoid(Constants.Drive.kIntakePistons[2], Constants.Drive.kIntakePistons[3]);

    private boolean deployed = false;
    private boolean running = false;

    /**
     * Vectored intake that rolls balls through the bumper gap and into feeder.
     */
    public Intake2() {
        rollerMotor.setInverted(false);
        rollerMotor.setSmartCurrentLimit(40);

        SmartDashboard.putNumber("Intake.kIntakeSpeed", kIntakeSpeed);
    }

    public void periodic() {
        kIntakeSpeed = SmartDashboard.getNumber("Intake.kIntakeSpeed", kIntakeSpeed);
    }

    public void intake() {
        rollerMotor.set(kIntakeSpeed);
        running = true;
    }

    public void outtake() {
        rollerMotor.set(-kIntakeSpeed);
        running = true;
    }

    public void stop() {
        rollerMotor.set(0);
        running = false;
    }

    public void deploy() {
        intakePistons1.set(DoubleSolenoid.Value.kReverse);
        intakePistons2.set(DoubleSolenoid.Value.kForward);
        deployed = true;
    }

    public void retract() {
        intakePistons1.set(DoubleSolenoid.Value.kForward);
        intakePistons2.set(DoubleSolenoid.Value.kReverse);
        deployed = false;
    }

    public void toggle() {
        if (deployed) {
            retract();
        } else {
            deploy();
        }
    }

    public boolean isDeployed() { return deployed; }
    public boolean isRunning() { return running; }
}