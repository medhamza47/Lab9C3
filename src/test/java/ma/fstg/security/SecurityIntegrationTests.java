package ma.fstg.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Scénario 1: Accès public à la page d'accueil '/' autorisé sans authentification")
    void shouldAllowPublicAccessToIndex() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("Scénario 2: Accès à '/user/dashboard' sans authentification redirige vers '/login'")
    void shouldRedirectAnonymousUserToLoginForUserDashboard() throws Exception {
        mockMvc.perform(get("/user/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Scénario 3: Accès à '/admin/dashboard' sans authentification redirige vers '/login'")
    void shouldRedirectAnonymousUserToLoginForAdminDashboard() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Scénario 4: Connexion réussie avec le compte 'user' / '1111' via le formulaire de login")
    void shouldAuthenticateValidUser() throws Exception {
        mockMvc.perform(formLogin("/login").user("user").password("1111"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("user").withRoles("USER"));
    }

    @Test
    @DisplayName("Scénario 5: Échec de connexion avec des identifiants invalides")
    void shouldFailAuthenticationWithBadCredentials() throws Exception {
        mockMvc.perform(formLogin("/login").user("user").password("badpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("Scénario 6: Utilisateur avec rôle 'USER' a accès à '/user/dashboard'")
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldAllowUserRoleToAccessUserDashboard() throws Exception {
        mockMvc.perform(get("/user/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("user_dashboard"));
    }

    @Test
    @DisplayName("Scénario 7: Utilisateur avec rôle 'USER' reçoit 403 Forbidden sur '/admin/dashboard'")
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldDenyUserRoleFromAccessingAdminDashboard() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Scénario 8: Connexion réussie avec le compte 'admin' / '1234'")
    void shouldAuthenticateValidAdmin() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("admin").withRoles("ADMIN", "USER"));
    }

    @Test
    @DisplayName("Scénario 9: Utilisateur avec rôle 'ADMIN' a accès à '/admin/dashboard'")
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    void shouldAllowAdminRoleToAccessAdminDashboard() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_dashboard"));
    }

    @Test
    @DisplayName("Scénario 10: Utilisateur avec rôle 'ADMIN' a aussi accès à '/user/dashboard'")
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    void shouldAllowAdminRoleToAccessUserDashboard() throws Exception {
        mockMvc.perform(get("/user/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("user_dashboard"));
    }

    @Test
    @DisplayName("Scénario 11: Déconnexion '/logout' redirige vers '/'")
    void shouldLogoutSuccessfully() throws Exception {
        mockMvc.perform(logout("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }
}
