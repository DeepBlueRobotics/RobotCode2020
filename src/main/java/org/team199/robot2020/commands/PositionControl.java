package org.team199.robot2020.commands;

import org.team199.lib.ColorMatcher;
import org.team199.robot2020.subsystems.Dialer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj.DriverStation;

public class PositionControl extends CommandBase {
    private final Dialer dialer;
    private final ColorMatcher colorMatcher;
    private String gameData;
    private String targetColor;

    public PositionControl(Dialer dialer) {
        this.dialer = dialer;
        this.colorMatcher = new ColorMatcher();
        addRequirements(dialer);
    }

    public void initialize() {
        dialer.toggle();
        gameData = DriverStation.getInstance().getGameSpecificMessage();
        if(gameData.length() > 0) {
            switch (gameData.charAt(0)) {
                case 'B' :
                    targetColor = "Blue";
                break;
                case 'G' :
                    targetColor = "Green";
                break;
                case 'R' :
                    targetColor = "Red";
                break;
                case 'Y' :
                    targetColor = "Yellow";
                break;
                default :
                    //This is corrupt data
                break;
            }
        } else {
            //Code for no data received yet
        }
    }

    public void execute() {
        dialer.setSpeed(1);
    }

    public boolean isFinished() {
        return colorMatcher.getColorString().equals(targetColor) || !dialer.isDeployed();
    }

    public void end(boolean interrupted) {
        dialer.setSpeed(0);
    }

}