package com.itesm.domain.models.availability;

public class DataAvailabilityInfo {

    private final boolean available;
    private final String availabilityStatus;
    private final String note;

    public DataAvailabilityInfo(
            boolean available,
            String availabilityStatus,
            String note
    ) {
        this.available = available;
        this.availabilityStatus = availabilityStatus;
        this.note = note;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public String getNote() {
        return note;
    }
}
