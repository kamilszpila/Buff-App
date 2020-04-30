package pl.kamilszpila.buff.view

import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.buff_view.view.*
import pl.kamilszpila.buff.R
import pl.kamilszpila.buff.exception.InvalidBuffException
import pl.kamilszpila.buff.exception.InvalidBuffViewParentException
import pl.kamilszpila.buff.model.Buff


class BuffView(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {

    companion object {
        private const val HIDING_BUFF_AFTER_ANSWER_DELAY = 2000L
        private const val MIN_ANSWERS_COUNT = 2
        private const val MAX_ANSWERS_COUNT = 5
    }

    private val buffView: LinearLayout? =
        View.inflate(context, R.layout.buff_view, this) as LinearLayout
    private val mHandler = Handler()
    private var countDownTimer: CountDownTimer? = null

    fun stop() {
        mHandler.removeCallbacksAndMessages(null)
    }

    fun showBuff(buff: Buff) {
        if (!isBuffValid(buff)) {
            return
        }

        if (!isParentLayoutValid()) {
            return
        }

        setupAndShowBuffView(buff)
    }

    @Throws(InvalidBuffException::class)
    private fun isBuffValid(buff: Buff): Boolean {
        Log.d("BuffView", "isBuffValid: ")
        if (buff.author == null) {
            throw InvalidBuffException(context.getString(R.string.invalid_author))
        }
        if (buff.question == null) {
            throw InvalidBuffException(context.getString(R.string.invalid_question))
        }
        if (buff.answers == null || buff.answers.isEmpty()) {
            throw InvalidBuffException(context.getString(R.string.invalid_answers))
        }
        if (buff.answers.size < MIN_ANSWERS_COUNT || buff.answers.size > MAX_ANSWERS_COUNT) {
            throw InvalidBuffException(
                context.getString(
                    R.string.invalid_answers_count,
                    MIN_ANSWERS_COUNT, MAX_ANSWERS_COUNT
                )
            )
        }
        return true
    }

    @Throws(InvalidBuffViewParentException::class)
    private fun isParentLayoutValid(): Boolean {
        if (parent == null) {
            throw InvalidBuffViewParentException(context.getString(R.string.invalid_parent_layout))
        }
        if (parent !is RelativeLayout && parent !is ConstraintLayout) {
            throw InvalidBuffViewParentException(context.getString(R.string.invalid_parent_layout))
        }
        return true
    }

    private fun setupAndShowBuffView(buff: Buff) {

        setTopSection(buff.author!!)

        setQuestionView(buff.question!!)

        setAnswersView(buff.answers!!)

        setCountDownTimerView(buff.timeToShow)

        setBuffViewEnabled(true)

        buffView?.visibility = View.VISIBLE
    }

    private fun setTopSection(author: Buff.Author) {
        closeBuffBtn?.setOnClickListener {
            stopTimer()
            closeBuffView()
        }

        setAuthorView(author)

        topSection?.visibility = View.VISIBLE
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
    }

    private fun closeBuffView() {
        setBuffViewEnabled(false)
        buffView?.visibility = View.GONE
    }

    private fun View.setBuffViewEnabled(enabled: Boolean) {
        isEnabled = enabled
        if (this is ViewGroup) {
            children.forEach { child -> child.setBuffViewEnabled(enabled) }
        }
    }

    private fun setAuthorView(author: Buff.Author) {
        val authorFullName = "${author.firstName} ${author.lastName}"

        authorText?.text = authorFullName

        buffView?.authorImage?.let {
            Glide.with(buffView)
                .load(author.image)
                .into(it)
        }
    }

    private fun setQuestionView(question: Buff.Question) {
        questionText?.text = question.title

        questionLayout?.visibility = View.VISIBLE
    }

    private fun setCountDownTimerView(time: Int) {
        val timeInMillis = (time * 1000).toLong()

        updateTimerProgress(timeInMillis, timeInMillis)

        countDownTimer = object : CountDownTimer(timeInMillis, 16) {
            override fun onTick(remainingTime: Long) {
                updateTimerProgress(remainingTime, timeInMillis)
            }

            override fun onFinish() {
                closeBuffView()
            }
        }.start()
    }

    private fun updateTimerProgress(remainingTime: Long, totalTime: Long) {
        timerProgress?.progress = ((totalTime - remainingTime) * 100 / totalTime).toInt()
        timerText?.text = ((remainingTime / 1000)).toString()
    }

    private fun setAnswersView(answers: List<Buff.Answer>) {
        answersLayout?.removeAllViews()

        for (answer in answers) {
            val answerView = getAnswerView(answer)
            answerView?.let {
                answersLayout?.addView(it)
            }
        }
    }

    private fun getAnswerView(answer: Buff.Answer): View? {
        val answerView = LayoutInflater.from(answersLayout.context).inflate(
            R.layout.answer_layout,
            answersLayout,
            false
        )
            ?: return null

        val tvAnswerText = answerView.findViewById(R.id.answerText) as TextView?
        tvAnswerText?.text = answer.title

        answerView.setOnClickListener {
            stopTimer()
            setBuffViewEnabled(false)
            mHandler.postDelayed({
                closeBuffView()
            }, HIDING_BUFF_AFTER_ANSWER_DELAY)
        }
        return answerView
    }
}