package up.edu.br.promofy.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthService
{
    public static FirebaseUser getLogged() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static boolean isLogged() {
        return getLogged() != null;
    }
}
