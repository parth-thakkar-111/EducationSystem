package com.codecx.educationsystem.activities.educator

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.webkit.MimeTypeMap
import android.widget.CheckBox
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import com.codecx.educationsystem.R
import com.codecx.educationsystem.activities.CourseDetailActivity.Companion.selectedTask
import com.codecx.educationsystem.blassclasses.BaseActivity
import com.codecx.educationsystem.databinding.ActivityCreateQuizBinding
import com.codecx.educationsystem.dataclasses.OptionsDataClass
import com.codecx.educationsystem.dataclasses.QuizDataClass
import com.codecx.educationsystem.utils.FireBaseRefrences
import com.codecx.educationsystem.utils.showToast
import com.codecx.zipunzip.interfaces.ResultCallBacks
import com.google.android.material.textfield.TextInputLayout
import org.koin.android.ext.android.inject
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class CreateQuizActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateQuizBinding
    private var options: HashMap<String, OptionsDataClass>? = null
    private var selectedFileUri: Uri = Uri.EMPTY
    private var selectedFileMimeType: String = "*/*"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.headerLayout.apply {
            tvHeader.text = "Create Quiz"
        }
        binding.apply {
            btnCreate.setOnClickListener {
                val quizTitle = txtQuizTitle.editText?.text?.toString()
                val quizQuestion = txtQuizQuestion.editText?.text?.toString()
                if (quizTitle?.isEmpty() == true) {
                    txtQuizTitle.error = "Required..."
                    return@setOnClickListener
                }
                if (quizQuestion?.isEmpty() == true) {
                    txtQuizTitle.error = "Required..."
                    return@setOnClickListener
                }
                if (quizOptions.childCount < 4) {
                    showToast("Minimum 4 options required...")
                    return@setOnClickListener
                }
                collectDataAndCreateQuiz(quizTitle, quizQuestion)
            }
            btnAttach.setOnClickListener {
                resultLauncher.launch(Intent(Intent.ACTION_PICK).also {
                    it.type = "*/*"
                })
            }

            btnAddOption.setOnClickListener {
                val view = LayoutInflater.from(this@CreateQuizActivity)
                    .inflate(R.layout.quizoptionitemlayout, null, false)
                quizOptions.addView(view)
                quizOptions.invalidate()
                TransitionManager.beginDelayedTransition(quizOptions)
                invalidateListeners()
            }

        }
    }

    private fun invalidateListeners() {
        binding.quizOptions.children.forEachIndexed { index, view ->
            val btnDelete = view.findViewById<ImageView>(R.id.btnDeleteQuiz)
            btnDelete.setOnClickListener {
                try {
                    binding.quizOptions.removeView(view)
                } catch (ex: Exception) {

                }

            }
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let { uri ->
                selectedFileUri = uri
                selectedFileMimeType = getMimeType(uri) ?: "*/*"
                binding.tvAttachFile.text = "(${selectedFileMimeType}) type file selected"
            }
        }

    private fun getMimeType(uri: Uri): String? {
        var mimeType: String? = "*/*"
        mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr: ContentResolver = contentResolver
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                uri
                    .toString()
            )
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.lowercase(Locale.getDefault())
            )
        }
        return mimeType
    }

    private fun collectDataAndCreateQuiz(quizTitle: String?, quizQuestion: String?) {
        options = getOptions()
        val quizData = QuizDataClass()
        quizData.apply {
            this.quizTitle = quizTitle
            this.quizQuestion = quizQuestion
            this.quizOptions = options ?: hashMapOf()
            this.task = selectedTask
            this.fileMimeType = selectedFileMimeType
        }
        mViewModel.addQuiz(quizData, selectedFileUri, object : ResultCallBacks<QuizDataClass> {
            override fun onFail(message: Exception) {
                mViewModel.closeProgress()
                selectedFileUri = Uri.EMPTY
                selectedFileMimeType="*/*"
                runOnUiThread {
                    showToast(message.message)
                }
            }

            override fun onSuccess(result: QuizDataClass) {
                mViewModel.closeProgress()
                selectedFileUri = Uri.EMPTY
                selectedFileMimeType="*/*"
                runOnUiThread {
                    showToast("Create successful")
                }
            }

        })
    }

    private fun getOptions(): HashMap<String, OptionsDataClass> {
        val mMap = HashMap<String, OptionsDataClass>()
        binding.quizOptions.children.forEachIndexed { index, view ->
            val textOption = view.findViewById<TextInputLayout>(R.id.txtQuizOption)
            val mCheck = view.findViewById<CheckBox>(R.id.btnIsCorrect)
            val optionData = OptionsDataClass()
            optionData.optionId = fireBaseRefrences.quizRef.push().key
            optionData.optionTitle = textOption.editText?.text?.toString() ?: ""
            optionData.isCorrect = mCheck.isChecked
            mMap[optionData.optionId!!] = optionData
        }
        return mMap
    }
}