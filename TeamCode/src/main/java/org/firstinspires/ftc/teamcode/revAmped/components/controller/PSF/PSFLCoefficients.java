package org.firstinspires.ftc.teamcode.revAmped.components.controller.PSF;

import org.firstinspires.ftc.teamcode.revAmped.components.controller.SlideController;

public record PSFLCoefficients(double P, double K_static, double K_V, double L) implements SlideController.SlideConstants {
}
