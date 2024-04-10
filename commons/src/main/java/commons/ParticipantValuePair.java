package commons;



public record ParticipantValuePair(Participant participant, Money money)
        implements Comparable<ParticipantValuePair> {

    /**
     * @param   other
     *          The other {@code ParticipantValuePair} to be compared.
     *
     * @return  A negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(ParticipantValuePair other) {
        return this.money.compareTo(other.money());
    }
}

