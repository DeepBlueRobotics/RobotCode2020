package org.team199.robot2020.subsystems;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import org.team199.lib.MotorControllerFactory;
import org.team199.robot2020.Constants;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Dialer extends SubsystemBase {
    private boolean deployState = false;
    private final WPI_TalonSRX motor = MotorControllerFactory.createTalon(Constants.Drive.kFeederBelt);
    private final DoubleSolenoid dialerPiston = new DoubleSolenoid(Constants.Drive.kDialerPiston[0], Constants.Drive.kDialerPiston[1]);


    public Dialer () {

    }

    public void periodic () {

    }

    public void setSpeed(double speed) {
        if (isDeployed()) { 
            motor.set(speed);
        } else {
            motor.set(0);
        }
    }

    public void toggle() {
        if (!isDeployed()) {
            //deploy
            dialerPiston.set(DoubleSolenoid.Value.kForward);
            deployState = true;
        } else {
            //retract
            dialerPiston.set(DoubleSolenoid.Value.kReverse);
            deployState = false;
            motor.set(0);
        }
    }

    public boolean isDeployed() {
        return deployState;
    }
}