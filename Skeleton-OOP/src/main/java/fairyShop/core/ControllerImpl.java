package fairyShop.core;

import fairyShop.common.ConstantMessages;
import fairyShop.common.ExceptionMessages;
import fairyShop.models.*;
import fairyShop.repositories.HelperRepository;
import fairyShop.repositories.PresentRepository;

import java.util.List;
import java.util.stream.Collectors;

import static fairyShop.common.ConstantMessages.*;
import static fairyShop.common.ExceptionMessages.HELPER_TYPE_DOESNT_EXIST;
import static fairyShop.common.ExceptionMessages.NO_HELPER_READY;

public class ControllerImpl implements Controller {
    private HelperRepository<Helper> helperRepository = new HelperRepository<>();
    private PresentRepository<Present> presentRepository = new PresentRepository<>();
    private Shop shop = new ShopImpl();

    @Override
    public String addHelper(String type, String helperName) {
        Helper helper;
        switch (type) {
            case "Sleepy":
                helper = new Sleepy(helperName);
                break;
            case "Happy":
                helper = new Happy(helperName);
                break;
            default:
                throw new IllegalArgumentException(HELPER_TYPE_DOESNT_EXIST);
        }
        this.helperRepository.add(helper);
        return String.format(ADDED_HELPER, type, helperName);
    }

    @Override
    public String addInstrumentToHelper(String helperName, int power) {
        if (this.helperRepository.findByName(helperName) == null) {
            throw new IllegalArgumentException(HELPER_TYPE_DOESNT_EXIST);
        }
        Instrument instrument = new InstrumentImpl(power);
        this.helperRepository.findByName(helperName).addInstrument(instrument);
        return String.format(SUCCESSFULLY_ADDED_INSTRUMENT_TO_HELPER, power, helperName);
    }

    @Override
    public String addPresent(String presentName, int energyRequired) {
        Present present = new PresentImpl(presentName, energyRequired);
        this.presentRepository.add(present);
        return String.format(SUCCESSFULLY_ADDED_PRESENT, presentName);
    }

    @Override
    public String craftPresent(String presentName) {
        List<Helper> collect = helperRepository.getModels()
                .stream()
                .filter(helper -> helper.getEnergy() > 50)
                .collect(Collectors.toList());
        if (collect.isEmpty()) {
            throw new IllegalArgumentException(NO_HELPER_READY);
        }
        int brokenInstruments = 0;
        Present present = presentRepository.findByName(presentName);
        for (Helper helper : collect) {
            shop.craft(present, helper);
            brokenInstruments += (int) helper.getInstruments()
                    .stream()
                    .filter(Instrument::isBroken)
                    .count();
            if (present.isDone()) {
                break;
            }
        }
        if (present.isDone()) {
            return String.format(ConstantMessages.PRESENT_DONE, presentName, "done") +
                    String.format(ConstantMessages.COUNT_BROKEN_INSTRUMENTS, brokenInstruments);
        }
        return String.format(ConstantMessages.PRESENT_DONE, presentName, "not done") +
                String.format(ConstantMessages.COUNT_BROKEN_INSTRUMENTS, brokenInstruments);

    }

    @Override
    public String report() {
        int size = (int) presentRepository.getModels().stream().filter(Present::isDone).count();
        List<String> collect = helperRepository.getModels().stream().map(helper -> String.format("Name: %s%n" +
                        "Energy: %d%n" +
                        "Instruments: %d not broken left%n", helper.getName(), helper.getEnergy(),
                (int) helper.getInstruments().stream().filter(instrument -> !instrument.isBroken()).count())).collect(Collectors.toList());
        return String.format("%d presents are done!%n", size) + String.format("Helpers info:%n") + String.join("", collect).trim();
    }
}
