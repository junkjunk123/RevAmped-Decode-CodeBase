package org.firstinspires.ftc.teamcode.UG.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.UG.Depo.Depo;
import org.firstinspires.ftc.teamcode.UG.Depo.FlywheelMotorLeft;
import org.firstinspires.ftc.teamcode.UG.Depo.FlywheelMotorRight;
import org.firstinspires.ftc.teamcode.UG.Intake.Intake;
import org.firstinspires.ftc.teamcode.UG.Intake.IntakeMotor;
import org.firstinspires.ftc.teamcode.UG.UGRobot;
import org.firstinspires.ftc.teamcode.UG.UltimateGoalConstants;

import java.util.List;

import revAmped.components.Architecture.TeleOpStateHandler;
import revAmped.components.CommandScheduler.Delay;
import revAmped.components.Drive.TeleOpDrivetrainHandler;
import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.MathUtil.FiniteFieldVector;
import revAmped.components.MathUtil.Vector;
import revAmped.components.Misc.Globals;
import revAmped.components.Misc.OpModeType;
import revAmped.components.Misc.RevAmpedOpMode;

@TeleOp
public class OutreachTeleOp extends RevAmpedOpMode {
    private UGRobot robot;
    private RevAmpedGamepad admin;
    private RevAmpedGamepad guest;
    public TeleOpStateHandler<CycleState> tsh;
    private TeleOpDrivetrainHandler drivetrain;
    public enum CycleState implements TeleOpStateHandler.State{
        SHOOT(Intake.IntakeState.NEUTRAL, Depo.DepoState.LAUNCH,new Vector(1,1,1)),
        INTAKE(Intake.IntakeState.INTAKE, Depo.DepoState.NEUTRAL,new Vector(1,1,1)),
        NEUTRAL(Intake.IntakeState.NEUTRAL, Depo.DepoState.NEUTRAL,new Vector(1,1,1));

        CycleState(Intake.IntakeState intakeState, Depo.DepoState depoState, Vector transitionVector){
            this.intakeState = intakeState;
            this.depoState = depoState;
            this.transitionVector = new FiniteFieldVector<>(FiniteFieldVector.binaryVector(), transitionVector.values());
        }

        private final FiniteFieldVector<FiniteFieldVector.BinaryVector> transitionVector;
        private final Intake.IntakeState intakeState;
        private final Depo.DepoState depoState;

        @Override
        public List<Enum<?>> componentStates(){return List.of(intakeState,depoState);}

        @Override
        public FiniteFieldVector<FiniteFieldVector.BinaryVector> getTransitionVector (){
            return transitionVector;
        }
    }
    @Override
    public void RevAmpedInit(){
        robot = new UGRobot(this, Globals.AllianceColor.None);
        admin = new RevAmpedGamepad(gamepad1);
        guest = new RevAmpedGamepad(gamepad2);
        tsh = new TeleOpStateHandler<>(CycleState.NEUTRAL, scheduler);
        drivetrain = new TeleOpDrivetrainHandler.TeleOpDrivetrainBuilder()
                .setOpMode(this)
                .setDirections(UltimateGoalConstants.DRIVE_DIRECTIONS)
                .setGamepad_1(admin)
                .build();
    }
    @Override
    public void RevAmpedLoop(){
        if(admin.a.isTrue()){
            tsh.runTransition(c -> {
                robot.flywheelLeft().setMotorState(FlywheelMotorLeft.FlywheelMotorState.MAX_POWER);
                robot.flywheelRight().setMotorState(FlywheelMotorRight.FlywheelMotorState.MAX_POWER);
                scheduler.add(new Delay(3000,() -> {
                    robot.topIntake().setMotorState(IntakeMotor.IntakeMotorState.INTAKE);
                    robot.bottomIntake().setMotorState(IntakeMotor.IntakeMotorState.INTAKE);
                    c.accept(true);
                }
                ));
            },CycleState.SHOOT,5000);
        } else {
            switch (tsh.currentState()){
                case SHOOT -> {
                    tsh.runTransition(c -> {
                        robot.flywheelLeft().setMotorState(FlywheelMotorLeft.FlywheelMotorState.STOPPED);
                        robot.flywheelRight().setMotorState(FlywheelMotorRight.FlywheelMotorState.STOPPED);
                        c.accept(true);
                    },CycleState.NEUTRAL,1000);
                }
            }
        }

        if(admin.b.isPress()){
            switch (tsh.currentState()){
                case NEUTRAL -> {
                    tsh.runTransition(c -> {
                        robot.topIntake().setMotorState(IntakeMotor.IntakeMotorState.INTAKE);
                        robot.bottomIntake().setMotorState(IntakeMotor.IntakeMotorState.INTAKE);
                        c.accept(true);
                    },CycleState.INTAKE,1000);
                }
                case SHOOT -> {
                    tsh.runTransition(c -> {
                        robot.flywheelRight().setMotorState(FlywheelMotorRight.FlywheelMotorState.STOPPED);
                        robot.flywheelLeft().setMotorState(FlywheelMotorLeft.FlywheelMotorState.STOPPED);
                        robot.topIntake().setMotorState(IntakeMotor.IntakeMotorState.INTAKE);
                        robot.bottomIntake().setMotorState(IntakeMotor.IntakeMotorState.INTAKE);
                        c.accept(true);
                    },CycleState.INTAKE,1000);
                }
                case INTAKE -> {
                    tsh.runTransition(c -> {
                        robot.topIntake().setMotorState(IntakeMotor.IntakeMotorState.STOPPED);
                        robot.bottomIntake().setMotorState(IntakeMotor.IntakeMotorState.STOPPED);
                        c.accept(true);
                    },CycleState.NEUTRAL,1000);
                }
            }
        }
    }
    @Override
    public void update(){
        robot.update();
        telemetry.update();
        admin.update();
        guest.update();
        drivetrain.updateLocalization();

    }

    @Override
    public OpModeType getOpModeType() {
        return UGRobot.OpModeType.TELEOP;
    }
}
