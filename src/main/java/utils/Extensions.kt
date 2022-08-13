package utils


fun String.toIPV4OrNull(): IPV4? {
    val nullableParts = this.split(".").map {
        it.trim().toUIntOrNull()
    }

    if (nullableParts.size != 4 || nullableParts.any { it == null || it > 255U }) {
        return null
    }

    val parts = nullableParts.filterNotNull()

    return IPV4(parts.reduceRightIndexed { index, i, acc ->
        acc + (i shl ((3 - index) * 8))
    })
}

fun String.toIPV4() = toIPV4OrNull()!!
