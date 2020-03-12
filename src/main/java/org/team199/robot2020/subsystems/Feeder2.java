/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.team199.robot2020.subsystems;

import com.playingwithfusion.TimeOfFlight;
import com.revrobotics.CANSparkMax;

import org.team199.lib.MotorControllerFactory;
import org.team199.robot2020.Constants;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Feeder2 extends SubsystemBase {
  private final CANSparkMax funnelMotor = MotorControllerFactory.createSparkMax(Constants.Drive.kFeederFunnel);
  private final CANSparkMax hopperMotor = MotorControllerFactory.createSparkMax(Constants.Drive.kFeederHopper);

  private final TimeOfFlight inSensor = new TimeOfFlight(Constants.Drive.kFeederInSensor);
  private final TimeOfFlight outSensor = new TimeOfFlight(Constants.Drive.kFeederOutSensor);

  private static double kFunnelSpeed = 1;
  private static double kHopperIntakeSpeed = .3;
  private static double kHopperShootSpeed =  1;
  private static double kInSensorDistance = 200;
  private static double kOutSensorDistance = 130;

  public Feeder2() {
    hopperMotor.setSmartCurrentLimit(30);
    funnelMotor.setSmartCurrentLimit(30);
    hopperMotor.setInverted(true);

    SmartDashboard.putNumber("Feeder2.kFunnelSpeed", kFunnelSpeed);
    SmartDashboard.putNumber("Feeder2.kHopperIntakeSpeed", kHopperIntakeSpeed);
    SmartDashboard.putNumber("Feeder2.kHopperShootSpeed", kHopperShootSpeed);
    SmartDashboard.putNumber("Feeder2.kInSensorDistance", kInSensorDistance);
    SmartDashboard.putNumber("Feeder2.kOutSensorDistance", kOutSensorDistance);
  }

  @Override
  public void periodic() {
    kFunnelSpeed = SmartDashboard.getNumber("Feeder2.kFunnelSpeed", kFunnelSpeed);
    kHopperIntakeSpeed = SmartDashboard.getNumber("Feeder2.kHopperIntakeSpeed", kHopperIntakeSpeed);
    kHopperShootSpeed = SmartDashboard.getNumber("Feeder2.kHopperShootSpeed", kHopperShootSpeed);
    kInSensorDistance = SmartDashboard.getNumber("Feeder2.kInSensorDistance", kInSensorDistance);
    kOutSensorDistance = SmartDashboard.getNumber("Feeder2.kOutSensorDistance", kOutSensorDistance);
  
    SmartDashboard.putNumber("Feeder2.currentInSensorDistance", inSensor.getRange());
    SmartDashboard.putNumber("Feeder2.currentOutSensorDistance", outSensor.getRange());
  }

  public boolean isCellEntering() {
    return inSensor.getRange() < kInSensorDistance;
  }

  public boolean isCellAtShooter() {
    return outSensor.getRange() < kOutSensorDistance;
  }

  public void funnel() {
    funnelMotor.set(kFunnelSpeed);
  }

  public void hop() {
    hopperMotor.set(kHopperIntakeSpeed);
  }

  public void outtake() {
    funnelMotor.set(-kFunnelSpeed);
    hopperMotor.set(-kHopperIntakeSpeed);
  }

  public void shoot() {
    funnelMotor.set(kFunnelSpeed);
    hopperMotor.set(kHopperShootSpeed);
  }

  public void stopHopper() {
    hopperMotor.set(0);
  }

  public void stop() {
    funnelMotor.set(0);
    hopperMotor.set(0);
  }
}
