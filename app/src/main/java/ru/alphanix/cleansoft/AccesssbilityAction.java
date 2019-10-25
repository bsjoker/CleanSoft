package ru.alphanix.cleansoft;

public class AccesssbilityAction {
    private static AccesssbilityAction instance;

    public static AccesssbilityAction getInstance() {
        if (instance == null) {
            instance = new AccesssbilityAction();
        }
        return instance;
    }

    private AccesssbilityAction() {
    }

    public void set(String str, boolean z) {
        Cache.getInstance().set(str, Boolean.valueOf(z));
    }

    public boolean get(String str) {
        return Cache.getInstance().getBoolean(str, Boolean.valueOf(false)).booleanValue();
    }
}
