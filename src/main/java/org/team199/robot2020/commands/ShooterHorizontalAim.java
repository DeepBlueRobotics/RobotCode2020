package org.team199.robot2020.commands;

import org.team199.robot2020.subsystems.Drivetrain;
import org.team199.robot2020.subsystems.Drivetrain.Side;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.lib.Limelight;

public class ShooterHorizontalAim extends CommandBase {
    private final Limelight limelight;
    private final Drivetrain drivetrain;
    private final double txRange = 2.0;     // TODO: Determine correct txRange
    private final double adjustmentRange = 0.1;     // TODO: Determine correct txRange
    private final double turnMultiplier = 0.6;
    private double adjustment = 0.0;
    public ShooterHorizontalAim(Drivetrain drivetrain, Limelight limelight){
        this.drivetrain = drivetrain;
        this.limelight = limelight;
        addRequirements(drivetrain);
    }

    public void execute() {
        adjustment = limelight.steeringAssist(drivetrain.getHeading());
        adjustment *= turnMultiplier;
        drivetrain.tankDrive(adjustment, -adjustment, false);
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.tankDrive(0, 0, false);
    }

    public boolean isFinished() {
        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0.0);
        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0.0);
        double speed = Math.abs(drivetrain.getEncRate(Side.LEFT)) + Math.abs(drivetrain.getEncRate(Side.RIGHT));
        speed /= 2;
        return (Math.abs(tx) < txRange) && tv == 1.0 && (speed < adjustmentRange);
    }
}