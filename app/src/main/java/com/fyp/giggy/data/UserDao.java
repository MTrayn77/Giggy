// C21361681 – Michael Traynor
// UserDao.java – CRUD & lookups

package com.fyp.giggy.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(User user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User findByEmail(String email);

    @Query("SELECT * FROM users WHERE name = :name LIMIT 1")
    User findByName(String name);

    @Query("SELECT * FROM users WHERE (email = :identifier OR name = :identifier) AND password = :password LIMIT 1")
    User login(String identifier, String password);
}
