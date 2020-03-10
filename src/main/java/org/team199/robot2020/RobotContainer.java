
/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.team199.robot2020;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.Trajectory.State;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;

import java.util.List;

import org.team199.lib.Limelight;
import org.team199.lib.LinearInterpolation;
import org.team199.lib.RobotPath;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import org.team199.robot2020.commands.Regurgitate;
import org.team199.robot2020.commands.TeleopDrive;
import org.team199.robot2020.commands.Shoot;
import org.team199.robot2020.commands.ShooterHorizontalAim;
import org.team199.robot2020.subsystems.Drivetrain;
import org.team199.robot2020.subsystems.Shooter;
import org.team199.robot2020.commands.AdjustClimber;
import org.team199.robot2020.commands.AutoShootAndDrive;
import org.team199.robot2020.commands.DeployClimber;
import org.team199.robot2020.commands.RaiseRobot;
import org.team199.robot2020.subsystems.Feeder;
import org.team199.robot2020.subsystems.Intake;
import org.team199.robot2020.subsystems.Climber;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class RobotContainer {
    private final DigitalInput autoSwitch1 = new DigitalInput(Constants.Ports.kAutoPathSwitch1Port);
    private final DigitalInput autoSwitch2 = new DigitalInput(Constants.Ports.kAutoPathSwitch2Port);
    private final DigitalInput autoSwitch3 = new DigitalInput(Constants.Ports.kAutoPathSwitch3Port);
    private final Drivetrain drivetrain = new Drivetrain();
    private final Limelight lime = new Limelight();
    private final Intake intake = new Intake();
    private final Feeder feeder = new Feeder();
    private final Joystick leftJoy = new Joystick(Constants.OI.LeftJoy.kPort);
    private final Joystick rightJoy = new Joystick(Constants.OI.RightJoy.kPort);
    private final Joystick controller = new Joystick(Constants.OI.Controller.kPort);
    private final Climber climber = new Climber();
    private final RobotPath[][] paths;
    private final LinearInterpolation linearInterpol = new LinearInterpolation("ShooterData.csv");
    private final boolean isBlue = DriverStation.getInstance().getAlliance() == DriverStation.Alliance.Blue;
    private final Shooter shooter = new Shooter(drivetrain, lime, linearInterpol, isBlue ? Constants.FieldPositions.BLUE_PORT : Constants.FieldPositions.RED_PORT);
    private final PowerDistributionPanel pdp = new PowerDistributionPanel(Constants.Ports.kPDPCanID);

    private boolean encoderReset = false, has5 = false;
    private double targetEncoderDist = 100.0;   // TODO: Figure out the correct value.
    /**
     * Creates a new {@link RobotContainer} and initialized joysticks and default commands
     */
    public RobotContainer() {
        if(DriverStation.getInstance().getJoystickName(0).length() != 0) {
            configureButtonBindingsLeftJoy();
        } else{
            System.err.println("ERROR: Dude, you're missing the left joystick.");
        }

        if(DriverStation.getInstance().getJoystickName(1).length() != 0) {
            configureButtonBindingsRightJoy();
        } else{
            System.err.println("ERROR: Dude, you're missing the right joystick.");
        }

        if(DriverStation.getInstance().getJoystickName(2).length() != 0) {
            configureButtonBindingsController();
        } else{
            System.err.println("ERROR: Dude, you're missing the controller.");
        }

        shooter.setDefaultCommand(setName("Shooter Default Command", new RunCommand(()-> shooter.setSpeed(shooter.getTargetSpeed()), shooter)));
        drivetrain.setDefaultCommand(new TeleopDrive(drivetrain, leftJoy, rightJoy, lime));
        
        feeder.setDefaultCommand(setName("Feeder Default Command", new RunCommand(() -> {
            if (feeder.isCellEntering() && !feeder.isCellAtShooter()) {
                feeder.runForward();
            } else {
                feeder.stop();
            }
        }, feeder)));

        // Old Intake default command:
        /*
        intake.setDefaultCommand(new RunCommand(() -> {
            if(intake.isDeployed()) {
                if(feeder.has5Intake()) {
                    intake.stop();
                } else if(feeder.isIntakeCellEntering()) {
                    intake.slow();
                } else {
                    intake.intake();
                }
            } else {
                intake.stop();
            }
        }, intake));
        */
        intake.setDefaultCommand(setName("Intake Default Commadn", new RunCommand(() -> {
            if(intake.isDeployed()) {
                if(feeder.isIntakeCellEntering()) {
                    if (!feeder.isCellAtShooter()) {
                        has5 = false;
                        intake.reverse();
                    } else {
                        has5 = true;
                    }
                } else if(!has5) {
                    intake.intake();
                } else {
                    intake.stop();
                }
            } else {
                intake.stop();
            }
        }, intake)));

        paths = new RobotPath[7][10];
        loadDPaths(RobotPath.Path.PATH1, new String[] {"TRLeft", "TrenchRun", "TrenchRun"}, new boolean[] {false, false, true}, Constants.FieldPositions.RED_LEFT.pos, Constants.FieldPositions.BLUE_LEFT.pos);
        loadDPaths(RobotPath.Path.PATH2, new String[] {"TRCenter", "TrenchRun", "TrenchRun"}, new boolean[] {false, false, true}, Constants.FieldPositions.RED_CENTER.pos, Constants.FieldPositions.BLUE_CENTER.pos);
        loadDPaths(RobotPath.Path.PATH3, new String[] {"TRRight", "TrenchRun", "TrenchRun"}, new boolean[] {false, false, true}, Constants.FieldPositions.RED_RIGHT.pos, Constants.FieldPositions.BLUE_RIGHT.pos);
        loadDPaths(RobotPath.Path.PATH4, new String[] {"GS1", "GS2", "GS3"}, false, Constants.FieldPositions.RED_GS.pos, Constants.FieldPositions.BLUE_GS.pos);
    }

    private void configureButtonBindingsLeftJoy() {
        // Arcade/Tank drive button
        new JoystickButton(leftJoy, Constants.OI.LeftJoy.kToggleDriveModeButton).whenPressed(new InstantCommand(
                () -> SmartDashboard.putBoolean("Arcade Drive", !SmartDashboard.getBoolean("Arcade Drive", false))));

        // characterize drive button
        
        // Toggle Characterize Drive                
        new JoystickButton(leftJoy, Constants.OI.LeftJoy.kCharacterizedDriveButton).whenPressed(new InstantCommand(
                () -> SmartDashboard.putBoolean("Characterized Drive", !SmartDashboard.getBoolean("Characterized Drive", false))));
    }

    private void configureButtonBindingsRightJoy() {
        // Align the robot and then shoots
        new JoystickButton(rightJoy, Constants.OI.RightJoy.kToggleBreakModeButton).whenPressed(new InstantCommand(drivetrain::toggleBreakMode, drivetrain));
        new JoystickButton(rightJoy, Constants.OI.RightJoy.kAlignAndShootButton).whileHeld(setName("Align and Shoot", new SequentialCommandGroup(new ShooterHorizontalAim(drivetrain, lime), new Shoot(feeder, intake))));
        new JoystickButton(rightJoy, Constants.OI.RightJoy.kShootButton).whileHeld(new Shoot(feeder, intake));
    }

    private void configureButtonBindingsController() {
        // Intake toggle button
        new JoystickButton(controller, Constants.OI.Controller.kIntakeButton).whenPressed(new InstantCommand(() -> {
            if (intake.isDeployed()) {
                intake.retract();
                intake.stop();
            } else {
                intake.doTheFlop();
                intake.intake();
            }
        }, intake));

        // Power cell regurgitate button
        new JoystickButton(controller, Constants.OI.Controller.kRegurgitateButton).whileHeld(new Regurgitate(intake, feeder));

        // Deploy climber button and allow for adjustment
        new JoystickButton(controller, Constants.OI.Controller.kDeployClimberButton).whenPressed(setName("Deploy Climber", new SequentialCommandGroup(
            new DeployClimber(climber),
            new AdjustClimber(climber, controller)
        )));

        // climb button
        new JoystickButton(controller, Constants.OI.Controller.kRaiseRobotButton).whenPressed(new RaiseRobot(climber));
    }

    /**
     * @return The {@link Drivetrain} of the robot
     */
    final Drivetrain getDrivetrain() {
        return drivetrain;
    }

    /**
     * @return The {@link Command} to run in autonomous
     * @see Robot#autonomousInit()
     */
    public Command getAutonomousCommand() {
        try {
            final RobotPath.Path path = getPath();
            final RobotPath[] paths = this.paths[path.idx];
            if(paths.length == 0) {
                throw new Exception();
            }
            return new AutoShootAndDrive(drivetrain, intake, feeder, 
                                         shooter, lime, 
                                         (isBlue ? Constants.FieldPositions.BLUE_PORT.pos : Constants.FieldPositions.RED_PORT.pos),
                                         pdp, Constants.Ports.kFeederBeltPDP, paths, path);
        } catch(final Exception e) {
            return new InstantCommand();
        }
    }

    
    /*
     * DIO Port 0 = Switch 1
     * DIO Port 1 = Switch 2
     * DIO Port 2 = Switch 3
     * on  = jumper in
     * off = jumper out
     * Red/Blue determined by DS
     * Switch states
     * 1    2
     * off off off = off
     * on off off  = 1
     * off on off  = 2
     * on on off   = 3
     * off off on  = off
     * on off on   = 4
     * off on on   = 5
     * on on on    = 6
     */
    /**
     * @return The {@link RobotPath.Path} to run in autonomous
     * @see Robot#autonomousInit()
     */
    public RobotPath.Path getPath() {
        // get() returns true if the circuit is open.
        int b1 = autoSwitch1.get() ? 0 : 1;
        int b2 = autoSwitch2.get() ? 0 : 1;
        int b3 = autoSwitch3.get() ? 0 : 1;
        return RobotPath.Path.fromIdx((b1 | b2 << 1 | b3 << 2)-1);
    }

    /**
     * Loads paths into the <code>paths</code> array based on the team's alliance and the {@link RobotPath.Path}
     * @param path The {@link RobotPath.Path} to load into
     * @param pathNames The names of the paths to load
     * @param isInverted Whether the paths should be inverted
     * @param redInitPos The initial position of the path if the team is on the red alliance
     * @param blueInitPos The initial position of the path if the team is on the blue alliance
     * @see #loadDPaths(org.team199.lib.RobotPath.Path, String[], boolean, Translation2d, Translation2d)
     * @see #loadDPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, Translation2d, int)
     * @see #loadDPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, Translation2d)
     * @see #loadPaths(org.team199.lib.RobotPath.Path, String[], boolean[], Translation2d)
     * @see #loadPaths(org.team199.lib.RobotPath.Path, String[], boolean, Translation2d)
     * @see #loadPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, int)
     * @see #loadPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d)
     * @see RobotPath#RobotPath(String, Drivetrain, boolean, Translation2d)
     */
    private void loadDPaths(RobotPath.Path path, String[] pathNames, boolean[] isInverted, Translation2d redInitPos, Translation2d blueInitPos) {
        loadPaths(path, pathNames, isInverted, DriverStation.getInstance().getAlliance() == DriverStation.Alliance.Blue ? blueInitPos : redInitPos);
    }

    /**
     * Loads paths into the <code>paths</code> array starting at index <code>[{@link RobotPath.Path#idx}, 0]</code> based on the team's alliance
     * @param path The {@link RobotPath.Path} to load into
     * @param pathNames The names of the paths to load
     * @param isInverted Whether the paths should be inverted
     * @param redInitPos The initial position of the path if the team is on the red alliance
     * @param blueInitPos The initial position of the path if the team is on the blue alliance
     * @see #loadDPaths(org.team199.lib.RobotPath.Path, String[], boolean[], Translation2d, Translation2d)
     * @see #loadDPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, Translation2d, int)
     * @see #loadDPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, Translation2d)
     * @see #loadPaths(org.team199.lib.RobotPath.Path, String[], boolean[], Translation2d)
     * @see #loadPaths(org.team199.lib.RobotPath.Path, String[], boolean, Translation2d)
     * @see #loadPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, int)
     * @see #loadPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d)
     * @see RobotPath#RobotPath(String, Drivetrain, boolean, Translation2d)
     */
    private void loadDPaths(RobotPath.Path path, String[] pathNames, boolean isInverted, Translation2d redInitPos, Translation2d blueInitPos) {
        loadPaths(path, pathNames, isInverted, DriverStation.getInstance().getAlliance() == DriverStation.Alliance.Blue ? blueInitPos : redInitPos);
    }

    /**
     * Loads paths into the <code>paths</code> array starting at index <code>[{@link RobotPath.Path#idx}, 0]</code>
     * @param path The {@link RobotPath.Path} to load into
     * @param pathNames The names of the paths to load
     * @param isInverted Whether the paths should be inverted
     * @param initPos The initial position of the path
     * @see #loadDPaths(org.team199.lib.RobotPath.Path, String[], boolean[], Translation2d, Translation2d)
     * @see #loadDPaths(org.team199.lib.RobotPath.Path, String[], boolean, Translation2d, Translation2d)
     * @see #loadDPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, Translation2d, int)
     * @see #loadDPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, Translation2d)
     * @see #loadPaths(org.team199.lib.RobotPath.Path, String[], boolean, Translation2d)
     * @see #loadPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, int)
     * @see #loadPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d)
     * @see RobotPath#RobotPath(String, Drivetrain, boolean, Translation2d)
     */
    private void loadPaths(RobotPath.Path path, String[] pathNames, boolean[] isInverted, Translation2d initPos) {
        boolean invalidLength = false;
        if(isInverted.length < pathNames.length) {
            invalidLength = true;
            System.err.println("Invalid inversion parameters for path: " + path.name());
            if(isInverted.length == 0) {
                isInverted = new boolean[] {true};
            }
        }
        for(int i = 0; i < pathNames.length; i++) {
            if(invalidLength) {
                loadPath(path, pathNames[i], isInverted[0], initPos, i);
            } else {
                loadPath(path, pathNames[i], isInverted[i], initPos, i);
            }
            Trajectory trajectory = paths[path.idx][i].getTrajectory();
            List<State> states = trajectory.getStates();
            initPos = states.get(states.size()-1).poseMeters.getTranslation();
        }
    }

    
    /**
     * Loads paths into the <code>paths</code> array starting at index <code>[{@link RobotPath.Path#idx}, 0]</code>
     * @param path The {@link RobotPath.Path} to load into
     * @param pathNames The names of the paths to load
     * @param isInverted Whether the paths should be inverted
     * @param initPos The initial position of the path
     * @see #loadDPaths(org.team199.lib.RobotPath.Path, String[], boolean[], Translation2d, Translation2d)
     * @see #loadDPaths(org.team199.lib.RobotPath.Path, String[], boolean, Translation2d, Translation2d)
     * @see #loadDPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, Translation2d, int)
     * @see #loadDPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, Translation2d)
     * @see #loadPaths(org.team199.lib.RobotPath.Path, String[], boolean, Translation2d)
     * @see #loadPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, int)
     * @see #loadPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d)
     * @see RobotPath#RobotPath(String, Drivetrain, boolean, Translation2d)
     */
    private void loadPaths(final RobotPath.Path path, final String[] pathNames, final boolean isInverted, Translation2d initPos) {
        for(int i = 0; i < pathNames.length; i++) {
            loadPath(path, pathNames[i], isInverted, initPos, i);
            Trajectory trajectory = paths[path.idx][i].getTrajectory();
            List<State> states = trajectory.getStates();
            initPos = states.get(states.size()-1).poseMeters.getTranslation();
        }
    }

    /**
     * Loads a path into the <code>paths</code> array at index <code>[{@link RobotPath.Path#idx}, 0]</code> based on the team's alliance
     * @param path The {@link RobotPath.Path} to load into
     * @param pathNames The name of the path to load
     * @param isInverted Whether the paths should be inverted
     * @param redInitPos The initial position of the path if the team is on the red alliance
     * @param blueInitPos The initial position of the path if the team is on the blue alliance
     * @see #loadDPaths(org.team199.lib.RobotPath.Path, String[], boolean[], Translation2d, Translation2d)
     * @see #loadDPaths(org.team199.lib.RobotPath.Path, String[], boolean, Translation2d, Translation2d)
     * @see #loadDPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, Translation2d, int)
     * @see #loadPaths(org.team199.lib.RobotPath.Path, String[], boolean[], Translation2d)
     * @see #loadPaths(org.team199.lib.RobotPath.Path, String[], boolean, Translation2d)
     * @see #loadPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, int)
     * @see #loadPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d)
     * @see RobotPath#RobotPath(String, Drivetrain, boolean, Translation2d)
     */
    private void loadDPath(RobotPath.Path path, String pathName, boolean isInverted, Translation2d redInitPos, Translation2d blueInitPos) {
        loadDPath(path, pathName, isInverted, redInitPos, blueInitPos, 0);
    }

    /**
     * Loads a path into the <code>paths</code> array at index <code>[{@link RobotPath.Path#idx}, idx]</code> based on the team's alliance
     * @param path The {@link RobotPath.Path} to load into
     * @param pathNames The name of the path to load
     * @param isInverted Whether the paths should be inverted
     * @param redInitPos The initial position of the path if the team is on the red alliance
     * @param blueInitPos The initial position of the path if the team is on the blue alliance
     * @param idx The index in which to load the path in the 2d array
     * @see #loadDPaths(org.team199.lib.RobotPath.Path, String[], boolean[], Translation2d, Translation2d)
     * @see #loadDPaths(org.team199.lib.RobotPath.Path, String[], boolean, Translation2d, Translation2d)
     * @see #loadDPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, Translation2d)
     * @see #loadPaths(org.team199.lib.RobotPath.Path, String[], boolean[], Translation2d)
     * @see #loadPaths(org.team199.lib.RobotPath.Path, String[], boolean, Translation2d)
     * @see #loadPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, int)
     * @see #loadPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d)
     * @see RobotPath#RobotPath(String, Drivetrain, boolean, Translation2d)
     */
    private void loadDPath(RobotPath.Path path, String pathName, boolean isInverted, Translation2d redInitPos, Translation2d blueInitPos, int idx) {
        loadPath(path, pathName, isInverted, DriverStation.getInstance().getAlliance() == DriverStation.Alliance.Blue ? blueInitPos : redInitPos, idx);
    }

    /**
     * Loads a path into the <code>paths</code> array starting at index <code>[{@link RobotPath.Path#idx}, 0]</code>
     * @param path The {@link RobotPath.Path} to load into
     * @param pathName The names of the paths to load
     * @param isInverted Whether the paths should be inverted
     * @param initPos The initial position of the path
     * @see #loadDPaths(org.team199.lib.RobotPath.Path, String[], boolean[], Translation2d, Translation2d)
     * @see #loadDPaths(org.team199.lib.RobotPath.Path, String[], boolean, Translation2d, Translation2d)
     * @see #loadDPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, Translation2d, int)
     * @see #loadDPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, Translation2d)
     * @see #loadPaths(org.team199.lib.RobotPath.Path, String[], boolean[], Translation2d)
     * @see #loadPaths(org.team199.lib.RobotPath.Path, String[], boolean, Translation2d)
     * @see #loadPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, int)
     * @see RobotPath#RobotPath(String, Drivetrain, boolean, Translation2d)
     */
    private void loadPath(final RobotPath.Path path, final String pathName, final boolean isInverted, final Translation2d initPos) {
        loadPath(path, pathName, isInverted, initPos, 0);
    }

    /**
     * Loads a path into the <code>paths</code> array at index <code>[{@link RobotPath.Path#idx}, idx]</code>
     * @param path The {@link RobotPath.Path} to load into
     * @param pathName The names of the paths to load
     * @param isInverted Whether the paths should be inverted
     * @param initPos The initial position of the path
     * @param idx The index in which to load the path in the 2d array
     * @see #loadDPaths(org.team199.lib.RobotPath.Path, String[], boolean[], Translation2d, Translation2d)
     * @see #loadDPaths(org.team199.lib.RobotPath.Path, String[], boolean, Translation2d, Translation2d)
     * @see #loadDPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, Translation2d, int)
     * @see #loadDPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d, Translation2d)
     * @see #loadPaths(org.team199.lib.RobotPath.Path, String[], boolean[], Translation2d)
     * @see #loadPaths(org.team199.lib.RobotPath.Path, String[], boolean, Translation2d)
     * @see #loadPath(org.team199.lib.RobotPath.Path, String, boolean, Translation2d)
     * @see RobotPath#RobotPath(String, Drivetrain, boolean, Translation2d)
     */
    private void loadPath(final RobotPath.Path path, final String pathName, final boolean isInverted, final Translation2d initPos, int idx) {
        try {
            paths[path.idx][idx] = new RobotPath(pathName, drivetrain, isInverted, initPos);
        } catch(final Exception e) {
            System.err.println("Error Occured Loading Path: [" + path.name() + "," + pathName + "]");
            e.printStackTrace(System.err);
        }
    }

    /**
     * Sets the name of a command
     * @param name The name to set
     * @param command The command of which to set the name
     * @return <code>command</code>
     */
    public static CommandBase setName(String name, CommandBase command) {
        command.setName(name);
        return command;
    }
}