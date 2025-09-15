package revAmped.components.Misc;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.Color;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.IntakeColor;
import org.firstinspires.ftc.teamcode.RevAmped.OpModes.Auto.DecodeAutonomousActionExecutor;
import org.firstinspires.ftc.teamcode.RevAmped.DecodeConstants;

import java.util.List;

import revAmped.components.Pedro.FollowerManager;

/**
 * Globals class to hold global variables and settings for the robot.
 * This class is used to store configuration settings such as alliance color,
 * testing mode, and telemetry instance.
 */
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
    public static AllianceColor allianceColor;

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
    }

    public static DecodeAutonomousActionExecutor aae;

    public static FollowerManager followerManager;

    public enum RandomizationState {
        PPG(23, List.of(Color.ArtifactColor.PURPLE, Color.ArtifactColor.PURPLE, Color.ArtifactColor.GREEN)),
        PGP(22, List.of(Color.ArtifactColor.PURPLE, Color.ArtifactColor.GREEN, Color.ArtifactColor.PURPLE)),
        GPP(21, List.of(Color.ArtifactColor.GREEN, Color.ArtifactColor.PURPLE, Color.ArtifactColor.PURPLE));

        final int ID;
        final List<Color.ArtifactColor> colors;

        RandomizationState(int tagID, List<Color.ArtifactColor> colors) {
            ID = tagID;
            this.colors = colors;
        }

        public int getID() {
            return ID;
        }

        public List<Color.ArtifactColor> getColors() {
            return colors;
        }
    }
    public static RandomizationState randomizationState;

    public static OpModeType OpModeType;
}
