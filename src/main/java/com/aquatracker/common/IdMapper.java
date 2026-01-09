package com.aquatracker.common;

public class IdMapper {

    public static String toApiId(String prefix, Long id) {
        if (id == null) {
            return null;
        }
        return prefix + "_" + id;
    }

    public static Long fromApiId(String apiId) {
        if (apiId == null || apiId.isEmpty()) {
            return null;
        }
        try {
            int lastUnderscore = apiId.lastIndexOf('_');
            if (lastUnderscore == -1) {
                return null;
            }
            String numberPart = apiId.substring(lastUnderscore + 1);
            return Long.parseLong(numberPart);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String toFishId(Long id) {
        return toApiId("fish", id);
    }

    public static String toPlantId(Long id) {
        return toApiId("plant", id);
    }

    public static String toAquariumId(Long id) {
        return toApiId("aq", id);
    }

    public static String toUserId(String id) {
        if (id == null) {
            return null;
        }
        // UUID jest już unikalny, zwracamy bezpośrednio bez prefiksu
        return id;
    }

    public static String toLogId(Long id) {
        return toApiId("log", id);
    }

    public static String toContactId(Long id) {
        return toApiId("c", id);
    }

    public static String toInvitationId(Long id) {
        return toApiId("inv", id);
    }

    public static Long fromFishId(String fishId) {
        return fromApiId(fishId);
    }

    public static Long fromPlantId(String plantId) {
        return fromApiId(plantId);
    }

    public static Long fromAquariumId(String aquariumId) {
        return fromApiId(aquariumId);
    }

    public static String fromUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        // Obsługa zarówno formatu z prefiksem "u_" (dla kompatybilności) jak i bez
        if (userId.startsWith("u_")) {
            // Usuń prefiks "u_" i zwróć resztę jako UUID
            String uuidPart = userId.substring(2);
            // Sprawdź czy to poprawny UUID
            if (isValidUUID(uuidPart)) {
                return uuidPart;
            }
        }
        // Jeśli to już UUID bez prefiksu, zwróć bezpośrednio
        if (isValidUUID(userId)) {
            return userId;
        }
        // Jeśli to nie UUID, zwróć null (błąd formatu)
        return null;
    }
    
    private static boolean isValidUUID(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            // UUID ma format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx (36 znaków)
            // lub bez myślników: 32 znaki hex
            String uuidPattern = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
            String uuidPatternNoDashes = "^[0-9a-fA-F]{32}$";
            return str.matches(uuidPattern) || str.matches(uuidPatternNoDashes);
        } catch (Exception e) {
            return false;
        }
    }

    public static Long fromLogId(String logId) {
        return fromApiId(logId);
    }

    public static Long fromContactId(String contactId) {
        return fromApiId(contactId);
    }

    public static Long fromInvitationId(String invitationId) {
        return fromApiId(invitationId);
    }

    public static String toHistoryId(Long id) {
        return toApiId("hist", id);
    }

    public static Long fromHistoryId(String historyId) {
        return fromApiId(historyId);
    }

    public static String toShareId(Long id) {
        return toApiId("share", id);
    }

    public static Long fromShareId(String shareId) {
        return fromApiId(shareId);
    }

    public static String toNotificationId(Long id) {
        return toApiId("notif", id);
    }

    public static Long fromNotificationId(String notificationId) {
        return fromApiId(notificationId);
    }
}

