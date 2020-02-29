package org.team199.robot2020.commands;

import org.team199.lib.ColorMatcher;
import org.team199.robot2020.subsystems.Dialer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class PositionControl extends CommandBase {
    private final Dialer dialer;
    private final ColorMatcher colorMatcher;

    public PositionControl(Dialer dialer) {
        this.dialer = dialer;
        this.colorMatcher = new ColorMatcher();
        addRequirements(dialer);
    }

    public void initialize() {
        dialer.toggle();
    }

    public void execute() {
        dialer.setSpeed(1);
    }

    public boolean isFinished() {
        return colorMatcher.getColorString().equals("Blue") || !dialer.isDeployed(); //TODO write code to get info from the thing
    }

    public void end(boolean interrupted) {
        dialer.setSpeed(0);
    }

}