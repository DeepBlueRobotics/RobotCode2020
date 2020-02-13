package org.team199.robot2020.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import org.team199.robot2020.subsystems.Shooter;

public class EjectOneCell extends CommandBase {
    private final Shooter shooter;

    public EjectOneCell(Shooter shooter) {
        this.shooter = shooter;
    }

    public void initialize() {
        // start timer
        
    }

    public void execute() {
        /*
        spin thing that puts ball into shooter
        */
    }

    public boolean isFinished() {
        return false;
        // When timer ends return true
    }

    public void end(boolean interrupted) {
        /*
        decrease ball count
        do wait time thing and then schedule another EjectCell if there are more power cells
        */
    }
}