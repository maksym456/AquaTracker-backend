package com.aquarium.aquarium;

import java.time.LocalDateTime;
import java.util.List;

public class AquariumStatusDto {
    private String level;
    private List<StatusIssueDto> issues;
    private LocalDateTime lastCheckedAt;

    public AquariumStatusDto() {}

    public AquariumStatusDto(String level, List<StatusIssueDto> issues, LocalDateTime lastCheckedAt) {
        this.level = level;
        this.issues = issues;
        this.lastCheckedAt = lastCheckedAt;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<StatusIssueDto> getIssues() {
        return issues;
    }

    public void setIssues(List<StatusIssueDto> issues) {
        this.issues = issues;
    }

    public LocalDateTime getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(LocalDateTime lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }

    public static class StatusIssueDto {
        private String type;
        private String message;

        public StatusIssueDto() {}

        public StatusIssueDto(String type, String message) {
            this.type = type;
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

