package org.team199.robot2020.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;

import org.team199.robot2020.subsystems.Feeder1;
import org.team199.robot2020.subsystems.Intake1;

public class Shoot1 extends CommandBase {
    private final Feeder1 feeder1;
    private final Intake1 intake1;
    private final double limitDistance = 10000;    // TODO: Figure out the correct value.

    public Shoot1(Feeder1 feeder1, Intake1 intake1) {
        this.feeder1 = feeder1;
        this.intake1 = intake1;
        addRequirements(feeder1);
    }

    public void initialize() {
        feeder1.eject();
    }

    public void execute() {
        if (feeder1.getCellPosition() > limitDistance) intake1.outtake();
        else intake1.stop();
    }

    public boolean isFinished() {
        return false; // wait until flywheel is up to speed 
    }

    public void end(boolean interrupted) {
        feeder1.stop();
        feeder1.reset();
    }
}