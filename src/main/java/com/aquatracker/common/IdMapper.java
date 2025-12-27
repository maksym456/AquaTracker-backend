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

    public static String toUserId(Long id) {
        return toApiId("u", id);
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

    public static Long fromUserId(String userId) {
        return fromApiId(userId);
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
}

