package mcjty.lib.base;

import mcjty.lib.varia.Logging;
import net.minecraftforge.common.config.Configuration;

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

    public static int colorTextNormal = 0xFF303030;
    public static int colorTextInListNormal = 0xFF151515;
    public static int colorTextDisabled = 0xFFa0a0a0;

    public static int colorTextFieldFiller = 0xffc6c6c6;
    public static int colorTextFieldFocusedFiller = 0xffeeeeee;
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

    public static int colorToggleNormalBorderBottomRight = 0xff777777;
    public static int colorToggleNormalBorderTopLeft = 0xffeeeeee;
    public static int colorToggleNormalFiller = 0xffc6c6c6;
    public static int colorToggleDisabledBorderBottomRight = 0xff777777;
    public static int colorToggleDisabledBorderTopLeft = 0xffeeeeee;
    public static int colorToggleDisabledFiller = 0xffc6c6c6;
    public static int colorToggleTextNormal = 0xFF303030;
    public static int colorToggleTextDisabled = 0xFFa0a0a0;

    public static int colorButtonExternalBorder = 0xff000000;

    public static int colorCycleButtonTriangleNormal = 0xff000000;
    public static int colorCycleButtonTriangleDisabled = 0xff888888;

    public static int colorButtonBorderBottomRight = 0xff777777;
    public static int colorButtonBorderTopLeft = 0xffeeeeee;
    public static int colorButtonFiller = 0xffc6c6c6;
    public static int colorButtonFillerGradient1 = 0xffb1b1b1;
    public static int colorButtonFillerGradient2 = 0xffe1e1e1;

    public static int colorButtonDisabledBorderBottomRight = 0xff777777;
    public static int colorButtonDisabledBorderTopLeft = 0xffeeeeee;
    public static int colorButtonDisabledFiller = 0xffc6c6c6;
    public static int colorButtonDisabledFillerGradient1 = 0xffb1b1b1;
    public static int colorButtonDisabledFillerGradient2 = 0xffe1e1e1;

    public static int colorButtonSelectedBorderTopLeft = 0xff5c669d;
    public static int colorButtonSelectedFiller = 0xff7f89bf;
    public static int colorButtonSelectedFillerGradient1 = 0xff6a74aa;
    public static int colorButtonSelectedFillerGradient2 = 0xff949ed4;
    public static int colorButtonSelectedBorderBottomRight = 0xffbcc5ff;

    public static void init(Configuration cfg) {

        colorSliderTopLeft = getSetting(cfg, "colorSliderTopLeft", colorSliderTopLeft, "Color: slider top left border");
        colorSliderBottomRight = getSetting(cfg, "colorSliderBottomRight", colorSliderBottomRight, "Color: slider bottom right border");
        colorSliderFiller = getSetting(cfg, "colorSliderFiller", colorSliderFiller, "Color: slider background");
        colorSliderKnobTopLeft = getSetting(cfg, "colorSliderKnobTopLeft", colorSliderKnobTopLeft, "Color: slider knob top left border");
        colorSliderKnobBottomRight = getSetting(cfg, "colorSliderKnobBottomRight", colorSliderKnobBottomRight, "Color: slider knob bottom right border");
        colorSliderKnobFiller = getSetting(cfg, "colorSliderKnobFiller", colorSliderKnobFiller, "Color: slider knob background");
        colorSliderKnobDraggingTopLeft = getSetting(cfg, "colorSliderKnobDraggingTopLeft", colorSliderKnobDraggingTopLeft, "Color: slider knob top left border while dragging");
        colorSliderKnobDraggingBottomRight = getSetting(cfg, "colorSliderKnobDraggingBottomRight", colorSliderKnobDraggingBottomRight, "Color: slider knob bottom right border while dragging");
        colorSliderKnobDraggingFiller = getSetting(cfg, "colorSliderKnobDraggingFiller", colorSliderKnobDraggingFiller, "Color: slider knob background while dragging");
        colorSliderKnobMarkerLine = getSetting(cfg, "colorSliderKnobMarkerLine", colorSliderKnobMarkerLine, "Color: slider knob little marker lines");

        colorTextNormal = getSetting(cfg, "colorTextNormal", colorTextNormal, "Color: text normal");
        colorTextInListNormal = getSetting(cfg, "colorTextInListNormal", colorTextInListNormal, "Color: text as used in lists");
        colorTextDisabled = getSetting(cfg, "colorTextDisabled", colorTextDisabled, "Color: text disabled");

        colorTextFieldTopLeft = getSetting(cfg, "colorTextFieldTopLeft", colorTextFieldTopLeft, "Color: textfield top left border");
        colorTextFieldBottomRight = getSetting(cfg, "colorTextFieldBottomRight", colorTextFieldBottomRight, "Color: textfield bottom right border");
        colorTextFieldFiller = getSetting(cfg, "colorTextFieldFiller", colorTextFieldFiller, "Color: textfield background");
        colorTextFieldFocusedFiller = getSetting(cfg, "colorTextFieldFocusedFiller", colorTextFieldFocusedFiller, "Color: textfield backbground while focused");
        colorTextFieldCursor = getSetting(cfg, "colorTextFieldCursor", colorTextFieldCursor, "Color: textfield cursor");

        colorEnergyBarTopLeft = getSetting(cfg, "colorEnergyBarTopLeft", colorEnergyBarTopLeft, "Color: energy bar top left border");
        colorEnergyBarBottomRight = getSetting(cfg, "colorEnergyBarBottomRight", colorEnergyBarBottomRight, "Color: energy bar bottom right border");
        colorEnergyBarHighEnergy = getSetting(cfg, "colorEnergyBarHighEnergy", colorEnergyBarHighEnergy, "Color: energy bar high energy level");
        colorEnergyBarLowEnergy = getSetting(cfg, "colorEnergyBarLowEnergy", colorEnergyBarLowEnergy, "Color: energy bar low energy level");
        colorEnergyBarSpacer = getSetting(cfg, "colorEnergyBarSpacer", colorEnergyBarSpacer, "Color: energy bar spacer (between every energy level bar)");
        colorEnergyBarText = getSetting(cfg, "colorEnergyBarText", colorEnergyBarText, "Color: energy bar text");

        colorListBackground = getSetting(cfg, "colorListBackground", colorListBackground, "Color: list background");
        colorListSeparatorLine = getSetting(cfg, "colorListSeparatorLine", colorListSeparatorLine, "Color: list separator line");
        colorListSelectedHighlightedGradient1 = getSetting(cfg, "colorListSelectedHighlightedGradient1", colorListSelectedHighlightedGradient1, "Color: list selected and highlighted gradient");
        colorListSelectedHighlightedGradient2 = getSetting(cfg, "colorListSelectedHighlightedGradient2", colorListSelectedHighlightedGradient2, "Color: list selected and highlighted gradient");
        colorListSelectedGradient1 = getSetting(cfg, "colorListSelectedGradient1", colorListSelectedGradient1, "Color: list selected gradient");
        colorListSelectedGradient2 = getSetting(cfg, "colorListSelectedGradient2", colorListSelectedGradient2, "Color: list selected gradient");
        colorListHighlightedGradient1 = getSetting(cfg, "colorListHighlightedGradient1", colorListHighlightedGradient1, "Color: list highlighted gradient");
        colorListHighlightedGradient2 = getSetting(cfg, "colorListHighlightedGradient2", colorListHighlightedGradient2, "Color: list highlighted gradient");

        colorBackgroundBevelBright = getSetting(cfg, "colorBackgroundBevelBright", colorBackgroundBevelBright, "Color: standard bevel bright border color");
        colorBackgroundBevelDark = getSetting(cfg, "colorBackgroundBevelDark", colorBackgroundBevelDark, "Color: standard bevel dark border color");
        colorBackgroundFiller = getSetting(cfg, "colorBackgroundFiller", colorBackgroundFiller, "Color: standard background color");

        colorToggleNormalBorderTopLeft = getSetting(cfg, "colorToggleNormalBorderTopLeft", colorToggleNormalBorderTopLeft, "Color: toggle button normal top left border");
        colorToggleNormalBorderBottomRight = getSetting(cfg, "colorToggleNormalBorderBottomRight", colorToggleNormalBorderBottomRight, "Color: toggle button normal bottom right border");
        colorToggleNormalFiller = getSetting(cfg, "colorToggleNormalFiller", colorToggleNormalFiller, "Color: toggle button normal background");
        colorToggleDisabledBorderTopLeft = getSetting(cfg, "colorToggleDisabledBorderTopLeft", colorToggleDisabledBorderTopLeft, "Color: toggle button disabled top left border");
        colorToggleDisabledBorderBottomRight = getSetting(cfg, "colorToggleDisabledBorderBottomRight", colorToggleDisabledBorderBottomRight, "Color: toggle button disabled bottom right border");
        colorToggleDisabledFiller = getSetting(cfg, "colorToggleDisabledFiller", colorToggleDisabledFiller, "Color: toggle button disabled background");
        colorToggleTextNormal = getSetting(cfg, "colorToggleTextNormal", colorToggleTextNormal, "Color: toggle button normal text");
        colorToggleTextDisabled = getSetting(cfg, "colorToggleTextDisabled", colorToggleTextDisabled, "Color: toggle button disabled text");

        colorCycleButtonTriangleNormal = getSetting(cfg, "colorCycleButtonTriangleNormal", colorCycleButtonTriangleNormal, "Color: cycle button small triangle");
        colorCycleButtonTriangleDisabled = getSetting(cfg, "colorCycleButtonTriangleDisabled", colorCycleButtonTriangleDisabled, "Color: cycle button disabled small triangle");

        colorButtonExternalBorder = getSetting(cfg, "colorButtonExternalBorder", colorButtonExternalBorder, "Color: external border around buttons and some other components");
        colorButtonBorderTopLeft = getSetting(cfg, "colorButtonBorderTopLeft", colorButtonBorderTopLeft, "Color: button top left border");
        colorButtonBorderBottomRight = getSetting(cfg, "colorButtonBorderBottomRight", colorButtonBorderBottomRight, "Color: button bottom right border");
        colorButtonFiller = getSetting(cfg, "colorButtonFiller", colorButtonFiller, "Color: button background");
        colorButtonFillerGradient1 = getSetting(cfg, "colorButtonFillerGradient1", colorButtonFillerGradient1, "Color: button background gradient");
        colorButtonFillerGradient2 = getSetting(cfg, "colorButtonFillerGradient2", colorButtonFillerGradient2, "Color: button background gradient");

        colorButtonDisabledBorderTopLeft = getSetting(cfg, "colorButtonDisabledBorderTopLeft", colorButtonDisabledBorderTopLeft, "Color: disabled button top left border");
        colorButtonDisabledBorderBottomRight = getSetting(cfg, "colorButtonDisabledBorderBottomRight", colorButtonDisabledBorderBottomRight, "Color: disabled button bottom right border");
        colorButtonDisabledFiller = getSetting(cfg, "colorButtonDisabledFiller", colorButtonDisabledFiller, "Color: disabled button background");
        colorButtonDisabledFillerGradient1 = getSetting(cfg, "colorButtonDisabledFillerGradient1", colorButtonDisabledFillerGradient1, "Color: disabled button background gradient");
        colorButtonDisabledFillerGradient2 = getSetting(cfg, "colorButtonDisabledFillerGradient2", colorButtonDisabledFillerGradient2, "Color: disabled button background gradient");

        colorButtonSelectedBorderTopLeft = getSetting(cfg, "colorButtonSelectedBorderTopLeft", colorButtonSelectedBorderTopLeft, "Color: selected button top left border");
        colorButtonSelectedBorderBottomRight = getSetting(cfg, "colorButtonSelectedBorderBottomRight", colorButtonSelectedBorderBottomRight, "Color:selected  button bottom right border");
        colorButtonSelectedFiller = getSetting(cfg, "colorButtonSelectedFiller", colorButtonSelectedFiller, "Color: selected button background");
        colorButtonSelectedFillerGradient1 = getSetting(cfg, "colorButtonSelectedFillerGradient1", colorButtonSelectedFillerGradient1, "Color: selected button background gradient");
        colorButtonSelectedFillerGradient2 = getSetting(cfg, "colorButtonSelectedFillerGradient2", colorButtonSelectedFillerGradient2, "Color: selected button background gradient");
    }

    private static int getSetting(Configuration cfg, String settingName, int setting, String comment) {
        try {
            return 0xff000000 + Integer.parseInt(cfg.get(CATEGORY_STYLE, settingName, Integer.toHexString(setting & 0xffffff), comment).getString(), 16);
        } catch (NumberFormatException e) {
            Logging.logError("Error parsing configuration option: " + settingName + "!");
            return 0;
        }
    }
}
