# Unizon - Application e-commerce

Ce document présente l'application "Unizon", une application Android simulant une boutique en ligne. Il détaille les fonctionnalités ainsi que les choix techniques faits pour son implémentation.

## 1. Fonctionnalités de l'application

L'application offre une expérience d'achat complète, de la consultation des produits à la simulation d'une commande.

### Consultation des produits
- **Affichage de la liste :** L'écran principal affiche une liste complète des produits disponibles, récupérés depuis l'API FakeStore.
- **Filtres :** L'utilisateur peut filtrer les produits affichés en sélectionnant une catégorie spécifique (ex : "electronics", "jewelery").
- **Tri :** Une option de tri permet d'organiser la liste des produits selon plusieurs critères :
    - Prix (croissant ou décroissant)
    - Note (de la plus haute à la plus basse)
- **Détail du produit :** Un clic sur un produit mène à un écran de détail affichant toutes ses informations : image, description complète, prix et note.
- **Rafraîchissement :** L'utilisateur peut rafraîchir la liste des produits en "tirant" l'écran vers le bas (`pull-to-refresh`).

### Gestion du panier
- **Ajout et modification :** Depuis la liste ou l'écran de détail, l'utilisateur peut ajouter des produits à son panier. La quantité d'un même produit peut être augmentée ou diminuée.
- **Visualisation :** Une icône de panier est affichée sur la barre supérieure, avec une pastille indiquant le nombre d'articles dans le panier. Cette icône permet également de regarder le contenu du panier.
- **Récapitulatif :** L'écran du panier liste tous les articles, leur quantité, le prix unitaire et le prix total. Il affiche également le montant total du panier.
- **Suppression :** L'utilisateur peut supprimer un article du panier, quelle que soit sa quantité en cliquant sur l'icône de poubelle.

### Favoris
- **Ajout et suppression :** Chaque produit peut être marqué comme "favori" via une icône en forme de cœur.
- **Liste des favoris :** Un onglet dans la barre de navigation inférieure permet d'accéder à un écran qui regroupe tous les produits favoris de l'utilisateur.
- **Compteur :** Une pastille sur l'icône des favoris indique le nombre total de produits marqués en favori.

### Commandes
- **Validation du panier :** L'application simule un processus d'achat via un écran de validation de commande.
- **Historique des achats :** Les commandes passées sont enregistrées et peuvent être consultées dans un écran "Historique", accessible depuis la barre de navigation en bas.

### Persistance des données
- Le panier, les favoris et l'historique des commandes sont sauvegardés localement. L'utilisateur retrouve donc ses informations même après avoir fermé et rouvert l'application.

## 2. Implémentation Technique

L'application est développée en essayant de suivre les pratiques modernes pour le développement Android natif.

- **Interface Utilisateur :** Jetpack Compose simplifie les constructions des interfaces utilisateur, de plus ce langage est plus facilement lisible et compréhensible que l'utilisation du XML.
- **Architecture :** Structuration MVVM (Model-View-ViewModel), qui sépare la logique de l'interface utilisateur de la logique métier. Cette architecture permet une meilleure organisation structurelle, le code est plus lisible, mieux architecturé, car respectant des standards bien définis et plus faciles à maintenir. Cette architecture est structurée de la façon suivante :
    - **Views (UI) :** Les fonctions @Composable sont "passives" ; elles observent l'état exposé par le ViewModel et notifient ce dernier des actions utilisateur.
    - **ViewModels :** Il sert de pont. Il récupère les données des modèles, les transforme en un état (State) consommable par l'interface et survit aux rotations d'écran.
    - **Models (Data) :** Centralise la logique de données, qu'elle vienne du réseau (Retrofit) ou du stockage local (Room).

- **Réseau :**
    - **Retrofit :** Une bibliothèque de client HTTP permettant de communiquer avec l'API REST.
    - **Gson :** Pour la sérialisation/désérialisation des objets JSON provenant de l'API.

- **Base de données locale :**
    - **Room :** Une bibliothèque qui fournit une surcouche d'abstraction au-dessus de SQLite. Elle simplifie la communication avec la base de données en convertissant directement les données en objet Kotlin pouvant être utilisés par l'application. Elle est utilisée pour stocker :
        - Les articles du panier (`cart_items`).
        - Les produits favoris (`favorite_items`).
        - Les commandes et leurs articles (`orders`, `order_items`).

- **Opérations Asynchrones :**
    - **Coroutines :** Pour gérer les tâches de fond (appels réseau, accès à la base de données) de manière efficace et non bloquante.
    - **Flow :** Pour observer les changements dans la base de données Room de manière réactive et mettre à jour un élément précis automatiquement sans avoir à recharger tout l'écran.

- **Navigation :**
    - **Jetpack Navigation :** Pour gérer la navigation entre les différents écrans de l'application.

- **Chargement d'images :**
    - **Coil (Coroutine Image Loader) :** Une bibliothèque légère et rapide pour charger et afficher des images depuis des URLs.

- **Thème :**
    - **Material 3 :** L'application utilise les composants et les principes de Material Design 3.
    - **Thème personnalisé :** Une palette de couleurs personnalisée a été appliquée à l'ensemble de l'application pour une pointe d'originalité dans le projet.

## 3. Retour d'expérience

Ce projet a été une opportunité pour mettre en pratique les concepts clés de l'écosystème Android. Voici quelques retours sur les défis rencontrés et les leçons apprises.

### La complexité des ViewModels et de la Navigation

La mise en place d'une architecture réactive avec `ViewModel` et `Navigation Compose` est puissante, mais présente des défis conceptuels importants.

- **La gestion de l'état (`StateFlow`) :** Le principal défi n'est pas de créer la classe `ViewModel`, mais de bien modéliser l'état qu'elle expose. L'utilisation de `StateFlow` pour communiquer les données de manière réactive à l'interface est efficace, mais demande de la rigueur.
    - **Point bloquant :** Une erreur fréquente est de vouloir exposer trop d'états (`StateFlow`) différents et indépendants. Il faut trouver un équilibre pour regrouper les données liées (ex: `cartItems`, `cartTotalPrice`, `cartItemCount` dans `CartViewModel`) en un ou plusieurs états cohérents, afin d'éviter des mises à jour de l'interface trop fréquentes et désynchronisées.

- **Le cycle de vie et la portée (`viewModelScope`) :** Toutes les opérations asynchrones (appels réseau, requêtes BDD) doivent être lancées dans le `viewModelScope`. Oublier cette règle est une source de bugs difficiles à tracer, comme des fuites de mémoire ou des crashs, car la coroutine peut continuer de s'exécuter alors que l'écran et son `ViewModel` ont déjà été détruits.

- **La gestion de la pile de navigation (`Back Stack`) :** La navigation, notamment avec une barre inférieure, n'est pas triviale.
Un bug avait lieu lors de la redirection vers l'historique après une commande. Le retour vers l'écran des produits ne fonctionnait plus correctement, c'est-à-dire que l'on voyait l'historique d'après commande plutôt que de voir la liste des produits. La solution a été de manipuler explicitement la pile de navigation (`back stack`) avec `popUpTo` pour s'assurer que l'état de la navigation reste propre et prévisible, en évitant l'empilement d'écrans qui ne devraient pas l'être.

En conclusion, bien que ces architectures demandent un investissement initial en termes de configuration et de compréhension, elles permettent de construire une base applicative facilement modifiable et améliorable.

En conclusion, bien que ces architectures demandent un investissement initial en termes de configuration et de compréhension, elles permettent de construire une base applicative facilement modifiable et améliorable.