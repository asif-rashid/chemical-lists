package com.example.chemicallists.problem;

public record ProblemDetailsResponse(String type, String title, int status, String detail, String instance) {

    public static ProblemDetailsResponse of(String type, String title, int status, String detail, String instance) {
        return new ProblemDetailsResponse(type, title, status, detail, instance);
    }
}
