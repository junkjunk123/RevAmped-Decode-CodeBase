package org.firstinspires.ftc.teamcode.revAmped.components.controller.PSF;

import org.firstinspires.ftc.teamcode.revAmped.components.controller.SlideController;

public record PSFCoefficients(double P, double K_static, double K_V) implements SlideController.SlideConstants {
}
