package com.example.todolist.addEditTask;

import static androidx.core.util.Preconditions.checkNotNull;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todolist.data.database.Task;
import com.example.todolist.data.database.TaskDatabase;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AddEditTaskPresenter implements AddEditTaskContract.Presenter{
    private Context context;
    private CompositeDisposable compositeDisposable;

    @NonNull
    private final AddEditTaskContract.View addTaskView;

    @Nullable
    private int taskId;

    @SuppressLint("RestrictedApi")
    public AddEditTaskPresenter(@Nullable int taskId, Context context,
                                @NonNull AddEditTaskContract.View addTaskView) {
        this.taskId = taskId;
        this.context = context;
        this.addTaskView = checkNotNull(addTaskView);
        this.compositeDisposable = new CompositeDisposable();

        addTaskView.setPresenter(this);
    }

    @Override
    public void createTask(String title, String description) {
        Task newTask = new Task(title, description, false);
        if (newTask.isEmpty()) {
            addTaskView.showEmptyTaskError();
        } else {
            //tasksRepository.saveTask(newTask);
            compositeDisposable.add(Single.fromCallable(() -> TaskDatabase.getInstance(context).getTaskDao().insertTask(newTask))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::createSuccess, error -> showError(error, "Save error")));
            addTaskView.showTasksList();
        }
    }

    private void createSuccess(Long response) {

    }

    private void showError(Throwable throwable, String message) {
        throwable.getLocalizedMessage();
        // view showError
    }

    @Override
    public void getTask() {
        if(String.valueOf(taskId) == null || String.valueOf(taskId).isEmpty()) {
            Toast.makeText(context, "You have no task to do", Toast.LENGTH_SHORT).show();
        } else {
            Task task = TaskDatabase.getInstance(context).getTaskDao().getTask(taskId);
            addTaskView.setTitle(task.getTitle());
            addTaskView.setDescription(task.getDescription());
        }
    }


    @Override
    public void updateTask(String title, String description) {
        Task updateTask = TaskDatabase.getInstance(context).getTaskDao().getTask(taskId);
        //tasksRepository.saveTask(new Task(taskId, title, description));
        updateTask.setTitle(title);
        updateTask.setDescription(description);

        compositeDisposable.add(Single.fromCallable(() -> TaskDatabase.getInstance(context).getTaskDao().updateTask(updateTask))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateSuccess, error -> showError(error, "Save error")));
        addTaskView.showTasksList(); // After an edit, go back to the list.
    }

    private void updateSuccess(Integer integer) {
    }

    @Override
    public void populateTask() {

        //tasksRepository.getTask(taskId, this);
        TaskDatabase.getInstance(context).getTaskDao().getTask(taskId);
    }

    @Override
    public void start() {
        populateTask();
    }

    public void dispose() {
        compositeDisposable.dispose();
    }
}
