package utils

fun UInt.asIP() = "${(this shr 24) % 256U}.${(this shr 16) % 256U}.${(this shr 8) % 256U}.${this % 256U}"

operator fun UInt.div(other: Int) = this to other
operator fun Pair<UInt, Int>.contains(other: UInt) = (other shr (32 - this.second)) == (first shr (32 - this.second))

fun String.toIPV4OrNull(): UInt? {
    val nullableParts = this.split(".").map {
        it.trim().toUIntOrNull()
    }

    if (nullableParts.size != 4 || nullableParts.any { it == null || it > 255U }) {
        return null
    }

    val parts = nullableParts.filterNotNull()

    return parts.reduceRightIndexed { index, i, acc ->
        acc + (i shl ((3 - index) * 8))
    }
}

fun String.toIPV4() = toIPV4OrNull()!!
