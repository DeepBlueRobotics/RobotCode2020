package org.team199.robot2020.commands;

import org.team199.robot2020.commands.ShooterHorizontalAim.SpinDirection;
import org.team199.robot2020.subsystems.Drivetrain;
import org.team199.robot2020.subsystems.Feeder;
import org.team199.robot2020.subsystems.Intake;
import org.team199.robot2020.subsystems.Shooter;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.lib.Limelight;
import frc.robot.lib.LinearInterpolation;
import frc.robot.lib.path.RobotPath;

public class AutoShootAndDrive extends SequentialCommandGroup {
    public AutoShootAndDrive(Drivetrain drivetrain, Intake intake, Feeder feeder, Shooter shooter, 
                             Limelight lime, RobotPath path, RobotPath reversePath, ShooterHorizontalAim.SpinDirection spinDirection, LinearInterpolation linearInterpol, Translation2d target) {
        addRequirements(drivetrain, intake, feeder, shooter);
        
        addCommands(
            new InstantCommand(path::initializeDrivetrainPosition),
            new ShooterHorizontalAim(drivetrain, lime, spinDirection),
            // new InstantCommand(() -> { 
            //     SmartDashboard.putNumber("Shooter.kTargetSpeed", linearInterpol.calculate(drivetrain.getOdometry().getPoseMeters().getTranslation().getDistance(target))); 
            // }),
            new AutoShoot(feeder, shooter),
            new AutoShoot(feeder, shooter),
            new AutoShoot(feeder, shooter),
            // new ShooterHorizontalAim(drivetrain, lime),
            new InstantCommand(() -> {
                intake.intake();
                intake.deploy();
            }, intake),
            path.getPathCommand(true, true),
            reversePath.getPathCommand(true, true),
            new ShooterHorizontalAim(drivetrain, lime, SpinDirection.COUNTERCLOCKWISE),
            // new InstantCommand(() -> { 
            //     SmartDashboard.putNumber("Shooter.kTargetSpeed", linearInterpol.calculate(drivetrain.getOdometry().getPoseMeters().getTranslation().getDistance(target))); 
            // }),
            new AutoShoot(feeder, shooter),
            new AutoShoot(feeder, shooter),
            new AutoShoot(feeder, shooter),
            new InstantCommand(() -> {
                intake.retract();
                intake.stop();
            }, intake)
        );
    }
}