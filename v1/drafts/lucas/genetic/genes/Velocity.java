package autobot.v1.drafts.lucas.genetic.genes;

public class Velocity extends Gene {

    // TODO: Mover para consts
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 20;

    public Velocity() {
        super.minValue = MIN_VALUE;
        super.maxValue = MAX_VALUE;
        super.mutate();
    }

    public Velocity(int value) {
        super.minValue = MIN_VALUE;
        super.maxValue = MAX_VALUE;
        super.value = value;
    }

    public Velocity(Velocity copy) {
        super.copy(copy);
    }

    @Override
    public Velocity copy() {
        return new Velocity(this);
    }

}
