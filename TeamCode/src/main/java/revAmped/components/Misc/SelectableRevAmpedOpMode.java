package revAmped.components.Misc;

import com.pedropathing.telemetry.SelectScope;
import com.pedropathing.telemetry.Selector;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class SelectableRevAmpedOpMode extends RevAmpedOpMode {
    private final Selector<Supplier<RevAmpedOpMode>> selector;
    private RevAmpedOpMode selectedOpMode;
    private final static String[] MESSAGE = {
            "Use the d-pad to move the cursor.",
            "Press right bumper to select.",
            "Press left bumper to go back."
    };

    public SelectableRevAmpedOpMode(String name, Consumer<SelectScope<Supplier<RevAmpedOpMode>>> opModes) {
        selector = Selector.create(name, opModes, MESSAGE);
        selector.onSelect(opModeSupplier -> {
            onSelect();
            selectedOpMode = opModeSupplier.get();
            selectedOpMode.gamepad1 = gamepad1;
            selectedOpMode.gamepad2 = gamepad2;
            selectedOpMode.telemetry = telemetry;
            selectedOpMode.hardwareMap = hardwareMap;
            selectedOpMode.scheduler = scheduler;
            selectedOpMode.RevAmpedInit();
        });
    }

    protected void onSelect() {
    }

    protected void onLog(List<String> line) {
        telemetry.update();
    }

    @Override
    public void RevAmpedInit() {

    }

    @Override
    public final void RevAmpedInitLoop() {
        if (selectedOpMode == null) {
            if (gamepad1.dpadUpWasPressed() || gamepad2.dpadUpWasPressed())
                selector.decrementSelected();
            else if (gamepad1.dpadDownWasPressed() || gamepad2.dpadDownWasPressed())
                selector.incrementSelected();
            else if (gamepad1.rightBumperWasPressed() || gamepad2.rightBumperWasPressed())
                selector.select();
            else if (gamepad1.leftBumperWasPressed() || gamepad2.leftBumperWasPressed())
                selector.goBack();

            List<String> lines = selector.getLines();
            for (String line : lines) {
                telemetry.addLine(line);
            }
            onLog(lines);

        } else selectedOpMode.RevAmpedInitLoop();
    }

    @Override
    public final void RevAmpedStart() {
        if (selectedOpMode == null) throw new RuntimeException("No OpMode selected!");
        selectedOpMode.RevAmpedStart();
    }

    @Override
    public final void RevAmpedLoop() {
        selectedOpMode.RevAmpedLoop();
    }

    @Override
    public final void RevAmpedStop() {
        if (selectedOpMode != null) selectedOpMode.RevAmpedStop();
    }

    @Override
    public void update() {
        selectedOpMode.update();
    }
}
