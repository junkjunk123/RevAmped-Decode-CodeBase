package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped.Components.DecodeColor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Intake.IntakeColor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.Scheduler;

public class TurnTable {
    private final Scheduler scheduler;
    private HashMap <TurnTableState,Color> tableMap = new HashMap<>();

    public record Servos(TableServo turntable, Popper popper) {
        public void update() {
            turntable.update();
            popper.update();
        }
    }

    public final Servos servos;
    public final IntakeColor intakeColor;
    //private Globals.AllianceColor allianceColor;

    public enum Color implements DecodeColor.DetectionColor {
        PURPLE(IntakeColor.minPurpleHue,IntakeColor.maxPurpleHue),
        GREEN(IntakeColor.minGreenHue, IntakeColor.maxGreenHue),
        NEUTRAL(0,255);

        @Override
        public float getMinHue() {
            return minHue;
        }

        @Override
        public float getMaxHue() {
            return maxHue;
        }
        float minHue;
        float maxHue;

        Color(float minHue, float maxHue){
            this.minHue = minHue;
            this.maxHue = maxHue;
        }
    }
    public enum TurnTableState {
        BALL1((servos,scheduler) -> {
            servos.turntable.setServoState(TableServo.TableState.BALL1);
        }),
        BALL2((servos,scheduler) -> {
            servos.turntable.setServoState(TableServo.TableState.BALL2);
        }),
        BALL3((servos,scheduler) -> {
            servos.turntable.setServoState(TableServo.TableState.BALL3);
        }),
        POP((servos,scheduler) -> {
            servos.popper.setCurrentState(Popper.PopperState.POP);
            scheduler.add(new Delay(500,() -> servos.popper().setCurrentState(Popper.PopperState.NEUTRAL)));
        });
        private BiConsumer<Servos, Scheduler> initialize;

        TurnTableState(BiConsumer<Servos, Scheduler> initialize){this.initialize = initialize;}

        public void initialize(Servos servos, Scheduler scheduler){
            initialize.accept(servos, scheduler);
        }
        }
    private TurnTableState currentState;

    public TurnTable(Scheduler scheduler, boolean setPositions, HardwareMap hardwareMap){
        this.scheduler = scheduler;
        TableServo turntable = new TableServo(hardwareMap, setPositions ? TableServo.TableState.BALL1 : TableServo.TableState.REST);
        Popper popper = new Popper(hardwareMap,"",setPositions);
        intakeColor = new IntakeColor(hardwareMap,"",Color.NEUTRAL);
        servos = new Servos(turntable,popper);
        currentState = TurnTableState.BALL1;
    }
     public void setCurrentState(TurnTableState currentState){
        this.currentState = currentState;
        currentState.initialize(servos,scheduler);
     }
     public void setCurrentState(TurnTableState currentState, boolean init){
        this.currentState = currentState;
        if (init) currentState.initialize(servos, scheduler);
     }

     public TurnTableState getCurrentState(){
        return currentState;
    }

    public void update(){
        servos.update();
    }

    public void addToMap(TurnTableState currentState){ //updates the hashmap based on the current table pos and color from color sensor
        tableMap.put(currentState, intakeColor.getColor());
    }
    public void removeFromMap(TurnTableState currentState){
        tableMap.remove(currentState);
    }
    public void PreparePurple(){
        for (Map.Entry<TurnTableState,Color> entry : tableMap.entrySet()){
            if (entry.getValue() == Color.PURPLE){
                setCurrentState(entry.getKey());
            }
        }
    }
    public void PrepareGreen(){
        for (Map.Entry<TurnTableState,Color> entry : tableMap.entrySet()){
            if (entry.getValue() == Color.GREEN){
                setCurrentState(entry.getKey());
            }
        }
    }
}