package utils

private val privateSubnets = listOf(
    "192.168.0.0".toIPV4() / (16U.toInt()),
    "172.16.0.0".toIPV4() / (12U.toInt()),
    "10.0.0.0".toIPV4() / (8U.toInt()),
)

val loopback = "127.0.0.0".toIPV4()/(8U.toInt())
val linkLocal = "169.254.0.0".toIPV4()/(8U.toInt())

fun UInt.isPrivateIP() = privateSubnets.any { this in it }

