package org.team199.lib;

import com.revrobotics.CANAnalog;
import com.revrobotics.CANDigitalInput;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANPIDController.AccelStrategy;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

public class DummySparkMaxAnswer extends CANErrorAnswer {

    private static final long serialVersionUID = 2284848703213263465L;
    public static final CANErrorAnswer canErrorAnswer = new CANErrorAnswer();
    public static final CANDigitalInput canDigitalInput = Mockito.mock(CANDigitalInput.class, Mockito.RETURNS_SMART_NULLS);
    public static final CANPIDController canPIDController = Mockito.mock(CANPIDController.class, new CANErrorAnswer() {
        private static final long serialVersionUID = 558452215206948125L;
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            if(invocation.getMethod().getReturnType() == AccelStrategy.class) {
                return AccelStrategy.kTrapezoidal;
            }
            return super.answer(invocation);
        }
    });
    public static final CANEncoder canEncoder = Mockito.mock(CANEncoder.class, canErrorAnswer);
    public static final CANAnalog canAnalog = Mockito.mock(CANAnalog.class, canErrorAnswer);

    @Override
    public Object answer(InvocationOnMock invocation) throws Throwable {
        Class<?> returnType = invocation.getMethod().getReturnType();
        if(returnType == CANDigitalInput.class) {
            return canDigitalInput;
        } else if(returnType == CANPIDController.class) {
            return canPIDController;
        } else if(returnType == MotorType.class) {
            return MotorType.kBrushless;
        } else if(returnType == IdleMode.class) {
            return IdleMode.kBrake;
        } else if(returnType == CANEncoder.class) {
            return canEncoder;
        } else if(returnType == CANAnalog.class) {
            return canAnalog;
        }
        return super.answer(invocation);
    }

}