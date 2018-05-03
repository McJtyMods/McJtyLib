package mcjty.lib.builder;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class InformationString {

    private final String string;
    private final List<Function<ItemStack, String>> informationStringParameters;

    public InformationString(String string) {
        this.string = string;
        informationStringParameters = new ArrayList<>();
    }

    public void addParameter(Function<ItemStack, String> parameter) {
        informationStringParameters.add(parameter);
    }

    public String getString() {
        return string;
    }

    public List<Function<ItemStack, String>> getInformationStringParameters() {
        return informationStringParameters;
    }
}
