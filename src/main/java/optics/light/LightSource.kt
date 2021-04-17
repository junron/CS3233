package optics.light

import optics.InteractiveOpticalRectangle
import optics.objects.OpticalRectangle
import utils.OpticsList
import java.util.concurrent.CompletableFuture

interface LightSource {
    fun renderRays(objects: OpticsList<InteractiveOpticalRectangle>): CompletableFuture<*>
    fun removeAllLines()
}
