package me._4o4.blinkenlightsconverter.models

class FramePage{
    private val rows = mutableListOf<String>()
    fun addRow(row: String){
        rows.add(row)
    }
    override fun toString(): String {
        val builder = StringBuilder()
        rows.forEach {
            builder.append("        <row>$it</row>")
        }
        return builder.toString()
    }
}