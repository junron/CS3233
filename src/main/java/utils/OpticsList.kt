package utils

import optics.objects.Interactive
import java.util.*

class OpticsList<T : Interactive<T>> : ArrayList<T>() {

    fun getAllExcept(exclude: T): OpticsList<T> {
        val result: OpticsList<T> = OpticsList()
        for (elem in this) {
            if (elem != exclude) result.add(elem)
        }
        return result
    }

    fun deepClone(): OpticsList<T> {
        val result: OpticsList<T> = OpticsList()
        for (elem in this) {
            result.add(elem.cloneObject())
        }
        return result
    }
}
