package intercomm.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import elemental.client.Browser;
import elemental.events.MouseEvent;
import elemental.html.Console;
import elemental.html.Storage;
import elemental.html.StorageEvent;
import elemental.html.Window;

/** Simple example to show how to inter-communicate between tabs. */
public class App implements EntryPoint {
    private static final Window wnd = Browser.getWindow();
    private static final Console L = wnd.getConsole();

    public void onModuleLoad() {
        Storage storage = Browser.getWindow().getLocalStorage();

        // text box which synchronizes its text with the active tab
        HorizontalPanel panel = new HorizontalPanel();
        panel.add(new InlineLabel("msg:"));
        TextBox text = new TextBox(); panel.add(text);
        text.addKeyUpHandler(e -> {
            String msg = text.getValue();
            L.log("msg '" + msg + "'");
            storage.setItem("intercomm.msg", msg);
        });
        RootPanel.get().add(panel);

        // floating circle which shows the mouse position of the active tab
        SimplePanel box = new SimplePanel();
        box.getElement().setAttribute("style", ""
                + "position: absolute;"
                + "background-color: #FF9800;"
                + "width: 10px;"
                + "height: 10px;"
                + "border-radius: 10px;");
        RootPanel.get().add(box);
        wnd.addEventListener("mousemove", e -> {
            MouseEvent me = (MouseEvent) e;
            storage.setItem("intercomm.mouse", me.getClientX() + "," + me.getClientY());
        });

        // listeners on the passive tabs
        wnd.addEventListener("storage", e -> {
            L.log(e);
            StorageEvent se = (StorageEvent) e;
            if ("intercomm.msg".equals(se.getKey())) {
                text.setValue(se.getNewValue());
            }
            if ("intercomm.mouse".equals(se.getKey())) {
                String[] xy = se.getNewValue().split(",");
                box.getElement().getStyle().setProperty("left", xy[0] + "px");
                box.getElement().getStyle().setProperty("top", xy[1] + "px");
            }
        });
    }
}
