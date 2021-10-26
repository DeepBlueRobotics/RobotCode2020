package org.team199.robot2020.commands;

import org.team199.robot2020.subsystems.Drivetrain;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.lib.Limelight;

public class ShooterHorizontalAim extends CommandBase {
    private final Limelight limelight;
    private final Drivetrain drivetrain;
    private final double txRange = 0.5;     // TODO: Determine correct txRange
    private final SpinDirection spinDir;
    private double adjustment;

    public ShooterHorizontalAim(Drivetrain drivetrain, Limelight limelight, SpinDirection spinDir){
        this.drivetrain = drivetrain;
        this.limelight = limelight;
        this.spinDir = spinDir;
        addRequirements(drivetrain);
    }

    public void execute() {
        adjustment = limelight.steeringAssist();
        if(NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0.0) == 0.0 && spinDir == SpinDirection.COUNTERCLOCKWISE) {
            adjustment *= -1;
        }
        drivetrain.tankDrive(adjustment, -adjustment, false);
    }

    public boolean isFinished() {
        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0.0);
        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0.0);
        tx += 4;
        return (Math.abs(tx) < txRange) && tv == 1.0 && Math.abs(drivetrain.getEncRate(Drivetrain.Side.LEFT)) + Math.abs(drivetrain.getEncRate(Drivetrain.Side.RIGHT)) < 1;
    }

    public static enum SpinDirection {
        CLOCKWISE, COUNTERCLOCKWISE;
    }
}