package com.imra.mynews.di.common;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.imra.mynews.mvp.models.Article;

import java.util.List;

import io.reactivex.Single;

/**
 * Date: 01.07.2020
 * Time: 20:42
 *
 * @author IMRA027
 */

@Dao
public interface ArticleDao {

    @Query("SELECT * FROM articles")
    List<Article> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert (Article article);

    @Delete
    void delete (Article article);

}
