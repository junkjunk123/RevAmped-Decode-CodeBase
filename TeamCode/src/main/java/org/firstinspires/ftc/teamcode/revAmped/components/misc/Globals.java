package org.firstinspires.ftc.teamcode.revAmped.components.misc;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.revAmped.DecodeConstants;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.FollowerManager;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.DecodeAutonomousActionExecutor;

import java.util.List;

/**
 * Globals class to hold global variables and settings for the robot.
 * This class is used to store configuration settings such as alliance color,
 * testing mode, and telemetry instance.
 */

@Config
public final class Globals {
    // Prevent instantiation

    private Globals() {
        throw new UnsupportedOperationException("Globals is a utility class and cannot be instantiated");
    }

    // Global variables
    public static boolean testing = false;
    public static boolean disableDrive = false;
    public static Telemetry telemetry;

    public static Constants constants = new DecodeConstants();

    /**
     * Enum representing the alliance color.
     * Used to determine the color of the alliance for the robot.
     */
    public enum AllianceColor {
        None(0),
        Blue(20),
        Red(24);

        final int tagID;
        AllianceColor(int tagID) {
            this.tagID = tagID;
        }

        public int getTagID() {
            return tagID;
        }
    }
    public static AllianceColor allianceColor = AllianceColor.None;

    /**
     * Initializes the telemetry instance.
     * This method should be called once at the start of the robot's operation. This is automatically called in a RevAmpedOpMode.
     * to set up the telemetry for logging and debugging.
     *
     * @param telemetry the Telemetry instance to use for logging
     */
    public static void init(Telemetry telemetry) {
        constants.build();
        Globals.telemetry = telemetry;
        Globals.aae = null;
        Globals.followerManager = null;
        Globals.testing = false;
        Globals.disableDrive = false;
    }

    public static DecodeAutonomousActionExecutor aae;

    public static FollowerManager followerManager;

    public enum RandomizationState {
        PPG(23, List.of(DecodeColorSensor.ArtifactColor.PURPLE, DecodeColorSensor.ArtifactColor.PURPLE, DecodeColorSensor.ArtifactColor.GREEN)),
        PGP(22, List.of(DecodeColorSensor.ArtifactColor.PURPLE, DecodeColorSensor.ArtifactColor.GREEN, DecodeColorSensor.ArtifactColor.PURPLE)),
        GPP(21, List.of(DecodeColorSensor.ArtifactColor.GREEN, DecodeColorSensor.ArtifactColor.PURPLE, DecodeColorSensor.ArtifactColor.PURPLE));

        final int ID;
        final List<DecodeColorSensor.ArtifactColor> colors;

        RandomizationState(int tagID, List<DecodeColorSensor.ArtifactColor> colors) {
            ID = tagID;
            this.colors = colors;
        }

        public int getID() {
            return ID;
        }

        public List<DecodeColorSensor.ArtifactColor> getColors() {
            return colors;
        }

        public int getGreenIndex() {
            switch (this) {
                case PGP -> {
                    return 1;
                }
                case PPG -> {
                    return 2;
                }
                default -> {
                    return 0;
                }
            }
        }
    }

    public static RandomizationState randomizationState;

    public static OpModeType OpModeType;
}
