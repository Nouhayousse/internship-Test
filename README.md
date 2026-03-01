# Reservation API

API backend REST développée en Java 17 / Spring Boot pour la gestion de réservations de ressources partagées.

---

## Table des matières

- [Lancement rapide](#lancement-rapide)
- [Architecture du projet](#architecture-du-projet)
- [Choix fonctionnels et règles métier](#choix-fonctionnels-et-règles-métier)
- [Gestion de la concurrence](#gestion-de-la-concurrence)
- [Arbitrages techniques](#arbitrages-techniques)
- [Endpoints disponibles](#endpoints-disponibles)
- [Limites connues](#limites-connues)

---

## Lancement rapide

### Prérequis

- Java 17
- Docker Desktop

### 1. Démarrer PostgreSQL

```bash
docker run --name reservation-pg \
  -e POSTGRES_DB=reservations \
  -e POSTGRES_USER=resauser \
  -e POSTGRES_PASSWORD=resapass \
  -p 5432:5432 \
  -d postgres:15
```

### 2. Lancer l'application

```bash
./gradlew bootRun
```

### 3. Accéder à la documentation Swagger UI

```
http://localhost:8080/reservations-api/swagger-ui/index.html
```

Le schéma de base de données est créé automatiquement par **Flyway** au démarrage.

---

## Architecture du projet

```
src/main/java/pfa/
├── config/             # Configuration Spring (OpenAPI, Auditing)
├── controller/         # Couche REST — aucune logique métier
├── dto/
│   ├── request/        # Objets entrants (validation @Valid)
│   └── response/       # Objets sortants (contrat API)
├── entity/
│   ├── enums/          # ReservationStatus (CONFIRMED, CANCELLED)
│   ├── BaseEntity      # Champ id (UUID) — @MappedSuperclass
│   ├── AuditEntity     # Audit automatique (createdBy, createdOn, updatedBy, updatedOn)
│   ├── Reservation
│   ├── Resource
│   └── User
├── exception/          # Exceptions métier + GlobalExceptionHandler
├── mapper/             # MapStruct — conversion Entity ↔ DTO
├── repository/         # Spring Data JPA
└── service/
    ├── IService/       # Interfaces de service
    └── ImplService/    # Implémentations
```

### Patterns appliqués

**BaseEntity / AuditEntity (héritage JPA)**

Toutes les entités héritent de `AuditEntity` qui étend `BaseEntity` :

- `BaseEntity` : fournit le champ `id` de type UUID généré automatiquement (`@GeneratedValue(strategy = GenerationType.UUID)`)
- `AuditEntity` : fournit les 4 champs d'audit via Spring Data Auditing (`@EntityListeners(AuditingEntityListener.class)`) :
  - `createdBy` / `createdOn` — non modifiables après insertion (`updatable = false`)
  - `updatedBy` / `updatedOn` — non insérables à la création (`insertable = false`)

Cela évite de dupliquer ces champs dans chaque entité et garantit une traçabilité uniforme sur toute la base.

**Interface IService + ImplService**

Chaque domaine (User, Resource, Reservation) suit le pattern :

```
IUserService         (interface — contrat)
  └── UserServiceImpl  (implémentation — logique)
```

Ce pattern permet de découpler le contrat de l'implémentation, de faciliter les tests unitaires via mocks, et de respecter le principe d'inversion de dépendance (SOLID).

**MapStruct pour le mapping**

Les entités JPA ne sont jamais exposées directement en API. MapStruct génère à la compilation les classes de conversion entre entités et DTOs, avec des annotations `@Mapping` pour les champs imbriqués (ex: `user.name` → `userName` dans `ReservationResponse`).

---

## Choix fonctionnels et règles métier

### Ressources

- Une ressource peut être **activée ou désactivée**
- Une ressource désactivée ne peut pas être réservée
- Toute tentative de réservation sur une ressource inactive retourne une erreur `400`

### Utilisateurs

- L'email est unique — une tentative de création avec un email existant retourne `409`

### Réservations

| Règle | Détail |
|---|---|
| `start_time` dans le futur | Impossible de réserver dans le passé → `400` |
| `end_time` > `start_time` | Contrainte vérifiée applicativement et en base (`CHECK`) |
| Pas de chevauchement | Deux réservations actives ne peuvent pas se chevaucher sur la même ressource → `409` |
| Annulation soft | L'annulation passe le statut à `CANCELLED` — la ligne reste en base pour traçabilité |
| Réservation déjà annulée | Tenter d'annuler une réservation déjà `CANCELLED` → `400` |

### Statuts de réservation

```
CONFIRMED  →  CANCELLED
```

Pas de statut `PENDING` : une réservation est confirmée immédiatement à la création ou rejetée. Ce choix simplifie le modèle sans sacrifier la cohérence.

---

## Gestion de la concurrence

C'est le point central du système. Sans mécanisme de protection, deux requêtes simultanées peuvent toutes deux passer le check de disponibilité avant que l'une d'elles ait sauvegardé, créant un conflit :

```
Thread A : vérifie disponibilité → OK
Thread B : vérifie disponibilité → OK   ← avant que A ait commité
Thread A : sauvegarde → ✅
Thread B : sauvegarde → ✅ CONFLIT !
```

### Solution retenue : Lock pessimiste (`SELECT FOR UPDATE`)

Lors de la création d'une réservation, la ressource concernée est verrouillée en base avec `LockModeType.PESSIMISTIC_WRITE` :

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT r FROM Resource r WHERE r.id = :id")
Optional<Resource> findByIdForUpdate(@Param("id") UUID id);
```

Cela génère en SQL :
```sql
SELECT * FROM resources WHERE id = ? FOR UPDATE
```

Le thread B se met en attente au niveau base de données jusqu'à ce que le thread A ait commité. Il voit alors les données à jour, détecte le conflit, et lève une `ConflictException` (`409`).

### Algorithme de détection des chevauchements

```sql
start_time < :endTime AND end_time > :startTime
```

Cette condition est la formulation standard pour détecter tout chevauchement entre deux créneaux, quels que soient les cas (inclusion, intersection partielle gauche/droite). Les réservations `CANCELLED` sont exclues du check.

### Pourquoi pas le lock optimiste ?

Le lock optimiste (`@Version`) serait plus performant sous faible charge, mais nécessite une gestion de retry applicatif en cas de conflit (`ObjectOptimisticLockingFailureException`). Le lock pessimiste a été préféré pour sa simplicité de raisonnement et sa garantie forte, appropriée pour un système de réservations où les conflits sont prévisibles.

---

## Arbitrages techniques

| Décision | Justification |
|---|---|
| **PostgreSQL** | ACID natif, gestion des locks robuste, `gen_random_uuid()` intégré |
| **Flyway** | Migrations versionnées et reproductibles — le schéma est tracé dans Git |
| **`ddl-auto: validate`** | Hibernate ne touche jamais au schéma — Flyway en est le seul responsable |
| **UUID comme identifiant** | Évite l'énumération des IDs en API, compatible avec les architectures distribuées |
| **MapStruct** | Mapping vérifié à la compilation (contrairement à ModelMapper qui échoue au runtime) |
| **Records Java pour les DTOs** | Immutabilité native, pas de boilerplate, adapté à des objets de transport sans logique |
| **BaseEntity / AuditEntity** | Centralise id et audit — pas de duplication entre entités |
| **Pattern IService / ImplService** | Découplage contrat/implémentation, testabilité, respect du principe SOLID |
| **SpringDoc / Swagger UI** | Documentation de l'API vivante, testable directement sans outil externe |
| **H2 en test** | Pas de dépendance PostgreSQL dans les tests — isolation et rapidité d'exécution |

---

## Endpoints disponibles

La documentation complète et interactive est disponible sur Swagger UI au démarrage :
`http://localhost:8080/swagger-ui/index.html`

### Users — `/api/users`

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/users` | Créer un utilisateur |
| `GET` | `/api/users/{id}` | Récupérer un utilisateur par ID |

### Resources — `/api/resources`

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/resources` | Créer une ressource |
| `GET` | `/api/resources` | Lister toutes les ressources |
| `GET` | `/api/resources/{id}` | Récupérer une ressource par ID |
| `GET` | `/api/resources/{id}/availability` | Vérifier la disponibilité sur un créneau |
| `PATCH` | `/api/resources/{id}/deactivate` | Désactiver une ressource |

### Reservations — `/api/reservations`

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/reservations` | Créer une réservation |
| `GET` | `/api/reservations/{id}` | Récupérer une réservation par ID |
| `DELETE` | `/api/reservations/{id}` | Annuler une réservation (soft delete) |


<img width="455" height="428" alt="image" src="https://github.com/user-attachments/assets/e40bdf6b-3894-4daa-b2a6-32dc878e4e62" />



### Codes HTTP retournés

| Code | Signification |
|---|---|
| `201` | Ressource créée avec succès |
| `200` | Succès |
| `400` | Données invalides ou règle métier violée |
| `404` | Ressource / utilisateur / réservation introuvable |
| `409` | Conflit — créneau déjà réservé ou email déjà utilisé |

---

## Limites connues

- **Pas d'authentification** : les endpoints sont publics. Dans un contexte réel, Spring Security avec JWT serait la prochaine étape.
- **Pas de pagination** : les listes (`GET /api/resources`) retournent tous les éléments. À adresser avec `Pageable` si le volume croît.
- **Lock pessimiste et contention** : sous très forte charge sur une ressource populaire, le `SELECT FOR UPDATE` peut créer un goulot d'étranglement. L'alternative serait un lock optimiste avec retry, au prix d'une complexité applicative accrue.
- **`createdBy` / `updatedBy` non renseignés** : Spring Data Auditing nécessite un `AuditorAware` bean pour alimenter ces champs. Sans authentification en place, ils restent vides.
- **Pas de limite de durée maximale** : une réservation peut couvrir n'importe quelle plage temporelle. Une règle métier de durée max pourrait être ajoutée facilement dans le service.
