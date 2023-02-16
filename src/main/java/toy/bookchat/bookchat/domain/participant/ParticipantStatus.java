package toy.bookchat.bookchat.domain.participant;

public enum ParticipantStatus {
    HOST("HOST"),
    SUBHOST("SUBHOST"),
    GUEST("GUEST");

    private final String participantStatus;
    
    ParticipantStatus(String participantStatus) {
        this.participantStatus = participantStatus;
    }
}
