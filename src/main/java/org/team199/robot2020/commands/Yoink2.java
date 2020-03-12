/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.team199.robot2020.commands;

import org.team199.robot2020.subsystems.Feeder2;
import org.team199.robot2020.subsystems.Intake2;

import edu.wpi.first.wpilibj2.command.InstantCommand;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class Yoink2 extends InstantCommand {
  private Intake2 intake2;
  private Feeder2 feeder2;

  public Yoink2(Intake2 intake2, Feeder2 feeder2) {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(this.intake2 = intake2, this.feeder2 = feeder2);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    if (intake2.isRunning()) {
      intake2.stop();
      feeder2.stop();
    } else {
      intake2.intake();
      feeder2.funnel();
    }
  }
}
