package fairyShop.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static fairyShop.common.ExceptionMessages.HELPER_NAME_NULL_OR_EMPTY;

public abstract class BaseHelper implements Helper {
    private String name;
    private int energy;
    private Collection<Instrument> instruments;

    public BaseHelper(String name, int energy) {
        this.setName(name);
        this.energy = energy;
        this.instruments = new ArrayList<>();
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new NullPointerException(HELPER_NAME_NULL_OR_EMPTY);
        }
        this.name = name;
    }


    @Override
    public void work() {
        if (energy - 10 <= 0) {
            this.energy = 0;
        } else {
            this.energy -= 10;
        }
    }

    @Override
    public void addInstrument(Instrument instrument) {
        this.instruments.add(instrument);

    }

    @Override
    public boolean canWork() {
        return energy > 0;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getEnergy() {
        return this.energy;
    }

    @Override
    public Collection<Instrument> getInstruments() {
        return Collections.unmodifiableCollection(this.instruments);
    }
}