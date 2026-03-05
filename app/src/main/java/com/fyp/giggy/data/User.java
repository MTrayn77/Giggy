// C21361681 – Michael Traynor
// Room Entity for Artist/Venue user with unique email & username

package com.fyp.giggy.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "users",
        indices = {
                @Index(value = {"email"}, unique = true),
                @Index(value = {"name"}, unique = true)
        }
)
public class User {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull public String name;
    @NonNull public String email;
    @NonNull public String password;
    @NonNull public String role; // "artist" or "venue"

    public User(@NonNull String name, @NonNull String email,
                @NonNull String password, @NonNull String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
