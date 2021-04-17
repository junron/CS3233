package serialize

import application.Storage.opticsTabController
import application.Storage.rayTabController
import javafx.scene.layout.Pane
import javafx.scene.shape.Line
import optics.InteractiveOpticalRectangle
import optics.PreciseJavaFXLine
import optics.light.Ray
import optics.objects.Mirror
import optics.objects.OpticalRectangle
import optics.objects.Refract
import optics.objects.Wall

object Deserialize {
    fun deserialize(obj: String, parent: Pane): Serializable? {
        return when (obj[0]) {
            'm' -> {
                val m = Mirror(0.0, 0.0, 0.0, 0.0, parent, 0.0)
                m.deserialize(obj)
                m
            }
            'w' -> {
                val w = Wall(0.0, 0.0, 0.0, 0.0, parent, 0.0)
                w.deserialize(obj)
                w
            }
            'e' -> {
                val re = Refract(0.0, 0.0, 0.0, 0.0, parent, 0.0, 1.0)
                re.deserialize(obj)
                re
            }
            'r' -> {
                return Ray.deserialize(obj, parent)
            }
            else -> null
        }
    }

    fun deserializeAndAdd(obj: String, parent: Pane) {
        val serializable = deserialize(obj, parent)
        if (serializable is Ray) {
            rayTabController?.createRay(serializable)
        } else if (serializable is InteractiveOpticalRectangle) {
            opticsTabController?.addObject(serializable,
                parent)
        }
    }
}
