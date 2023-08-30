package com.LetsPlay.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.persistence.Entity;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Document(collection = "users")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private static List<User> allUsers = new ArrayList<>();

    @Id
    private String id;

    @Field("name")
    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @Field("email")
    @Email
    @NotNull
    @Size(max = 50)
    private String email;

    @Field("password")
    @NotNull
    @Size(min = 6, max = 50)
    private String password;

    @Field("role")
    @NotNull
    @Size(min = 9, max = 10)
    private String role;

    public boolean hasValidEmail() {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.getEmail().trim());

        return matcher.matches();
    }

    public boolean hasDuplicatedEmail(String userId) {
        for (User user: allUsers) {
            if ((userId == null || !userId.equals(user.getId()))
                    && this.getEmail().trim().equalsIgnoreCase(user.getEmail())) {
                return true;
            }
        }
        return false;
    }

    public static void fetchAllUsers(List<User> users) {
        allUsers.clear();
        allUsers.addAll(users);
    }
}
