package up.edu.br.promofy.helpers;

import android.content.Context;

public class ApplicationHelper {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        ApplicationHelper.context = context;
    }
}
