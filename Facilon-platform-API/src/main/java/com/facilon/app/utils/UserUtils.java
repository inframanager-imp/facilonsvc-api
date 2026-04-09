package com.facilon.app.utils;

import com.facilon.app.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserUtils {
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserPrincipal) { // Assuming MyUserDetails is your custom UserDetails class
                return ((UserPrincipal) principal).getId(); // Assuming getId() is a method in MyUserDetails
            }
        }
        return null; // User not logged in or unable to retrieve user ID
    }
    public static String getCurrentUserLoginId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserPrincipal) { // Assuming MyUserDetails is your custom UserDetails class
                return ((UserPrincipal) principal).getLoginId(); // Assuming getId() is a method in MyUserDetails
            }
        }
        return null; // User not logged in or unable to retrieve user ID
    }
    public static List<String > getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            List<String> roles = new ArrayList<>();

            if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;

                // Extract roles
                for (GrantedAuthority authority : userPrincipal.getAuthorities()) {
                    roles.add(authority.getAuthority());
                }
                return roles;
            }
        }
        return new ArrayList<>();
    }

    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserPrincipal) {
                return ((UserPrincipal) principal).getEmail();
            }
        }
        return null; // User not logged in or unable to retrieve email
    }

    public static List<String> getCurrentUserGroupNames() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserPrincipal) {
                return ((UserPrincipal) principal).getGroupNames();
            }
        }
        return new ArrayList<>(); // User not logged in or unable to retrieve group names
    }

}
