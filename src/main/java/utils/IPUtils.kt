package utils

@JvmInline
value class IPV4(val uintIp: UInt) {

    operator fun div(other: Int): Subnet = Subnet(this to other)
    fun isPrivateIP() = privateSubnets.any { this in it }

    override fun toString() =
        "${(this.uintIp shr 24) % 256U}.${(this.uintIp shr 16) % 256U}.${(this.uintIp shr 8) % 256U}.${this.uintIp % 256U}"
}

@JvmInline
value class Subnet(private val x: Pair<IPV4, Int>) {

    val ip: IPV4
        get() = x.first

    override fun toString() =
        "${this.x.first}/${this.x.second}"

    operator fun contains(other: IPV4) =
        (other.uintIp shr (32 - this.x.second)) == (x.first.uintIp shr (32 - this.x.second))
}

private val privateSubnets = listOf(
    "192.168.0.0".toIPV4() / 16,
    "172.16.0.0".toIPV4() / 12,
    "10.0.0.0".toIPV4() / 8,
)

val loopback = "127.0.0.0".toIPV4() / 8
val linkLocal = "169.254.0.0".toIPV4() / 16
val broadcast = "255.255.255.255".toIPV4() / 32


