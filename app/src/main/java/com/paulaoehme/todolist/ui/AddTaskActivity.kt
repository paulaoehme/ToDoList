package com.paulaoehme.todolist.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.paulaoehme.todolist.databinding.ActivityAddTaskBinding
import com.paulaoehme.todolist.datasource.TaskDataSource
import com.paulaoehme.todolist.extensions.format
import com.paulaoehme.todolist.extensions.text
import com.paulaoehme.todolist.model.Task
import java.util.*

class AddTaskActivity : AppCompatActivity () {

    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra(TASK_ID)) {
            val taskId = intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.findById(taskId)?.let {
                binding.layoutTitle.text = it.title
                binding.layoutDate.text = it.date
                binding.layoutTime.text = it.time
            }
        }

        insertListeners()
    }

    private fun insertListeners() {

        // Binding date editTextView
        binding.layoutDate.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                val timezone = TimeZone.getDefault()
                val offset = timezone.getOffset(Date().time) * -1
                binding.layoutDate.text = Date(it + offset).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        // Binding time editTextView
        binding.layoutTime.editText?.setOnClickListener {
            val timepicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()
            timepicker.addOnPositiveButtonClickListener{
                val hour = if(timepicker.hour in 0..9) "0${timepicker.hour}" else timepicker.hour
                val minute = if(timepicker.minute in 0..9) "0${timepicker.minute}" else timepicker.minute

                binding.layoutTime.text = "$hour:$minute"
            }
            timepicker.show(supportFragmentManager, null)
        }

        // Binding cancel button
        binding.btnCancel.setOnClickListener{
            finish()
        }

        // Binding add button
        binding.btnNewTask.setOnClickListener {
            val task = Task (
                title = binding.layoutTitle.text,
                date = binding.layoutDate.text,
                time = binding.layoutTime.text,
                id = intent.getIntExtra(TASK_ID, 0)
            )
            TaskDataSource.insertTask(task)

            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    companion object{
        const val TASK_ID = "task_id"
    }
}