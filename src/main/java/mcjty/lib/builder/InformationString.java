package mcjty.lib.builder;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class InformationString {

    private final String string;
    private final List<Function<ItemStack, String>> informationStringParameters;

    public InformationString(String string) {
        this.string = string;
        informationStringParameters = Collections.emptyList();
    }

    public InformationString(String string, Function<ItemStack, String>... parameters) {
        this.string = string;
        if (parameters.length > 0) {
            informationStringParameters = new ArrayList<>(parameters.length);
            Collections.addAll(informationStringParameters, parameters);
        } else {
            informationStringParameters = Collections.emptyList();
        }
    }

    public String getString() {
        return string;
    }

    public List<Function<ItemStack, String>> getInformationStringParameters() {
        return informationStringParameters;
    }
}
