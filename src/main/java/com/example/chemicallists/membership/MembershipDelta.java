package com.example.chemicallists.membership;

import java.util.List;

public record MembershipDelta(List<String> added, List<String> skipped) {
}
