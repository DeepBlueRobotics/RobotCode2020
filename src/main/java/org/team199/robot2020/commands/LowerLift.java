/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.team199.robot2020.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import org.team199.robot2020.subsystems.Climber;

public class LowerLift extends CommandBase {
  Climber climber;
  double speed;

  public LowerLift(Climber climber, double speed) {
    this.climber = climber;
    this.speed = speed;
  }
  
  @Override
  public void initialize() {     
  }

  @Override
  public void execute() {
    climber.lowerLift(speed);
    
  }

  @Override
  public boolean isFinished() {
    return climber.getLiftHeight() < 1;
  }
  
  @Override
  public void end(final boolean interrupted) {
    climber.stopLift();
  }
}
