package pl.kamilszpila.buffapp.repository

import androidx.lifecycle.LiveData
import pl.kamilszpila.buff.model.Buff
import pl.kamilszpila.buffapp.remote.BuffClient

class MainActivityRepository {

    private val buffClient = BuffClient()
    val buffData : LiveData<Buff>


    init {
        this.buffData = buffClient.buffData
    }

    fun fetchBuffData(id: Int) {
        buffClient.fetchBuffData(id)
    }

}