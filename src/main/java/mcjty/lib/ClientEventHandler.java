package mcjty.lib;

import mcjty.lib.client.DelayedRenderer;
import mcjty.lib.gui.IKeyReceiver;
import mcjty.lib.gui.WindowManager;
import mcjty.lib.gui.widgets.Widget;
import net.fabricmc.fabric.api.client.screen.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.gui.screens.Screen;

public final class ClientEventHandler {

    private static WindowManager modal(Screen screen) {

        if(screen instanceof IKeyReceiver c && c.getWindow()!=null) {
            var m=c.getWindow().getWindowManager();
            if(m!=null&&m.getModalWindows().findFirst().isPresent())return m;
        }
        return null;

    }

    private static Widget<?> focus(Screen screen) {

        if(screen instanceof IKeyReceiver c&&c.getWindow()!=null) {
            var w=c.getWindow();
            return w.getWindowManager()==null?w.getTextFocus():w.getWindowManager().getTextFocus();
        }
        return null;

    }

    public static void register() {

        ScreenEvents.AFTER_INIT.register((client,screen,width,height)-> {

            ScreenMouseEvents.allowMouseDrag(screen).register((s,e,dx,dy)-> {
                var m=modal(s);if(m==null)return true;m.mouseDragged(e.x(),e.y(),e.button());return false;
            });
            ScreenMouseEvents.allowMouseClick(screen).register((s,e)->modal(s)==null||!((IKeyReceiver)s).mouseClickedFromEvent(e.x(),e.y(),e.button()));
            ScreenMouseEvents.allowMouseRelease(screen).register((s,e)->modal(s)==null||!((IKeyReceiver)s).mouseReleasedFromEvent(e.x(),e.y(),e.button()));
            ScreenMouseEvents.allowMouseScroll(screen).register((s,x,y,dx,dy)->modal(s)==null||!((IKeyReceiver)s).mouseScrolledFromEvent(x,y,dx,dy));
            ScreenKeyboardEvents.allowCharType(screen).register((s,e)-> {
                if(focus(s)==null)return true;((IKeyReceiver)s).charTypedFromEvent((char)e.codepoint());return false;
            });
            ScreenKeyboardEvents.allowKeyPress(screen).register((s,e)-> {
                if(focus(s)==null)return true;((IKeyReceiver)s).keyTypedFromEvent(e.key(),e.scancode());return false;
            });
        });

        LevelRenderEvents.COLLECT_SUBMITS.register(context -> DelayedRenderer.render(context.poseStack(),context.submitNodeCollector(),context.levelState().cameraRenderState.pos));

    }

}
