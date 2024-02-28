package mcjty.lib.base;

import net.neoforged.neoforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public class StyleConfig {

    public static final String CATEGORY_STYLE = "style";

    public static int colorSliderTopLeft = 0xff2b2b2b;
    public static int colorSliderBottomRight = 0xffffffff;
    public static int colorSliderFiller = 0xff636363;
    public static int colorSliderKnobTopLeft = 0xffeeeeee;
    public static int colorSliderKnobBottomRight = 0xff333333;
    public static int colorSliderKnobFiller = 0xff8b8b8b;
    public static int colorSliderKnobDraggingTopLeft = 0xff5c669d;
    public static int colorSliderKnobDraggingBottomRight = 0xffbcc5ff;
    public static int colorSliderKnobDraggingFiller = 0xff7f89bf;
    public static int colorSliderKnobMarkerLine = 0xff4e4e4e;
    public static int colorSliderKnobHoveringTopLeft = 0xffa5aac5;
    public static int colorSliderKnobHoveringBottomRight = 0xff777c99;
    public static int colorSliderKnobHoveringFiller = 0xff858aa5;

    public static int colorTextNormal = 0xFF303030;
    public static int colorTextInListNormal = 0xFF151515;
    public static int colorTextDisabled = 0xFFa0a0a0;

    public static int colorTextFieldFiller = 0xffc6c6c6;
    public static int colorTextFieldFocusedFiller = 0xffeeeeee;
    public static int colorTextFieldHoveringFiller = 0xffdadada;
    public static int colorTextFieldCursor = 0xff000000;
    public static int colorTextFieldTopLeft = 0xff2b2b2b;
    public static int colorTextFieldBottomRight = 0xffffffff;

    public static int colorEnergyBarTopLeft = 0xff2b2b2b;
    public static int colorEnergyBarBottomRight = 0xffffffff;
    public static int colorEnergyBarHighEnergy = 0xffdd0000;
    public static int colorEnergyBarLowEnergy = 0xff631111;
    public static int colorEnergyBarSpacer = 0xff430000;
    public static int colorEnergyBarText = 0xffffffff;

    public static int colorListBackground = 0xff8b8b8b;
    public static int colorListSeparatorLine = 0xff5c5c5c;
    public static int colorListSelectedHighlightedGradient1 = 0xffbbbb00;
    public static int colorListSelectedHighlightedGradient2 = 0xff999900;
    public static int colorListSelectedGradient1 = 0xff616161;
    public static int colorListSelectedGradient2 = 0xff414141;
    public static int colorListHighlightedGradient1 = 0xff717120;
    public static int colorListHighlightedGradient2 = 0xff515110;

    public static int colorBackgroundBevelBright = 0xffffffff;
    public static int colorBackgroundBevelDark = 0xff2b2b2b;
    public static int colorBackgroundFiller = 0xffc6c6c6;

    public static int colorToggleNormalBorderTopLeft = 0xffeeeeee;
    public static int colorToggleNormalBorderBottomRight = 0xff777777;
    public static int colorToggleNormalFiller = 0xffc6c6c6;
    public static int colorToggleDisabledBorderTopLeft = 0xffeeeeee;
    public static int colorToggleDisabledBorderBottomRight = 0xff777777;
    public static int colorToggleDisabledFiller = 0xffc6c6c6;
    public static int colorToggleTextNormal = 0xFF303030;
    public static int colorToggleTextDisabled = 0xFFa0a0a0;

    public static int colorButtonExternalBorder = 0xff000000;

    public static int colorCycleButtonTriangleNormal = 0xff000000;
    public static int colorCycleButtonTriangleDisabled = 0xff888888;

    public static int colorButtonBorderTopLeft = 0xffeeeeee;
    public static int colorButtonBorderBottomRight = 0xff777777;
    public static int colorButtonFiller = 0xffc6c6c6;
    public static int colorButtonFillerGradient1 = 0xffb1b1b1;
    public static int colorButtonFillerGradient2 = 0xffe1e1e1;

    public static int colorButtonDisabledBorderTopLeft = 0xffeeeeee;
    public static int colorButtonDisabledBorderBottomRight = 0xff777777;
    public static int colorButtonDisabledFiller = 0xffc6c6c6;
    public static int colorButtonDisabledFillerGradient1 = 0xffb1b1b1;
    public static int colorButtonDisabledFillerGradient2 = 0xffe1e1e1;

    public static int colorButtonSelectedBorderTopLeft = 0xff5c669d;
    public static int colorButtonSelectedBorderBottomRight = 0xffbcc5ff;
    public static int colorButtonSelectedFiller = 0xff7f89bf;
    public static int colorButtonSelectedFillerGradient1 = 0xff6a74aa;
    public static int colorButtonSelectedFillerGradient2 = 0xff949ed4;

    public static int colorButtonHoveringBorderTopLeft = 0xffa5aac5;
    public static int colorButtonHoveringBorderBottomRight = 0xff999ebb;
    public static int colorButtonHoveringFiller = 0xffa2a7c2;
    public static int colorButtonHoveringFillerGradient1 = 0xff8d92ad;
    public static int colorButtonHoveringFillerGradient2 = 0xffbabfda;

    private static Map<String, ForgeConfigSpec.ConfigValue<String>> colorConfigValues = new HashMap<>();

    public static void init(ForgeConfigSpec.Builder CLIENT_BUILDER) {
        CLIENT_BUILDER.comment("Style settings for all mods using mcjtylib").push(CATEGORY_STYLE);

        initSetting(CLIENT_BUILDER, "colorSliderTopLeft", colorSliderTopLeft, "Color: slider top left border");
        initSetting(CLIENT_BUILDER, "colorSliderBottomRight", colorSliderBottomRight, "Color: slider bottom right border");
        initSetting(CLIENT_BUILDER, "colorSliderFiller", colorSliderFiller, "Color: slider background");
        initSetting(CLIENT_BUILDER, "colorSliderKnobTopLeft", colorSliderKnobTopLeft, "Color: slider knob top left border");
        initSetting(CLIENT_BUILDER, "colorSliderKnobBottomRight", colorSliderKnobBottomRight, "Color: slider knob bottom right border");
        initSetting(CLIENT_BUILDER, "colorSliderKnobFiller", colorSliderKnobFiller, "Color: slider knob background");
        initSetting(CLIENT_BUILDER, "colorSliderKnobDraggingTopLeft", colorSliderKnobDraggingTopLeft, "Color: slider knob top left border while dragging");
        initSetting(CLIENT_BUILDER, "colorSliderKnobDraggingBottomRight", colorSliderKnobDraggingBottomRight, "Color: slider knob bottom right border while dragging");
        initSetting(CLIENT_BUILDER, "colorSliderKnobDraggingFiller", colorSliderKnobDraggingFiller, "Color: slider knob background while dragging");
        initSetting(CLIENT_BUILDER, "colorSliderKnobHoveringTopLeft", colorSliderKnobHoveringTopLeft, "Color: slider knob top left border while hovering");
        initSetting(CLIENT_BUILDER, "colorSliderKnobHoveringBottomRight", colorSliderKnobHoveringBottomRight, "Color: slider knob bottom right border while hovering");
        initSetting(CLIENT_BUILDER, "colorSliderKnobHoveringFiller", colorSliderKnobHoveringFiller, "Color: slider knob background while hovering");
        initSetting(CLIENT_BUILDER, "colorSliderKnobMarkerLine", colorSliderKnobMarkerLine, "Color: slider knob little marker lines");

        initSetting(CLIENT_BUILDER, "colorTextNormal", colorTextNormal, "Color: text normal");
        initSetting(CLIENT_BUILDER, "colorTextInListNormal", colorTextInListNormal, "Color: text as used in lists");
        initSetting(CLIENT_BUILDER, "colorTextDisabled", colorTextDisabled, "Color: text disabled");

        initSetting(CLIENT_BUILDER, "colorTextFieldTopLeft", colorTextFieldTopLeft, "Color: textfield top left border");
        initSetting(CLIENT_BUILDER, "colorTextFieldBottomRight", colorTextFieldBottomRight, "Color: textfield bottom right border");
        initSetting(CLIENT_BUILDER, "colorTextFieldFiller", colorTextFieldFiller, "Color: textfield background");
        initSetting(CLIENT_BUILDER, "colorTextFieldFocusedFiller", colorTextFieldFocusedFiller, "Color: textfield backbground while focused");
        initSetting(CLIENT_BUILDER, "colorTextFieldHoveringFiller", colorTextFieldHoveringFiller, "Color: textfield backbground while hovering");
        initSetting(CLIENT_BUILDER, "colorTextFieldCursor", colorTextFieldCursor, "Color: textfield cursor");

        initSetting(CLIENT_BUILDER, "colorEnergyBarTopLeft", colorEnergyBarTopLeft, "Color: energy bar top left border");
        initSetting(CLIENT_BUILDER, "colorEnergyBarBottomRight", colorEnergyBarBottomRight, "Color: energy bar bottom right border");
        initSetting(CLIENT_BUILDER, "colorEnergyBarHighEnergy", colorEnergyBarHighEnergy, "Color: energy bar high energy level");
        initSetting(CLIENT_BUILDER, "colorEnergyBarLowEnergy", colorEnergyBarLowEnergy, "Color: energy bar low energy level");
        initSetting(CLIENT_BUILDER, "colorEnergyBarSpacer", colorEnergyBarSpacer, "Color: energy bar spacer (between every energy level bar)");
        initSetting(CLIENT_BUILDER, "colorEnergyBarText", colorEnergyBarText, "Color: energy bar text");

        initSetting(CLIENT_BUILDER, "colorListBackground", colorListBackground, "Color: list background");
        initSetting(CLIENT_BUILDER, "colorListSeparatorLine", colorListSeparatorLine, "Color: list separator line");
        initSetting(CLIENT_BUILDER, "colorListSelectedHighlightedGradient1", colorListSelectedHighlightedGradient1, "Color: list selected and highlighted gradient");
        initSetting(CLIENT_BUILDER, "colorListSelectedHighlightedGradient2", colorListSelectedHighlightedGradient2, "Color: list selected and highlighted gradient");
        initSetting(CLIENT_BUILDER, "colorListSelectedGradient1", colorListSelectedGradient1, "Color: list selected gradient");
        initSetting(CLIENT_BUILDER, "colorListSelectedGradient2", colorListSelectedGradient2, "Color: list selected gradient");
        initSetting(CLIENT_BUILDER, "colorListHighlightedGradient1", colorListHighlightedGradient1, "Color: list highlighted gradient");
        initSetting(CLIENT_BUILDER, "colorListHighlightedGradient2", colorListHighlightedGradient2, "Color: list highlighted gradient");

        initSetting(CLIENT_BUILDER, "colorBackgroundBevelBright", colorBackgroundBevelBright, "Color: standard bevel bright border color");
        initSetting(CLIENT_BUILDER, "colorBackgroundBevelDark", colorBackgroundBevelDark, "Color: standard bevel dark border color");
        initSetting(CLIENT_BUILDER, "colorBackgroundFiller", colorBackgroundFiller, "Color: standard background color");

        initSetting(CLIENT_BUILDER, "colorToggleNormalBorderTopLeft", colorToggleNormalBorderTopLeft, "Color: toggle button normal top left border");
        initSetting(CLIENT_BUILDER, "colorToggleNormalBorderBottomRight", colorToggleNormalBorderBottomRight, "Color: toggle button normal bottom right border");
        initSetting(CLIENT_BUILDER, "colorToggleNormalFiller", colorToggleNormalFiller, "Color: toggle button normal background");
        initSetting(CLIENT_BUILDER, "colorToggleDisabledBorderTopLeft", colorToggleDisabledBorderTopLeft, "Color: toggle button disabled top left border");
        initSetting(CLIENT_BUILDER, "colorToggleDisabledBorderBottomRight", colorToggleDisabledBorderBottomRight, "Color: toggle button disabled bottom right border");
        initSetting(CLIENT_BUILDER, "colorToggleDisabledFiller", colorToggleDisabledFiller, "Color: toggle button disabled background");
        initSetting(CLIENT_BUILDER, "colorToggleTextNormal", colorToggleTextNormal, "Color: toggle button normal text");
        initSetting(CLIENT_BUILDER, "colorToggleTextDisabled", colorToggleTextDisabled, "Color: toggle button disabled text");

        initSetting(CLIENT_BUILDER, "colorCycleButtonTriangleNormal", colorCycleButtonTriangleNormal, "Color: cycle button small triangle");
        initSetting(CLIENT_BUILDER, "colorCycleButtonTriangleDisabled", colorCycleButtonTriangleDisabled, "Color: cycle button disabled small triangle");

        initSetting(CLIENT_BUILDER, "colorButtonExternalBorder", colorButtonExternalBorder, "Color: external border around buttons and some other components");
        initSetting(CLIENT_BUILDER, "colorButtonBorderTopLeft", colorButtonBorderTopLeft, "Color: button top left border");
        initSetting(CLIENT_BUILDER, "colorButtonBorderBottomRight", colorButtonBorderBottomRight, "Color: button bottom right border");
        initSetting(CLIENT_BUILDER, "colorButtonFiller", colorButtonFiller, "Color: button background");
        initSetting(CLIENT_BUILDER, "colorButtonFillerGradient1", colorButtonFillerGradient1, "Color: button background gradient");
        initSetting(CLIENT_BUILDER, "colorButtonFillerGradient2", colorButtonFillerGradient2, "Color: button background gradient");

        initSetting(CLIENT_BUILDER, "colorButtonDisabledBorderTopLeft", colorButtonDisabledBorderTopLeft, "Color: disabled button top left border");
        initSetting(CLIENT_BUILDER, "colorButtonDisabledBorderBottomRight", colorButtonDisabledBorderBottomRight, "Color: disabled button bottom right border");
        initSetting(CLIENT_BUILDER, "colorButtonDisabledFiller", colorButtonDisabledFiller, "Color: disabled button background");
        initSetting(CLIENT_BUILDER, "colorButtonDisabledFillerGradient1", colorButtonDisabledFillerGradient1, "Color: disabled button background gradient");
        initSetting(CLIENT_BUILDER, "colorButtonDisabledFillerGradient2", colorButtonDisabledFillerGradient2, "Color: disabled button background gradient");

        initSetting(CLIENT_BUILDER, "colorButtonSelectedBorderTopLeft", colorButtonSelectedBorderTopLeft, "Color: selected button top left border");
        initSetting(CLIENT_BUILDER, "colorButtonSelectedBorderBottomRight", colorButtonSelectedBorderBottomRight, "Color: selected button bottom right border");
        initSetting(CLIENT_BUILDER, "colorButtonSelectedFiller", colorButtonSelectedFiller, "Color: selected button background");
        initSetting(CLIENT_BUILDER, "colorButtonSelectedFillerGradient1", colorButtonSelectedFillerGradient1, "Color: selected button background gradient");
        initSetting(CLIENT_BUILDER, "colorButtonSelectedFillerGradient2", colorButtonSelectedFillerGradient2, "Color: selected button background gradient");

        initSetting(CLIENT_BUILDER, "colorButtonHoveringBorderTopLeft", colorButtonHoveringBorderTopLeft, "Color: hovering button top left border");
        initSetting(CLIENT_BUILDER, "colorButtonHoveringBorderBottomRight", colorButtonHoveringBorderBottomRight, "Color: hovering button bottom right border");
        initSetting(CLIENT_BUILDER, "colorButtonHoveringFiller", colorButtonHoveringFiller, "Color: hovering button background");
        initSetting(CLIENT_BUILDER, "colorButtonHoveringFillerGradient1", colorButtonHoveringFillerGradient1, "Color: hovering button background gradient");
        initSetting(CLIENT_BUILDER, "colorButtonHoveringFillerGradient2", colorButtonHoveringFillerGradient2, "Color: hovering button background gradient");

        CLIENT_BUILDER.pop();
    }

    private static void initSetting(ForgeConfigSpec.Builder CLIENT_BUILDER, String settingName, int setting, String comment) {
        ForgeConfigSpec.ConfigValue<String> value = CLIENT_BUILDER.comment(comment)
                .define(settingName, Integer.toHexString(setting & 0xffffff));
        colorConfigValues.put(settingName, value);
    }

    private static int resolveColor(String name) {
        return 0xff000000 + Integer.parseInt(colorConfigValues.get(name).get(), 16);
    }

    public static void updateColors() {
        colorSliderTopLeft = resolveColor("colorSliderTopLeft");
        colorSliderBottomRight = resolveColor("colorSliderBottomRight");
        colorSliderFiller = resolveColor("colorSliderFiller");
        colorSliderKnobTopLeft = resolveColor("colorSliderKnobTopLeft");
        colorSliderKnobBottomRight = resolveColor("colorSliderKnobBottomRight");
        colorSliderKnobFiller = resolveColor("colorSliderKnobFiller");
        colorSliderKnobDraggingTopLeft = resolveColor("colorSliderKnobDraggingTopLeft");
        colorSliderKnobDraggingBottomRight = resolveColor("colorSliderKnobDraggingBottomRight");
        colorSliderKnobDraggingFiller = resolveColor("colorSliderKnobDraggingFiller");
        colorSliderKnobHoveringTopLeft = resolveColor("colorSliderKnobHoveringTopLeft");
        colorSliderKnobHoveringBottomRight = resolveColor("colorSliderKnobHoveringBottomRight");
        colorSliderKnobHoveringFiller = resolveColor("colorSliderKnobHoveringFiller");
        colorSliderKnobMarkerLine = resolveColor("colorSliderKnobMarkerLine");

        colorTextNormal = resolveColor("colorTextNormal");
        colorTextInListNormal = resolveColor("colorTextInListNormal");
        colorTextDisabled = resolveColor("colorTextDisabled");

        colorTextFieldTopLeft = resolveColor("colorTextFieldTopLeft");
        colorTextFieldBottomRight = resolveColor("colorTextFieldBottomRight");
        colorTextFieldFiller = resolveColor("colorTextFieldFiller");
        colorTextFieldFocusedFiller = resolveColor("colorTextFieldFocusedFiller");
        colorTextFieldHoveringFiller = resolveColor("colorTextFieldHoveringFiller");
        colorTextFieldCursor = resolveColor("colorTextFieldCursor");

        colorEnergyBarTopLeft = resolveColor("colorEnergyBarTopLeft");
        colorEnergyBarBottomRight = resolveColor("colorEnergyBarBottomRight");
        colorEnergyBarHighEnergy = resolveColor("colorEnergyBarHighEnergy");
        colorEnergyBarLowEnergy = resolveColor("colorEnergyBarLowEnergy");
        colorEnergyBarSpacer = resolveColor("colorEnergyBarSpacer");
        colorEnergyBarText = resolveColor("colorEnergyBarText");

        colorListBackground = resolveColor("colorListBackground");
        colorListSeparatorLine = resolveColor("colorListSeparatorLine");
        colorListSelectedHighlightedGradient1 = resolveColor("colorListSelectedHighlightedGradient1");
        colorListSelectedHighlightedGradient2 = resolveColor("colorListSelectedHighlightedGradient2");
        colorListSelectedGradient1 = resolveColor("colorListSelectedGradient1");
        colorListSelectedGradient2 = resolveColor("colorListSelectedGradient2");
        colorListHighlightedGradient1 = resolveColor("colorListHighlightedGradient1");
        colorListHighlightedGradient2 = resolveColor("colorListHighlightedGradient2");

        colorBackgroundBevelBright = resolveColor("colorBackgroundBevelBright");
        colorBackgroundBevelDark = resolveColor("colorBackgroundBevelDark");
        colorBackgroundFiller = resolveColor("colorBackgroundFiller");

        colorToggleNormalBorderTopLeft = resolveColor("colorToggleNormalBorderTopLeft");
        colorToggleNormalBorderBottomRight = resolveColor("colorToggleNormalBorderBottomRight");
        colorToggleNormalFiller = resolveColor("colorToggleNormalFiller");
        colorToggleDisabledBorderTopLeft = resolveColor("colorToggleDisabledBorderTopLeft");
        colorToggleDisabledBorderBottomRight = resolveColor("colorToggleDisabledBorderBottomRight");
        colorToggleDisabledFiller = resolveColor("colorToggleDisabledFiller");
        colorToggleTextNormal = resolveColor("colorToggleTextNormal");
        colorToggleTextDisabled = resolveColor("colorToggleTextDisabled");

        colorCycleButtonTriangleNormal = resolveColor("colorCycleButtonTriangleNormal");
        colorCycleButtonTriangleDisabled = resolveColor("colorCycleButtonTriangleDisabled");

        colorButtonExternalBorder = resolveColor("colorButtonExternalBorder");
        colorButtonBorderTopLeft = resolveColor("colorButtonBorderTopLeft");
        colorButtonBorderBottomRight = resolveColor("colorButtonBorderBottomRight");
        colorButtonFiller = resolveColor("colorButtonFiller");
        colorButtonFillerGradient1 = resolveColor("colorButtonFillerGradient1");
        colorButtonFillerGradient2 = resolveColor("colorButtonFillerGradient2");

        colorButtonDisabledBorderTopLeft = resolveColor("colorButtonDisabledBorderTopLeft");
        colorButtonDisabledBorderBottomRight = resolveColor("colorButtonDisabledBorderBottomRight");
        colorButtonDisabledFiller = resolveColor("colorButtonDisabledFiller");
        colorButtonDisabledFillerGradient1 = resolveColor("colorButtonDisabledFillerGradient1");
        colorButtonDisabledFillerGradient2 = resolveColor("colorButtonDisabledFillerGradient2");

        colorButtonSelectedBorderTopLeft = resolveColor("colorButtonSelectedBorderTopLeft");
        colorButtonSelectedBorderBottomRight = resolveColor("colorButtonSelectedBorderBottomRight");
        colorButtonSelectedFiller = resolveColor("colorButtonSelectedFiller");
        colorButtonSelectedFillerGradient1 = resolveColor("colorButtonSelectedFillerGradient1");
        colorButtonSelectedFillerGradient2 = resolveColor("colorButtonSelectedFillerGradient2");

        colorButtonHoveringBorderTopLeft = resolveColor("colorButtonHoveringBorderTopLeft");
        colorButtonHoveringBorderBottomRight = resolveColor("colorButtonHoveringBorderBottomRight");
        colorButtonHoveringFiller = resolveColor("colorButtonHoveringFiller");
        colorButtonHoveringFillerGradient1 = resolveColor("colorButtonHoveringFillerGradient1");
        colorButtonHoveringFillerGradient2 = resolveColor("colorButtonHoveringFillerGradient2");
    }

}
