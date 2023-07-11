package com.example.todolist.taskDetail;

import static androidx.core.util.Preconditions.checkNotNull;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todolist.data.database.Task;
import com.example.todolist.data.database.TaskDatabase;

/**
 * Listens to user actions from the UI, retrieves the data
 * and updates the UI as required
 */
public class TaskDetailPresenter implements TaskDetailContract.Presenter {
    //private final TasksRepository tasksRepository;
    private final TaskDetailContract.View taskDetailView;
    @Nullable
    private int taskId;
    private Context context;

//    @SuppressLint("RestrictedApi")
//    public TaskDetailPresenter(@Nullable String taskId,
//                               @NonNull TasksRepository tasksRepository,
//                               @NonNull TaskDetailContract.View taskDetailView) {
//        this.taskId = taskId;
//        this.tasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null");
//        this.taskDetailView = checkNotNull(taskDetailView, "tasksDetailView cannot be null");
//        taskDetailView.setPresenter(this);
//    }

    @SuppressLint("RestrictedApi")
    public TaskDetailPresenter(@Nullable int taskId,
                               Context context,
                               @NonNull TaskDetailContract.View taskDetailView) {
        this.taskId = taskId;
        this.context = context;
        this.taskDetailView = checkNotNull(taskDetailView, "tasksDetailView cannot be null");
        taskDetailView.setPresenter(this);
    }

    @Override
    public void start() {
        openTask();
    }

    private void openTask() {
        taskDetailView.setLoadingIndicator(true);
        Task task = TaskDatabase.getInstance(context).getTaskDao().getTask(taskId);
        showTask(task);
//        tasksRepository.getTask(taskId, new TasksDataSource.GetTaskCallback() {
//            @Override
//            public void onTaskLoaded(Task task) {
//                if (!taskDetailView.isActive()) {
//                    return;
//                }
//                taskDetailView.setLoadingIndicator(false);
//                if (null == task) {
//                    taskDetailView.showMissingTask();
//                } else {
//                    showTask(task);
//                }
//            }
//
//            @Override
//            public void onDataNotAvailable() {
//                if (!taskDetailView.isActive()) {
//                    return;
//                }
//                taskDetailView.showMissingTask();
//            }
//        });
    }

    @Override
    public void editTask() {
        taskDetailView.showEditTask(taskId);
    }

    @Override
    public void deleteTask() {
        //tasksRepository.deleteTask(taskId);
        Task task = TaskDatabase.getInstance(context).getTaskDao().getTask(taskId);
        TaskDatabase.getInstance(context).getTaskDao().delete(task);
        TaskDatabase.getInstance(context).getTaskDao().getAllTasks();
        taskDetailView.showTaskDeleted();
    }

    @Override
    public void completeTask() {

        //tasksRepository.completeTask(taskId);
        Task task = TaskDatabase.getInstance(context).getTaskDao().getTask(taskId);
        task.setCompleted(true);
        TaskDatabase.getInstance(context).getTaskDao().updateTask(task);
        taskDetailView.showTaskMarkedComplete();
    }

    @Override
    public void activateTask() {

        //tasksRepository.activateTask(taskId);
        Task task = TaskDatabase.getInstance(context).getTaskDao().getTask(taskId);
        task.setCompleted(false);
        TaskDatabase.getInstance(context).getTaskDao().updateTask(task);
        taskDetailView.showTaskMarkedActive();
    }

    private void showTask(Task task) {
        String title = task.getTitle();
        String description = task.getDescription();

        if (title != null && title.isEmpty()) {
            taskDetailView.hideTitle();
        } else {
            taskDetailView.showTitle(title);
        }

        if (description != null && description.isEmpty()) {
            taskDetailView.hideDescription();
        } else {
            taskDetailView.showDescription(description);
        }

        taskDetailView.showCompletionStatus(task.isCompleted());
    }
}
