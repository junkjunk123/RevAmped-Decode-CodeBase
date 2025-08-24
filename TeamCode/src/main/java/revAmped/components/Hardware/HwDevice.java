package revAmped.components.Hardware;

import androidx.annotation.NonNull;

/**
 * Created by zwang on 2/15/2016.
 */
public interface HwDevice
{
    String id = "";

    String getId();

    @NonNull
    String toString();

    void update();
}
