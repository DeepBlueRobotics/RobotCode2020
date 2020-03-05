package org.team199.robot2020.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;

import org.team199.robot2020.subsystems.Feeder2;

public class Shoot2 extends CommandBase {
    private final Feeder2 feeder2;

    public Shoot2(Feeder2 feeder2) {
        this.feeder2 = feeder2;
        addRequirements(feeder2);
    }

    public void initialize() {
        feeder2.shoot();
    }

    public boolean isFinished() {
        return false; // wait until flywheel is up to speed 
    }

    public void end(boolean interrupted) {
        feeder2.stop();
    }
}
