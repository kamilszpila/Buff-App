package pl.kamilszpila.buff.model

import com.google.gson.annotations.SerializedName

data class Buff(

        val id: Int,

        @SerializedName("time_to_show")
        val timeToShow: Int,

        val author: Author?,
        val question: Question?,
        val answers: List<Answer>?
) {

    data class Author(
            @SerializedName("first_name")
            val firstName: String,

            @SerializedName("last_name")
            val lastName: String,

            val image: String
    )

    data class Question(
            val id: Int,
            val title: String
    )

    data class Answer(
            val id: Int,
            val title: String
    )
}