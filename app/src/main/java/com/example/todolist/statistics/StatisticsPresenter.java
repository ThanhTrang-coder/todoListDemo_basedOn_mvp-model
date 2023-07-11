package com.example.todolist.statistics;

import static androidx.core.util.Preconditions.checkNotNull;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.todolist.data.database.Task;
import com.example.todolist.data.database.TaskDatabase;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class StatisticsPresenter implements StatisticsContract.Presenter {

    //private final TasksRepository tasksRepository;
    private Context context;
    private CompositeDisposable compositeDisposable;

    private final StatisticsContract.View statisticsView;

    @SuppressLint("RestrictedApi")
    public StatisticsPresenter(Context context,
                               @NonNull StatisticsContract.View statisticsView) {
        this.context = context;
        this.compositeDisposable = new CompositeDisposable();
        this.statisticsView = checkNotNull(statisticsView, "StatisticsView cannot be null!");

        statisticsView.setPresenter(this);
    }

    @Override
    public void start() {
        loadStatistics();
    }

    private void loadStatistics() {
        statisticsView.setProgressIndicator(true);

        compositeDisposable.add(TaskDatabase.getInstance(context).getTaskDao().getAllTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showStatistic, this::showError));
    }

    private void showStatistic(List<Task> tasks) {
        int activeTasks = 0;
        int completedTasks = 0;
        for(Task task : tasks) {
            if (task.isCompleted()) {
                completedTasks += 1;
            }
            if(!task.isCompleted()) {
                activeTasks += 1;
            }
        }
        if(!statisticsView.isActive()) {
            return;
        }
        statisticsView.setProgressIndicator(false);

        statisticsView.showStatistics(activeTasks, completedTasks);
    }

    private void showError(Throwable throwable) {

    }
}
