package frc.robot.lib;

import java.io.IOException;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.SpeedController;
import frc.robot.subsystems.Drivetrain;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.PathfinderFRC;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;

public class RobotPath {
    
    private Trajectory leftTrajectory, rightTrajectory;
    private Drivetrain dt;
    private Encoder leftEncoder, rightEncoder;
    private AnalogGyro gyro;
    private EncoderFollower leftEncoderFollower, rightEncoderFollower;
    private Notifier notifier;
    private boolean isInit;

    public RobotPath(String pathName) throws IOException {
        leftTrajectory = PathfinderFRC.getTrajectory(pathName + ".left");
        leftTrajectory = PathfinderFRC.getTrajectory(pathName + ".right");
        isInit = false;
    }

    public void init(Drivetrain dt, Encoder leftEncoder, Encoder rightEncoder, AnalogGyro gyro, int ticksPerRev, double wheelDiameter, double maxVelocity) {
        if(isInit) {
            return;
        }
        this.dt = dt;
        this.leftEncoder = leftEncoder;
        this.rightEncoder = rightEncoder;
        this.gyro = gyro;
        leftEncoderFollower = new EncoderFollower(leftTrajectory);
        leftEncoderFollower.configureEncoder(leftEncoder.get(), ticksPerRev, wheelDiameter);
        leftEncoderFollower.configurePIDVA(1.0, 0.0, 0.0, 1 / maxVelocity, 0);
        rightEncoderFollower = new EncoderFollower(rightTrajectory);
        rightEncoderFollower.configureEncoder(rightEncoder.get(), ticksPerRev, wheelDiameter);
        rightEncoderFollower.configurePIDVA(1.0, 0.0, 0.0, 1 / maxVelocity, 0);
        isInit = true;
    }

    public void start() {
        if(!isInit) {
            return;
        }
        notifier = new Notifier(this::followPath);
        notifier.startPeriodic(leftTrajectory.get(0).dt);
    }

    public void stop() {
        if(!isInit) {
            return;
        }
        notifier.stop();
    }

    private void followPath() {
        if(leftEncoderFollower.isFinished() || rightEncoderFollower.isFinished()) {
            notifier.stop();
        } else {
            double leftSpeed = leftEncoderFollower.calculate(leftEncoder.get());
            double rightSpeed = rightEncoderFollower.calculate(rightEncoder.get());
            double heading = gyro.getAngle();
            double desiredHeading = Pathfinder.r2d(leftEncoderFollower.getHeading());
            double headingDifference = Pathfinder.boundHalfDegrees(desiredHeading - heading);
            double turn =  0.8 * (-1.0/80.0) * headingDifference;
            double[] charParams = dt.characterizedDrive(leftSpeed + turn, rightSpeed - turn);
            dt.drive(charParams[0], charParams[1]);
        }
    }

}