package org.newdeal.core.system;

/**
 * @author Frederick Vanderbeke
 * @since 27/05/2015.
 */
public class Model {
    private final Pattern arrivalPattern;
    private final Pattern departurePattern;
    private final Discipline discipline;
    private final Capacity capacity;
    private final NbServers nbServers;

    public Model(Pattern arrivalPattern, Pattern departurePattern, Discipline discipline, Capacity capacity, NbServers nbServers) {
        this.arrivalPattern = arrivalPattern;
        this.departurePattern = departurePattern;
        this.discipline = discipline;
        this.capacity = Capacity.copy(capacity);
        this.nbServers = NbServers.copy(nbServers);
    }

    public Pattern getArrivalPattern() {
        return arrivalPattern;
    }

    public Pattern getDeparturePattern() {
        return departurePattern;
    }

    public Discipline getDiscipline() {
        return discipline;
    }

    public Capacity getCapacity() {
        return Capacity.copy(capacity);
    }

    public NbServers getNbServers() {
        return NbServers.copy(nbServers);
    }
}
