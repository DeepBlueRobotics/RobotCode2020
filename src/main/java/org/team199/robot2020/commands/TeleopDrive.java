/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.team199.robot2020.commands;

import org.team199.robot2020.Constants;
import org.team199.robot2020.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class TeleopDrive extends CommandBase {
  private Drivetrain drivetrain;
  private Joystick leftJoy, rightJoy;

  /**
   * Creates a new TeleopDrive.
   */
  public TeleopDrive(Drivetrain drivetrain, Joystick leftJoy, Joystick rightJoy) {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(this.drivetrain = drivetrain);
    this.leftJoy = leftJoy;
    this.rightJoy = rightJoy;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double speed = -leftJoy.getY();
    double rotation = rightJoy.getX();

    if (leftJoy.getRawButton(Constants.OI.LeftJoy.SLOW_DRIVE_BUTTON)) {
      speed *= Constants.SLOW_DRIVE_SPEED;
    }

    if (rightJoy.getRawButton(Constants.OI.RightJoy.SLOW_DRIVE_BUTTON)) {
      rotation *= Constants.SLOW_DRIVE_ROTATION;
    }

    drivetrain.arcadeDrive(speed, rotation);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    drivetrain.arcadeDrive(0, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
