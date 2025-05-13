package com.example.office_management.dto.response;

public class LoginResponse {
    private int code;
    private String message;
    private ResultData result;

    public String getAccessToken() {
        return result != null ? result.getAccessToken() : null;
    }

    public static class ResultData {
        private String accessToken;
        private UserData userData;

        // Getter và setter
        public String getAccessToken() {
            return accessToken;
        }
    }

    public static class UserData {
        // Thêm các trường của userData ở đây
        // Getter và setter
    }
}