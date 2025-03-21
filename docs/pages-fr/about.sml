Page {
	padding: "8"
	title: "À propos"

	Column {
		padding: "8"

		Markdown { text: "# À propos de cette application" }
		Spacer { amount: 8 }
		Markdown {
			text: "Vous pouvez concevoir ce type d'applications avec le FreeBookDesigner.
				Pour tester une application de ce type, vous avez besoin de cette application de navigateur." 
		}
		Spacer {amount: 8}
		Row {
			Markdown {
				text:"Ouvrez le menu (icône de 
					hamburger) et sélectionnez 
					Paramètres.
					Dans la page des paramètres, 
					ajoutez un lien.
					Donnez un titre unique au lien, 
					qui s'affichera dans le menu 
					sous Paramètres.
					Entrez maintenant l'URL de votre 
					serveur web."
				}
			Spacer {amount: 8}
			Image { src: "menu.png" scale:"fit"}
		}
		Spacer {amount: 8}
		Markdown { 
			text:"Cette URL ressemble généralement à 
				**http://192.168.178.21:8000/**, qui est le chemin
				de votre serveur web local.
				Cette URL apparaît lorsque vous lancez votre
				serveur web de test basé sur Python dans votre 
				répertoire de projet avec : **python server.py** 
				Cliquez maintenant simplement sur le bouton [Ajouter un lien], 
				et le lien devrait être sauvegardé.
				Lorsque vous rouvrirez le menu, vous devriez voir le nouvel élément.
				Cliquez sur cet élément et votre application se chargera automatiquement.

				**L'interface utilisateur de l'application de navigateur disparaîtra jusqu'à ce que vous redémarriez le navigateur.**"
		}
    		Spacer { weight: 1 }
   		Button { label: "Retour à la page d'accueil" link: "page:app.home" }
	}
}