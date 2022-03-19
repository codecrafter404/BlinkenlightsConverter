package me._4o4.blinkenlightsconverter.models

class FramePage{
    private val rows = mutableListOf<String>()
    fun addRow(row: String){
        rows.add(row)
    }
    fun getRows(): List<String>{
        return rows.toList()
    }
    override fun toString(): String {
        val builder = StringBuilder()
        rows.forEach {
            builder.append("        <row>$it</row>")
        }
        return builder.toString()
    }
}