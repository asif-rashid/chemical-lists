package com.example.chemicallists.api;

import java.util.List;

public record BatchResultResponse(List<String> added, List<String> skipped) {
}
