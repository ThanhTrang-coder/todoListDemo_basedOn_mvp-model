package com.example.todolist.statistics;

import static androidx.core.util.Preconditions.checkNotNull;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.todolist.R;

import io.reactivex.disposables.CompositeDisposable;

public class StatisticsFragment extends Fragment implements StatisticsContract.View {
    private StatisticsContract.Presenter presenter;
    private CompositeDisposable compositeDisposable;
    private TextView tvStatistic;

    @SuppressLint("RestrictedApi")
    @Override
    public void setPresenter(StatisticsContract.Presenter presenter) {
        this.presenter = checkNotNull(presenter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_statistic, container, false);
        tvStatistic = root.findViewById(R.id.statistics);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

//    @Override
//    public void onDestroyView() {
//        compositeDisposable.dispose();
//        super.onDestroyView();
//    }

    @Override
    public void setProgressIndicator(boolean active) {
        if(active) {
            tvStatistic.setText(getString(R.string.loading));
        } else {
            tvStatistic.setText("");
        }
    }

    @Override
    public void showStatistics(int numberOfIncompleteTasks, int numberOfCompletedTasks) {
        if(numberOfIncompleteTasks == 0 && numberOfCompletedTasks == 0) {
            tvStatistic.setText(getResources().getString(R.string.statistics_no_tasks));
        } else {
            String statisticsString = getResources().getString(R.string.statistics_active_tasks) + " "
                    + numberOfIncompleteTasks + "\n" + getResources().getString(R.string.statistics_completed_tasks)
                    + " " + numberOfCompletedTasks;
            tvStatistic.setText(statisticsString);
        }
    }

    @Override
    public void showLoadingStatisticsError() {
        tvStatistic.setText(getResources().getString(R.string.statistics_error));
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}