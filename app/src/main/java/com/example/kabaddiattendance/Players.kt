package com.example.kabaddiattendance

object Players {
    val list = listOf(
        "Hafis", "Goutham", "Vinayak", "Adwaith",
        "Adithyan", "Siva Shakthi", "Milan", "Abhimanyu"
    )

    val images = mapOf(
        "Hafis" to "https://i.imghippo.com/files/wIj4414XM.jpg",
        "Goutham" to "https://i.imghippo.com/files/xQAg5811Nc.jpg",
        "Vinayak" to "https://i.imghippo.com/files/Wuyr6007wsk.jpg",
        "Adwaith" to "https://i.imghippo.com/files/Xsl5861i.jpg",
        "Adithyan" to "https://i.imghippo.com/files/4uF7760uMi.jpg",
        "Siva Shakthi" to "https://i.imghippo.com/files/VSww8171dU.jpg",
        "Milan" to "https://i.imghippo.com/files/BYv2035pKc.jpg",
        "Abhimanyu" to "https://i.imghippo.com/files/enYY5241s.jpg"
    )

    enum class Status(val label: String) {
        PRESENT("Present ✅"),
        ABSENT("Absent ❌"),
        LATE("Late ⏰"),
        EXCUSED("Excused 🟡");
    }
}
