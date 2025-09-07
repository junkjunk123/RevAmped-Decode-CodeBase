package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.Scheduler;

public class Turntable {
    private final Scheduler scheduler;
    private HashMap <TableState,Color> tableMap = new HashMap<>();

    public record Servos(TableServo turntable, Popper popper) {
        public void update() {
            turntable.update();
            popper.update();
        }
    }

    public final Servos servos;
    //private Globals.AllianceColor allianceColor;

    public enum Color {
        PURPLE,
        GREEN;
    }
    public enum TableState {
        BALL1((servos,scheduler) -> {
            servos.turntable.setCurrentState(TableServo.TableState.BALL1);
        }),
        BALL2((servos,scheduler) -> {
            servos.turntable.setCurrentState(TableServo.TableState.BALL2);
        }),
        BALL3((servos,scheduler) -> {
            servos.turntable.setCurrentState(TableServo.TableState.BALL3);
        }),
        POP((servos,scheduler) -> {
            servos.popper.setCurrentState(Popper.PopperState.POP);
            scheduler.add(new Delay(500,() -> servos.popper().setCurrentState(Popper.PopperState.NEUTRAL)));
        });
        private BiConsumer<Servos, Scheduler> initialize;

        TableState(BiConsumer<Servos, Scheduler> initialize){this.initialize = initialize;}

        public void initialize(Servos servos, Scheduler scheduler){
            initialize.accept(servos, scheduler);
        }
        }
    private TableState currentState;

    public Turntable(Scheduler scheduler, HardwareMap hardwareMap){
        this.scheduler = scheduler;
        TableServo turntable = new TableServo(hardwareMap);
        Popper popper = new Popper(hardwareMap,"",true);
        servos = new Servos(turntable,popper);
        currentState = TableState.BALL1;
    }
     public void setCurrentState(TableState currentState){
        this.currentState = currentState;
        currentState.initialize(servos,scheduler);
     }
     public void setCurrentState(TableState currentState, boolean init){
        this.currentState = currentState;
        if (init) currentState.initialize(servos, scheduler);
     }

     public TableState getCurrentState(){
        return currentState;
    }

    public void update(){
        servos.update();
    }

    public void addToMap(TableState currentState){ //updates the hashmap based on the current table pos and color from color sensor
    //to add element to hashmap, use .put()
    }
    public void removeFromMap(TableState currentState){
        tableMap.remove(currentState);
    }
    public void PreparePurple(){
        for (Map.Entry<TableState,Color> entry : tableMap.entrySet()){
            if (entry.getValue() == Color.PURPLE){
                setCurrentState(entry.getKey());
            }
        }
    }
    public void PrepareGreen(){
        for (Map.Entry<TableState,Color> entry : tableMap.entrySet()){
            if (entry.getValue() == Color.GREEN){
                setCurrentState(entry.getKey());
            }
        }
    }
}