package optics

import optics.objects.Interactive
import optics.objects.OpticalRectangle

abstract class InteractiveOpticalRectangle(
    x: Double,
    y: Double, width: Double,
    height: Double
) : OpticalRectangle(x, y, width, height), Interactive<InteractiveOpticalRectangle>
