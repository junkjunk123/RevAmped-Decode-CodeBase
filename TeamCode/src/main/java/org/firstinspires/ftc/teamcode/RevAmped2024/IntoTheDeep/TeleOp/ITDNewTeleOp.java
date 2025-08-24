package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepositionArm;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.TeleOp.NewTeleOpStateHandler.CycleState;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.TeleOp.NewTeleOpStateHandler.ButtonMapper;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.TeleOp.NewTeleOpStateHandler.StateDefiner;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.GyroHandler;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.SampleDepositionMethods;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.vSlideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.DriveTrainAndHang.HangMethods;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbar;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeMethods;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.AutoTeleopCommunication;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedGamepad;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedOpMode;
import java.util.HashMap;
import revAmped.components.CommandScheduler.Delay;
import revAmped.components.Misc.TeleOpAction;

public class ITDNewTeleOp extends ITDRevAmpedOpMode {
    private NewTeleOpStateHandler stateHandler = new NewTeleOpStateHandler();
    NewTeleOpStateHandler.CycleState cycleState = stateHandler.getCycleState();
    private RobotRevampedITD robot;
    private IntakeMethods intake;
    private SampleDepositionMethods sampleDepoMethods;
    private ITDRevAmpedGamepad gamepad_1;
    private ITDRevAmpedGamepad gamepad_2;
    private GyroHandler gyro;
    private HangMethods hang;
    private IntakeFourbar intakeFourbar;

    @Override
    public RobotRevampedITD.OpModeType getOpModeType() {
        return RobotRevampedITD.OpModeType.TELEOP;
    }

    @Override
    public void RevAmpedInit() {
        gamepad_1 = new ITDRevAmpedGamepad(gamepad1, 0.3f);
        gamepad_2 = new ITDRevAmpedGamepad(gamepad2, 0.1f);
        gamepad1.reset();
        gamepad2.reset();
        robot = new RobotRevampedITD(this, AutoTeleopCommunication.getAllianceColor(), AutoTeleopCommunication.getStartPose());
        gamepad_1.sendLEDS(robot.allianceColorToDetectionColor());
        gamepad_2.sendLEDS(robot.allianceColorToDetectionColor());
    }

    @Override
    public void RevAmpedLoop() {
        robot.drive.arcadeDrive(gamepad_1, false, 1, 0.1f, (float) Math.atan(Math.toRadians(15)), 1f, false);
        stateHandler.update();
    }

    @Override
    public void RevAmpedStart() {
        robot.init();
        gyro = new GyroHandler(robot);
        DepositionArm depositionArm = new DepositionArm(gyro, robot, scheduler.delays);
        intakeFourbar = new IntakeFourbar(robot, scheduler.delays);
        intake = new IntakeMethods(depositionArm, robot, scheduler.delays, gamepad_1, gamepad_2, intakeFourbar, telemetry);
        sampleDepoMethods = new SampleDepositionMethods(depositionArm, gyro, robot, scheduler.delays, gamepad_1, gamepad_2, intakeFourbar);
        hang = new HangMethods(robot, scheduler.delays);

        if (robot.vslides.getLimitSwitch().touchingUncached()) {
            cycleState = CycleState.SAMPLE_DEPO;
        }

        StateDefiner transitionFromNeutral = NEUTRAL -> {
            HashMap<ButtonMapper, CycleState> neutralTransitions = new HashMap<>();
            neutralTransitions.put(new ButtonMapper(gamepad_2.b::isPress, TeleOpAction.delaylessAction(intake::intakeDown, "1")), CycleState.INTAKE);
            neutralTransitions.put(new ButtonMapper(gamepad_2.dpad_up::isPress, TeleOpAction.delaylessAction(sampleDepoMethods::highBasketDepo, "2")), CycleState.SAMPLE_DEPO);
            return neutralTransitions;
        };

        StateDefiner transitionFromIntake = INTAKE -> {
            intake.update();
            HashMap<ButtonMapper, CycleState> intakeTransitions = new HashMap<>();
            intakeTransitions.put(new ButtonMapper(gamepad_2.y::isPress, TeleOpAction.delaylessAction(intake::transfer, "3")), CycleState.NEUTRAL);
            return intakeTransitions;
        };

        StateDefiner transitionFromIntakeFuture = INTAKE -> {
            HashMap<ButtonMapper, CycleState> futureIntakeTransitions = new HashMap<>();
            futureIntakeTransitions.put(new ButtonMapper(gamepad_2.dpad_up::isPress, TeleOpAction.delaylessAction(sampleDepoMethods::highBasketDepo, "4")), CycleState.SAMPLE_DEPO);
            return futureIntakeTransitions;
        };

        StateDefiner transitionFromSampleDepo = SAMPLE_DEPO -> {
            sampleDepoMethods.update();
            gyro.update();
            HashMap<ButtonMapper, CycleState> sampleDepoTransitions = new HashMap<>();
            sampleDepoTransitions.put(new ButtonMapper(gamepad_2.y::isPress, TeleOpAction.delaylessAction(sampleDepoMethods::collapseFromBasketDeposit, "5")), CycleState.NEUTRAL);
            sampleDepoTransitions.put(new ButtonMapper(gamepad_2.a::isPress, TeleOpAction.delaylessAction(robot.depoClaw::open, "6")), CycleState.SAMPLE_DEPO);
            return sampleDepoTransitions;
        };

        stateHandler.setStateMap(cycleState -> {
            if (cycleState == CycleState.NEUTRAL) return transitionFromNeutral;
            if (cycleState == CycleState.SAMPLE_DEPO) return transitionFromSampleDepo;
            if (cycleState == CycleState.INTAKE) return transitionFromIntake;
            return null;
        });

        stateHandler.setFutureStateMap(cycleState -> transitionFromIntakeFuture); //etc
        stateHandler.build(scheduler);
    }

    @Override
    public void update() {
        robot.update(gyro.getCurrentState() != GyroHandler.GyroState.UNACTIVATED);
        gamepad_1.update();
        gamepad_2.update();
    }
}
