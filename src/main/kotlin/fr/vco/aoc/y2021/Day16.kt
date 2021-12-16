package fr.vco.aoc.y2021

fun main() {
    val input = readFirstLine("Day16")

    val packet = input.map{it.digitToInt(16).toString(2).padStart(4,'0')}.joinToString ("")

    println("Part 1 : ${extractFirstPacket(packet).sumVersion()}")

}

sealed class Packet(val version: Int, val type: Int, val size: Int) {

    abstract fun sumVersion() : Int

    class ValuePacket(version: Int, type: Int, size: Int, val value: Long) : Packet(version, type, size) {
        override fun sumVersion() = version
        override fun toString() = "(version=$version, type=$type, size=$size, value=$value)"
    }

    class OperatorPacket(version: Int, type: Int, length: Int, val packets: List<Packet>) :
        Packet(version, type, packets.sumOf { it.size } + 7+ length) {
        override fun toString() = "(version=$version, type=$type, size=$size, packets=$packets)"
        override fun sumVersion() = packets.sumOf{it.sumVersion()} + version
    }
}


fun extractFirstPacket(packet: String): Packet {
    val version = packet.take(3).toInt(2)
    val type = packet.substring(3, 6).toInt(2)
    if (type == 4) {
        return parseValuePacket(version, type, packet.drop(6))
    } else {
        val i = packet.substring(6, 7).toInt(2)

        if (i == 0) {
            val totalLength = packet.substring(7, 7 + 15).toInt(2)
            var subPacketString = packet.drop(7 + 15)
            val subPackets = mutableListOf<Packet>()
            var lenght = 0
            while (lenght < totalLength) {
                val subPacket = extractFirstPacket(subPacketString)
                subPacketString = subPacketString.drop(subPacket.size)
                subPackets.add(subPacket)
                lenght += subPacket.size
            }
            return Packet.OperatorPacket(version, type,15,subPackets)
        } else {
            val subPacketsCount = packet.substring(7, 7 + 11).toInt(2)
            var subPacketString = packet.drop(7 + 11)
            val subPackets = mutableListOf<Packet>()
            repeat(subPacketsCount) {
                val subPacket = extractFirstPacket(subPacketString)
                subPacketString = subPacketString.drop(subPacket.size)
                subPackets.add(subPacket)
            }
            return Packet.OperatorPacket(version, type,11, subPackets)
        }
    }
}

fun parseValuePacket(version: Int, type: Int, packet: String): Packet.ValuePacket {
    var current = 0
    var binary = ""
    do {
        val isFinish = packet.substring(current, current + 1).toInt(2) == 0
        binary += packet.substring(current + 1, current + 5)
        current += 5
    } while (!isFinish)
    return Packet.ValuePacket(version, type, current + 6, binary.toLong(2))
}
