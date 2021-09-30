package org.team199.robot2020.commands;

import org.team199.robot2020.subsystems.Feeder;
import org.team199.robot2020.subsystems.Shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

public class AutoShoot extends SequentialCommandGroup {
    public AutoShoot(Feeder feeder, Shooter shooter) {
        addRequirements(feeder, shooter);
        addCommands(
            new WaitUntilCommand(shooter::isAtTargetSpeed),
            new ParallelRaceGroup(
                new Shoot(shooter, feeder),
                new WaitUntilCommand(() -> !shooter.isAtTargetSpeed()),
                new WaitCommand(2)
            ),
            new InstantCommand(() -> SmartDashboard.putNumber("Shooter Shot Speed", SmartDashboard.getNumber("Speed Spark Max Port 2", 0.0)))
        );
    }
}