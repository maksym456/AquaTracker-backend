package com.aquatracker.logs;

import com.aquatracker.common.IdMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/logs")
public class LogController {

    private final LogEntryRepository logEntryRepository;

    public LogController(LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
    }

    @GetMapping
    public List<LogEntryResponseDto> getLogs(
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) String aquariumId,
            @RequestParam(required = false, defaultValue = "desc") String sort,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        
        List<LogEntry> logs;
        
        if (aquariumId != null) {
            Long aqId = IdMapper.fromAquariumId(aquariumId);
            if (aqId != null) {
                logs = logEntryRepository.findByAquarium_IdOrderByCreatedAtDesc(aqId);
            } else {
                logs = List.of();
            }
        } else {
            logs = logEntryRepository.findAll();
        }
        
        if (actionType != null && !actionType.isEmpty()) {
            logs = logs.stream()
                    .filter(log -> actionType.equals(log.getActionType()))
                    .collect(Collectors.toList());
        }
        
        if ("asc".equals(sort)) {
            logs = logs.stream()
                    .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                    .collect(Collectors.toList());
        } else {
            logs = logs.stream()
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .collect(Collectors.toList());
        }
        
        int skip = offset != null ? offset : 0;
        int take = limit != null ? limit : Integer.MAX_VALUE;
        
        return logs.stream()
                .skip(skip)
                .limit(take)
                .map(LogEntryResponseDto::new)
                .collect(Collectors.toList());
    }

    public static class LogEntryResponseDto {
        private String id;
        private String userId;
        private String aquariumId;
        private String aquariumName;
        private String actionType;
        private String title;
        private String message;
        private String createdAt;
        private Map<String, Object> metadata;

        public LogEntryResponseDto(LogEntry log) {
            this.id = IdMapper.toLogId(log.getId());
            this.userId = log.getUser() != null ? IdMapper.toUserId(log.getUser().getId()) : null;
            this.aquariumId = log.getAquarium() != null ? IdMapper.toAquariumId(log.getAquarium().getId()) : null;
            this.aquariumName = log.getAquariumName();
            this.actionType = log.getActionType();
            this.title = log.getTitle();
            this.message = log.getMessage();
            this.createdAt = log.getCreatedAt() != null ? log.getCreatedAt().toString() : null;
            this.metadata = log.getMetadata() != null ? Map.of() : Map.of();
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getAquariumId() { return aquariumId; }
        public void setAquariumId(String aquariumId) { this.aquariumId = aquariumId; }
        public String getAquariumName() { return aquariumName; }
        public void setAquariumName(String aquariumName) { this.aquariumName = aquariumName; }
        public String getActionType() { return actionType; }
        public void setActionType(String actionType) { this.actionType = actionType; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
}

