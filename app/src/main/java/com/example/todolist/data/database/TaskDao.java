package com.example.todolist.data.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Observable;

@Dao
public interface TaskDao {
    @Insert
    Long insertTask(Task task);

    @Query("SELECT * FROM task WHERE id= :taskId")
    Task getTask(int taskId);
    @Query("SELECT * FROM task")
    Observable<List<Task>> getAllTasks();

    @Update
    int updateTask(Task task);

    @Delete
    void delete(Task task);

    @Query("DELETE FROM task WHERE id= :taskId")
    void deleteTaskById(int taskId);

    @Query("DELETE FROM task")
    void deleteAllTasks();
}
