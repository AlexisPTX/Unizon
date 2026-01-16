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

### Difficulté de navigation entre les écrans

Au début, la navigation entre plusieurs écrans était assez floue tout comme le concept. Cependant en regardant sur les forums, la documentation et avec l’aide de l’IA, nous avons pu progresser dans le développement des fonctionnalités en lien avec ce concept.

### La complexité de la mise en place des ViewModels

Le `ViewModel` est un point important de l'architecture MVVM, mais sa mise en place au début était compliquée, mais avec l'aide de l'IA et de forums, nous avons compris petit à petit comment le mettre en place jusqu'à obtenir une structure fonctionnelle.

- **La gestion de l'état :** Le principal défi n'est pas de créer la classe, mais de bien gérer l'état qu'elle expose. L'utilisation de `StateFlow` pour communiquer les données de manière réactive à l'interface Compose est puissante, mais demande de la rigueur ce qui était cause d'erreurs fréquentes.
Une erreur commise était de vouloir exposer trop d'états différents indépendamment. En me renseignant sur les forums, j'ai pu comprendre qu'il faut trouver un équilibre pour regrouper les données liées (ex : `cartItems`, `cartTotalPrice`, `cartItemCount` dans `CartViewModel`). Cela permet d'éviter de déclencher une mise à jour pour chaque variable une à une, au profit d'une mise à jour globale et cohérente de l'interface.

- **Le cycle de vie et la portée :** Toutes les opérations asynchrones (appels réseau, requêtes BDD) doivent être lancées dans le `viewModelScope` pour éviter des crashs fréquents que j'ai pu avoir, ce qu'il se passait, c'est que la coroutine continuait de s'exécuter alors que le ViewModel avait déjà été détruit.

En conclusion, bien que ces architectures demandent un investissement initial en termes de configuration et de compréhension, elles permettent de construire une base applicative facilement modifiable et améliorable.