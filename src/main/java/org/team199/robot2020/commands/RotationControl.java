package org.team199.robot2020.commands;

import org.team199.lib.ColorMatcher;
import org.team199.robot2020.subsystems.Dialer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class RotationControl extends CommandBase {
    private final Dialer dialer;
    private final ColorMatcher colorMatcher;

    String startColor;
    int colorChangeCounter = 0; //an 8th of a rotation (1 color change)

    public RotationControl(Dialer dialer) {
        this.dialer = dialer;
        this.colorMatcher = new ColorMatcher();
        addRequirements(dialer);
    }

    public void initialize() {
        dialer.toggle();
        startColor = colorMatcher.getColorString();
    }

    public void execute() {
        dialer.setSpeed(1);
        if (!colorMatcher.getColorString().equals(startColor)) {
            colorChangeCounter += 1;
            startColor = colorMatcher.getColorString();
        }
    }

    public boolean isFinished() {
        return colorChangeCounter == 32;
    }

    public void end(boolean interrupted) {
        dialer.setSpeed(0);
    }

}