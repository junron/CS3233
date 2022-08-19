package routing

import utils.IPV4
import utils.Subnet

data class RouteTableEntry(val dest: Subnet, val next: IPV4?, val numHops: Int, val iface: IPV4?)
