# TP 9 : Authentification en mémoire avec Spring Security

Projet réalisé dans le cadre du cours **Développement JakartaEE : Spring (ID: 17)** — **Lab 208 : TP 9 : Authentification en mémoire avec Spring Security**.

---

## 📌 Objectifs et Compétences du Lab

Ce projet met en œuvre de bout en bout l'authentification en mémoire avec Spring Security dans une application Spring Boot / Thymeleaf :
- Configuration personnalisée de la chaîne de filtres de sécurité (`SecurityFilterChain`).
- Gestion des utilisateurs en mémoire avec `InMemoryUserDetailsManager`.
- Hachage et protection des mots de passe avec `BCryptPasswordEncoder`.
- Contrôle d'accès basé sur les rôles (`ROLE_USER`, `ROLE_ADMIN`).
- Personnalisation de l'interface de connexion (`/login`) avec support CSRF et notifications.
- Gestion de la déconnexion sécurisée (`/logout`).

---

## 🏗️ Architecture du Projet

```
spring-security-demo/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── ma/fstg/security/
    │   │       ├── SpringSecurityDemoApplication.java
    │   │       ├── config/
    │   │       │   ├── PasswordEncoderConfig.java
    │   │       │   ├── SecurityConfig.java
    │   │       │   └── UserDetailsConfig.java
    │   │       └── web/
    │   │           └── HomeController.java
    │   └── resources/
    │       ├── application.properties
    │       ├── static/
    │       │   └── css/
    │       │       └── style.css
    │       └── templates/
    │           ├── admin_dashboard.html
    │           ├── index.html
    │           ├── login.html
    │           └── user_dashboard.html
    └── test/
        └── java/
            └── ma/fstg/security/
                ├── SecurityIntegrationTests.java
                └── SpringSecurityDemoApplicationTests.java
```

---

## 👥 Comptes Utilisateurs Configurés

| Nom d'utilisateur | Mot de passe (en clair) | Hachage | Rôles attribués | Accès autorisés |
| :--- | :--- | :--- | :--- | :--- |
| **`user`** | `1111` | BCrypt (`BCryptPasswordEncoder`) | `ROLE_USER` | `/`, `/user/dashboard` |
| **`admin`** | `1234` | BCrypt (`BCryptPasswordEncoder`) | `ROLE_ADMIN`, `ROLE_USER` | `/`, `/user/dashboard`, `/admin/dashboard` |

---

## 🔒 Règles de Sécurité (`SecurityFilterChain`)

1. **Route publique `/`** : Accessible à tous les visiteurs sans authentification (`permitAll`).
2. **Ressources statiques `/css/**`, `/js/**`, `/images/**`** : Accessibles sans restriction.
3. **Espace Utilisateur `/user/**`** : Réservé aux utilisateurs disposant du rôle `ROLE_USER`.
4. **Espace Administrateur `/admin/**`** : Strictement réservé aux utilisateurs disposant du rôle `ROLE_ADMIN`.
5. **Toute autre route** : Nécessite une authentification préalable (`authenticated()`).
6. **Formulaire de connexion** : Route `/login` personnalisée accessible à tous.
7. **Déconnexion** : Route `/logout` avec invalidation de session, suppression du cookie `JSESSIONID` et redirection vers `/`.

---

## 🚀 Lancement et Tests

### 1. Compilation et exécution des tests automatisés
```bash
mvn clean test
```

Tous les scénarios d'authentification et de contrôle d'accès sont validés :
- Accès public à la page d'accueil.
- Redirection automatique vers `/login` pour les pages protégées.
- Connexion avec credentials valides et refus en cas de mauvais mot de passe.
- Autorisation d'accès à l'espace utilisateur pour `user`.
- Refus HTTP 403 Forbidden sur l'espace admin pour `user`.
- Autorisation complète pour le compte `admin` sur les deux espaces.
- Déconnexion et redirection.

### 2. Démarrage de l'application
```bash
mvn spring-boot:run
```
L'application démarre sur le port `8080` : [http://localhost:8080/](http://localhost:8080/).
