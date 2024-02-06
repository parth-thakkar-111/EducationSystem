package com.codecx.educationsystem.activities.learner

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import com.codecx.educationsystem.R
import com.codecx.educationsystem.activities.CourseDetailActivity.Companion.selectedTask
import com.codecx.educationsystem.blassclasses.BaseActivity
import com.codecx.educationsystem.databinding.ActivityTaskQuizForLearnerBinding
import com.codecx.educationsystem.dataclasses.QuizDataClass
import com.codecx.educationsystem.dataclasses.ResultDataClass
import com.codecx.educationsystem.utils.*
import com.codecx.educationsystem.viewmodels.SingleSharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskQuizActivityForLearner : BaseActivity(), MediaPlayer.OnPreparedListener {
    private lateinit var binding: ActivityTaskQuizForLearnerBinding
    private var quizList: List<QuizDataClass>? = null
    private var mediaPlayer: MediaPlayer? = null
    private val mSharedViewModel: SingleSharedViewModel by viewModel()
    var isChecked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskQuizForLearnerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mHeader.tvHeader.text =
            "Quiz (${selectedTask?.taskCourse?.courseName}) -> ${selectedTask?.taskName}"
        if (savedInstanceState == null) {
            mViewModel.loadQuiz(selectedTask?.taskId)
        }
        binding.btnNext.setOnClickListener {
            if (isChecked) {
                if (mediaPlayer?.isPlaying == true || mediaPlayer != null) {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                }
                val obj = ResultDataClass()
                obj.apply {
                    this.taskId = selectedTask?.taskId
                    this.quizQuestion = mViewModel.currentQuiz.value?.quizQuestion
                    this.selectedOption = getCheckedOptionValue()
                    this.isCorrect = getIsCorrect(this.selectedOption)
                }
                mSharedViewModel.addQuizIntoAnsweredList(obj)
                if (mViewModel.quizNo == quizList?.lastIndex) {
                    startNewActivity<QuizResultActivity>(true)
                } else {
                    makeQuiz()
                }
            } else {
                showToast("Select Option")
            }


        }
        lifeCycleObserver(savedInstanceState)
    }

    private fun getIsCorrect(selectedOption: String?): Boolean {
        return if (selectedOption != null) {
            val fil =
                mViewModel.currentQuiz.value?.quizOptions?.filter { it.value.optionTitle == selectedOption }
            if (fil?.isNotEmpty() == true) {
                fil.toList().last().second.isCorrect
            } else {
                false
            }
        } else {
            false
        }
    }

    private fun getCheckedOptionValue(): String? {
        var checkedValue: String? = null
        binding.quizOptionsLayout.forEachIndexed { index, view ->
            if (view is RadioButton) {
                if (view.isChecked) {
                    checkedValue = view.text?.toString()
                    return@forEachIndexed
                }
            }
        }
        return checkedValue
    }

    private fun isChecked() = CoroutineScope(Dispatchers.Main).launch {
        binding.quizOptionsLayout.forEachIndexed { index, view ->
            if (view is RadioButton) {
                if (view.isChecked) {
                    isChecked = true
                    return@forEachIndexed
                } else {
                    isChecked = false
                }
            }
        }
    }

    private fun lifeCycleObserver(savedInstanceState: Bundle?) {
        mViewModel.apply {
            quiz.observe(this@TaskQuizActivityForLearner) {
                if (it.isNotEmpty()) {
                    binding.apply {
                        mEmpty.beGone()
                        quizLayout.beVisible()
                    }
                    quizList = it
                    if (savedInstanceState == null) {
                        makeQuiz()
                    }
                } else {
                    binding.apply {
                        mEmpty.beVisible()
                        quizLayout.beGone()
                    }
                }
            }
            currentQuiz.observe(this@TaskQuizActivityForLearner) { quiz ->
                quiz?.let {
                    displayQuiz(it)
                }
            }
        }
    }

    private fun displayQuiz(values: QuizDataClass) {
        if (mViewModel.quizNo == quizList?.lastIndex) {
            binding.btnNext.text = "Finish"
        }
        binding.apply {
            tvQuizTitle.text = values.quizTitle
            tvQuizQuestion.text = values.quizQuestion
            if (quizOptionsLayout.childCount != 0) {
                quizOptionsLayout.removeAllViews()
            }
            values.quizOptions.forEach {
                val view = LayoutInflater.from(this@TaskQuizActivityForLearner)
                    .inflate(R.layout.quizoptionlayout, null, false) as RadioButton
                view.text = it.value.optionTitle
                quizOptionsLayout.addView(view)
                initListeners()
            }
            val mimeType = values.fileMimeType
            mimeType?.let {
                if (isImage(it)) {
                    disableViews(quizImage)
                    quizImage.beVisible()
                    quizImage.setImage(values.fileUrl)
                    return
                }
                if (isAudio(it)) {
                    disableViews(quizAudio)
                    quizAudio.beVisible()
                    mediaPlayer = MediaPlayer.create(
                        this@TaskQuizActivityForLearner,
                        Uri.parse(values.fileUrl)
                    )
                    mediaPlayer?.start()
                    btnPlayPause.setImage(R.drawable.baseline_pause_24)
                    mediaPlayer?.setOnPreparedListener(this@TaskQuizActivityForLearner)
                    btnPlayPause.setOnClickListener {
                        if (mediaPlayer?.isPlaying == true) {
                            mediaPlayer?.pause()
                            btnPlayPause.setImage(R.drawable.baseline_play_arrow_24)
                        } else {
                            mediaPlayer?.start()
                            btnPlayPause.setImage(R.drawable.baseline_pause_24)
                        }
                    }
                    return
                }
                if (isPdf(it)) {
                    disableViews(fileLayout)
                    fileLayout.beVisible()
                    tvFileName.text = "Click here to open file"
                    fileLayout.setOnClickListener {
                        startActivity(Intent(Intent.ACTION_VIEW).also {
                            it.data = Uri.parse(values.fileUrl)
                        })
                    }
                    return
                }
                if (isVideo(it)) {
                    disableViews(videoView)
                    videoView.beVisible()
                    videoView.setVideoURI(Uri.parse(values.fileUrl))
                    videoView.start()
                    videoView.setOnClickListener {
                        if (videoView.isPlaying) {
                            videoView.pause()
                        } else {
                            videoView.start()
                        }
                    }
                    return
                }
            }

        }
    }

    private fun disableViews(mView: View) {
        binding.mMediaViews.children.forEachIndexed { index, view ->
            if (view.id != mView.id) {
                view.beGone()
            }
        }
    }

    private fun isImage(fileMimeType: String): Boolean {
        return fileMimeType.contains("image/")
    }

    private fun isVideo(fileMimeType: String): Boolean {
        return fileMimeType.contains("video/")
    }

    private fun isAudio(fileMimeType: String): Boolean {
        return fileMimeType.contains("audio/")
    }

    private fun isPdf(fileMimeType: String): Boolean {
        return fileMimeType == "application/pdf"
    }


    private fun initListeners() {
        binding.quizOptionsLayout.children.forEachIndexed { index, view ->
            if (view is RadioButton) {
                view.setOnClickListener {
                    isChecked = true
                    selectItem(it as RadioButton)
                }
            }
        }
    }

    private fun selectItem(btn: RadioButton) {
        binding.quizOptionsLayout.children.forEachIndexed { index, view ->
            if (view is RadioButton && btn.text != view.text) {
                if (view.isChecked) {
                    view.isChecked = false
                }
            }
        }
    }

    private fun makeQuiz() {
        isChecked = false
        mViewModel.quizNo++
        val value = quizList?.get(mViewModel.quizNo)
        mViewModel.submitCurrentQuiz(value!!)
    }

    override fun onPrepared(p0: MediaPlayer?) {
        val handler = Handler(Looper.myLooper() ?: mainLooper)
        runOnUiThread(object : Runnable {
            override fun run() {
                binding.mSeekBar.progress = (mediaPlayer?.currentPosition ?: 0) / 1000
                handler.postDelayed(this, 100L)
            }

        })
    }
}