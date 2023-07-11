package com.example.todolist.tasks;

import static androidx.core.util.Preconditions.checkNotNull;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.todolist.addEditTask.AddEditTaskActivity;
import com.example.todolist.data.database.Task;
import com.example.todolist.data.database.TaskDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TasksPresenter implements TasksContract.Presenter {
    //private final TasksRepository tasksRepository;
    private final TasksContract.View tasksView;
    private CompositeDisposable compositeDisposable;
    private TasksFilterType currentFiltering = TasksFilterType.ALL_TASKS;
    private boolean firstLoad = true;
    Context context;

    @SuppressLint("RestrictedApi")
    public TasksPresenter (
                           Context context,
                           @NonNull TasksContract.View tasksView)  {
        //this.tasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null");
        this.context = context;
        this.compositeDisposable = new CompositeDisposable();
        this.tasksView = checkNotNull(tasksView, "tasksView cannot be null");
        this.tasksView.setPresenter(this);
    }
    @Override
    public void start() {
        loadTasks(false);
    }

    @Override
    public void result(int requestCode, int resultCode) {
        if(AddEditTaskActivity.REQUEST_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode) {
            tasksView.showSuccessfullySavedMessage();
        }
    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        loadTasks(forceUpdate || firstLoad, true);
        firstLoad = false;

    }

    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            tasksView.setLoadingIndicator(true);
        }
        if(forceUpdate) {
            compositeDisposable.add(TaskDatabase.getInstance(context).getTaskDao().getAllTasks()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::processTasks, this::showError));
        }

        // The view may not be able to handle UI updates anymore
        if (!tasksView.isActive()) {
            return;
        }
        if (showLoadingUI) {
            tasksView.setLoadingIndicator(false);
        }
    }

    private void processTasks(List<Task> tasks) {
        List<Task> tasksToShow = new ArrayList<Task>();
        if (tasks.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            processEmptyTasks();
        } else {
            for (Task task : tasks) {
                switch (currentFiltering) {
                    case ALL_TASKS:
                        tasksToShow.add(task);
                        break;
                    case ACTIVE_TASKS:
                        if (task.isActive()) {
                            tasksToShow.add(task);
                        }
                        break;
                    case COMPLETED_TASKS:
                        if (task.isCompleted()) {
                            tasksToShow.add(task);
                        }
                        break;
                    default:
                        tasksToShow.add(task);
                        break;
                }
            }
            // Show the list of tasks
            tasksView.showTasks(tasksToShow);
            // Set the filter label's text.
            showFilterLabel();
        }
    }

    private void processEmptyTasks() {
        switch (currentFiltering) {
            case ACTIVE_TASKS:
                tasksView.showNoActiveTasks();
                break;
            case COMPLETED_TASKS:
                tasksView.showNoCompletedTasks();
                break;
            default:
                tasksView.showNoTasks();
                break;
        }
    }

    private void showFilterLabel() {
        switch (currentFiltering) {
            case ACTIVE_TASKS:
                tasksView.showActiveFilterLabel();
                break;
            case COMPLETED_TASKS:
                tasksView.showCompletedFilterLabel();
                break;
            default:
                tasksView.showAllFilterLabel();
                break;
        }
    }

    @Override
    public void addNewTask() {
        tasksView.showAddTask();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void openTaskDetails(Task requestedTask) {
        checkNotNull(requestedTask, "requestedTask cannot be null!");
        tasksView.showTaskDetailsUi(requestedTask.getId());
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void completeTask(Task completedTask) {
        checkNotNull(completedTask, "completedTask cannot be null!");
        tasksView.showTaskMarkedComplete();
        updateTask(completedTask);
        loadTasks(false, false );
    }

    private void createSuccess(Integer integer) {
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void activateTask(Task activeTask) {
        checkNotNull(activeTask, "activeTask cannot be null!");
        tasksView.showTaskMarkedActive();
        loadTasks(false, false );
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void updateTask(Task task) {
        checkNotNull(task);
        checkNotNull(TaskDatabase.getInstance(context).getTaskDao().updateTask(task));
            task.setCompleted(true);
            compositeDisposable.add(Single.fromCallable(() -> TaskDatabase.getInstance(context).getTaskDao().updateTask(task))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::createSuccess, this::showError));
    }

    @Override
    public void clearCompletedTasks() {
        compositeDisposable.add(TaskDatabase.getInstance(context).getTaskDao().getAllTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showTask, this::showError));

    }

    private void showTask(List<Task> tasks) {
        for(Task task : tasks) {
            if (task.isCompleted()) {
                TaskDatabase.getInstance(context).getTaskDao().deleteTaskById(task.getId());
                TaskDatabase.getInstance(context).getTaskDao().getAllTasks();
            }
        }
        tasksView.showCompletedTasksCleared();
        loadTasks(false, false);
    }

    private void showError(Throwable throwable) {

    }

    @Override
    public void setFiltering(TasksFilterType requestType) {
        currentFiltering = requestType;
    }

    @Override
    public TasksFilterType getFiltering() {
        return currentFiltering;
    }
}
