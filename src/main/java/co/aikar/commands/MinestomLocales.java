package co.aikar.commands;


import java.util.Locale;

public class MinestomLocales extends Locales {

    public MinestomLocales(MinestomCommandManager manager) {
        super(manager);
    }

    @Override
    public void loadLanguages() {
        super.loadLanguages();
        addMessageBundle("acf-minecraft", Locale.ENGLISH);
    }
}
