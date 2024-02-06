package com.comunidadedevspace.taskbeats

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    private var taskList = arrayListOf(
        Task(0, "Title0", "Desc0"), Task(1, "Title1", "Desc1")
    )

    private lateinit var ctnContent: LinearLayout

    private val adapter: TaskListAdapter = TaskListAdapter(::onListClickedItem)

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            val taskAction = data?.getSerializableExtra(TASK_ACTION_RESULT) as TaskAction
            val task: Task = taskAction.task

            if (taskAction.actionType == ActionType.Delete.name) {
                val newList = arrayListOf<Task>().apply {
                    addAll(taskList)
                }
                newList.remove(task)
                showMessage(ctnContent, "Você deletou a tarefa ${task.title}")
                if (newList.size == 0) {
                    ctnContent.visibility = View.VISIBLE
                }
                adapter.submitList(newList)
                taskList = newList

            } else if (taskAction.actionType == ActionType.Create.name) {
                val newList = arrayListOf<Task>().apply {
                    addAll(taskList)
                }
                newList.add(task)
                showMessage(ctnContent, "Tarefa adicionada ${task.title}")
                adapter.submitList(newList)
                taskList = newList
            } else if (taskAction.actionType == ActionType.Update.name) {

                val tempEmptyList = arrayListOf<Task>()
                taskList.forEach {
                    if (it.id == task.id) {
                        val newItem = Task(it.id, task.title, task.description)
                        tempEmptyList.add(newItem)
                    } else {
                        tempEmptyList.add(it)
                    }
                }
                showMessage(ctnContent, "Tarefa atualizada ${task.title}")
                adapter.submitList(tempEmptyList)
                taskList = tempEmptyList
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        ctnContent = findViewById(R.id.ctn_content)
        adapter.submitList(taskList)

        val rvTasks: RecyclerView = findViewById(R.id.rv_task_list)
        rvTasks.adapter = adapter


        // Este vem do FloatingActionButton
        val fab: View = findViewById(R.id.fab_add)
        fab.setOnClickListener {
            openTaskListDetail(null)
        }
    }

    private fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }

    //Este vem da lista
    private fun onListClickedItem(task: Task) {
        openTaskListDetail(task)
    }

    private fun openTaskListDetail(task: Task?) {
        val intent = TaskDetailActivity.start(this, task)
        startForResult.launch(intent)
    }
}

enum class ActionType {
    Delete,
    Update,
    Create
}

data class TaskAction(
    val task: Task, val actionType: String
) : Serializable

const val TASK_ACTION_RESULT = "TASK_ACTION_RESULT"