package pl.kamilszpila.buffapp.view_model

import android.app.Application
import android.os.Handler
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import pl.kamilszpila.buff.model.Buff
import pl.kamilszpila.buffapp.repository.MainActivityRepository

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val MAX_BUFF_ID = 5
        private const val TIME_BETWEEN_BUFFS = 30000L
    }

    private val repository = MainActivityRepository()
    val buffData: LiveData<Buff>
    private val mHandler = Handler()
    private var currentBuffId = 0
    private var remainingTime = 0L //TODO
    private var lastPostTime = 0L

    init {
        this.buffData = repository.buffData
    }

    fun start() {
        postFetchWithDelay(remainingTime)
    }

    fun stop() {
        mHandler.removeCallbacksAndMessages(null)
        val timeFromLastPost = System.currentTimeMillis() - lastPostTime
        remainingTime = if (timeFromLastPost < TIME_BETWEEN_BUFFS) {
            TIME_BETWEEN_BUFFS - timeFromLastPost
        } else {
            0
        }
    }

    private fun postFetchWithDelay(delay: Long) {
        mHandler.postDelayed(
            {
                fetchNextBuff()
            }, delay
        )
        lastPostTime = System.currentTimeMillis()
    }

    private fun fetchNextBuff() {
        if (++currentBuffId > MAX_BUFF_ID) {
            return
        }

        repository.fetchBuffData(currentBuffId)
        postFetchWithDelay(TIME_BETWEEN_BUFFS)
    }

}