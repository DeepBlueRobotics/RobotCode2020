package org.team199.robot2020.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;

import org.team199.robot2020.subsystems.Feeder;
import org.team199.robot2020.subsystems.Shooter;

public class Shoot extends CommandBase {
    private final Shooter shooter;
    private final Feeder feeder;

    public Shoot(Shooter shooter, Feeder feeder) {
        this.shooter = shooter;
        this.feeder = feeder;
        addRequirements(feeder);
    }

    public void execute() {
        if(shooter.isAtTargetSpeed()) {
            feeder.shoot();
        } else {
            feeder.stop();
        }
    }

    public boolean isFinished() {
        return false; // wait until flywheel is up to speed 
    }

    public void end(boolean interrupted) {
        feeder.stop();
    }
}
