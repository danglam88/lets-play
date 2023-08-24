package com.LetsPlay.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private static List<User> allUsers = new ArrayList<>();

    @Id
    private String id;

    private String name;

    private String email;

    private String password;

    private String role;

    public boolean hasValidEmail() {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.getEmail());

        return matcher.matches();
    }

    public boolean hasDuplicatedEmail(String userId) {
        for (User user: allUsers) {
            if ((userId == null || !userId.equals(user.getId()))
                    && this.getEmail().equalsIgnoreCase(user.getEmail())) {
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
