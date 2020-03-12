package org.team199.robot2020.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;

import org.team199.lib.Limelight;
import org.team199.robot2020.subsystems.Drivetrain;

public class ShooterHorizontalAim extends CommandBase {
    private final Limelight limelight;
    private final Drivetrain drivetrain;

    public ShooterHorizontalAim(Drivetrain drivetrain, Limelight limelight) {
        this.drivetrain = drivetrain;
        this.limelight = limelight;
        addRequirements(drivetrain);
    }

    public void initialize() {
        limelight.stopSteer = false;
        limelight.setLight(true);
    }

    public void execute() {
        double adjustment = -limelight.steeringAssist(drivetrain.getOdometry().getPoseMeters());
        drivetrain.tankDrive(adjustment, -adjustment, false);
    }

    public boolean isFinished() {
        return limelight.stopSteer;
    }

    public void end(boolean interrupted) {
        System.out.println("I have ended");
        limelight.setLight(false);
    }
}