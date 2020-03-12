package org.team199.robot2020.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;

import org.team199.robot2020.subsystems.Feeder2;
import org.team199.robot2020.subsystems.Intake2;

public class Shoot2 extends CommandBase {
    private final Feeder2 feeder2;
    private final Intake2 intake2;

    public Shoot2(Intake2 intake2, Feeder2 feeder2) {
        addRequirements(this.feeder2 = feeder2);
        addRequirements(this.intake2 = intake2);
    }

    public void initialize() {
        feeder2.shoot();
        intake2.deploy();
        intake2.intake();
    }

    public boolean isFinished() {
        return false; // wait until flywheel is up to speed 
    }

    public void end(boolean interrupted) {
        feeder2.stop();
        intake2.stop();
    }
}
