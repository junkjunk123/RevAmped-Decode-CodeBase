package revAmped.components.Misc;

public abstract class TeleOpEnumeratedAction {
    protected boolean completed = false;
    protected boolean initCompleted = false;
    protected abstract void init();
    protected abstract void loop();
    public String identifier() {return "";}
    public boolean update() {
        if (!initCompleted) {
            init();
            initCompleted = true;
        } else if (!completed) {
            loop();
        }

        return completed;
    }
}
