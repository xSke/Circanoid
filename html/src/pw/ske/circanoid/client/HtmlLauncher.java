package pw.ske.circanoid.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import pw.ske.circanoid.Circanoid;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig() {
        GwtApplicationConfiguration gwtApplicationConfiguration = new GwtApplicationConfiguration(800, 600);
        gwtApplicationConfiguration.antialiasing = true;
        return gwtApplicationConfiguration;
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new Circanoid();
    }
}
