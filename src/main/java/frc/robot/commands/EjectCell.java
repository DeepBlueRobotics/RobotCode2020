package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Shooter;

public class EjectCell extends CommandBase {
    Shooter shooter;

    public EjectCell(Shooter shooter) {
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
    public void end(boolean interrupted) {
        /*
        decrease ball count
        do wait time thing and then schedule another EjectCell if there are more power cells
        */
    }
}