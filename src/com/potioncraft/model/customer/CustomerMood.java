package com.potioncraft.model.customer;

/** Waiting expressions share exact thresholds with the UI and sprite filenames. */
public enum CustomerMood {
    NORMAL("ปกติ"), IMPATIENT("เริ่มไม่พอใจ"), ANGRY("โกรธ");

    public static final double IMPATIENT_THRESHOLD=.50, ANGRY_THRESHOLD=.25;
    private final String label;
    CustomerMood(String label){this.label=label;}
    public String getLabel(){return label;}
    public String getAssetKey(){return name().toLowerCase(java.util.Locale.ROOT);}
    public static CustomerMood fromPatience(double remaining){
        if(remaining<=ANGRY_THRESHOLD)return ANGRY;
        if(remaining<=IMPATIENT_THRESHOLD)return IMPATIENT;
        return NORMAL;
    }
}
