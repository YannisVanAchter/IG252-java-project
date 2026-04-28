# IG252-java-project

## Lancer le projet

Pour lancer le projet, assurez-vous d'être en possession de l'application Docker pour gérer la conteneurisation.

### Gestion des variables d'environnement

Vous trouverez les variables d'environnement dans le fichier [.env.test](./.env.test). 

Copiez le contenu du fichier dans un nouveau fichier intitulé `.env` à la racine du projet. 

Une fois fait, faites attention a changer les valeurs des variables `MYSQL_ROOT_PASSWORD`, `MYSQL_PASSWORD` et `TZ` en fonction de votre situation peronnelle.

### Build and start container

```powershell
IG252-java-project>docker-compose up --build
```

### Accèder au projet lancé

Aller à l'adresse suivante une fois lancé: [http://localhost:6901/vnc.html](http://localhost:6901/vnc.html)
