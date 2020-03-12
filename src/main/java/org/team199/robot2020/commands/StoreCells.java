/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.team199.robot2020.commands;

import org.team199.robot2020.subsystems.Feeder2;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class StoreCells extends CommandBase {
  private Feeder2 feeder2;
  private Timer timer = new Timer();
  private double kStoreDelay = 1;

  /**
   * Creates a new StoreCells.
   */
  public StoreCells(Feeder2 feeder2) {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(this.feeder2 = feeder2);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    timer.reset();
    timer.start();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (feeder2.isCellEntering() && !feeder2.isCellAtShooter()) {
      if (timer.get() > kStoreDelay) {
        feeder2.hop();
      }
    } else {
      feeder2.stopHopper();
      timer.reset();
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
