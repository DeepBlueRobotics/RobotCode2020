/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.team199.robot2020.commands;

import org.team199.robot2020.subsystems.Feeder2;
import org.team199.robot2020.subsystems.Intake2;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class Yeet2 extends CommandBase {
  private Intake2 intake2;
  private Feeder2 feeder2;

  /**
   * Creates a new Yoink2.
   */
  public Yeet2(Intake2 intake2, Feeder2 feeder2) {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(this.intake2 = intake2, this.feeder2 = feeder2);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    intake2.outtake();
    feeder2.outtake();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    intake2.stop();
    feeder2.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
