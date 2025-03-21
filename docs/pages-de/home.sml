Page {
    padding: "8"
	   title: "NoCodeLibMobile"

    Column {
		      padding: "8"
						
        Markdown {
            color: "#4C9BD9"
            text: "# Willkommen"
  		}
  		Markdown { 
      		fontSize: 16
			text :"
				Wir freuen uns, dass Sie die FreeBook-Lösung 
				verwenden. Bitte beachten Sie, dass dies noch 
				eine frühe Version ist, die möglicherweise 
				einige Fehler enthält.
				Der Inhalt dieser App wurde mit dem 
				**FreeBookDesigner** erstellt, einer App für den 
				Desktop."
		}
		Spacer { amount: 16 }
		Markdown {
			color: "#4C9BD9"
			text: "### FreeBookDesigner"
 		}
		Spacer { amount: 8 }
		Image { src: "desktop.png" }
		Spacer { amount: 16 }
		Markdown {
			fontSize: 16
			text: "Starten Sie jetzt, erstellen Sie ein E-Book oder 
				sogar eine App und erstellen Sie Inhalte für 
				andere Menschen, die Ihnen helfen werden, 
				Ihren Träumen näher zu kommen."}
		Spacer { weight: 1  }
		Button { label: "Bücher" link: "page:app.books"}
		Spacer { amount:  4}
		Button { label: "Über FreeBook" link: "page:app.about"}
	}
}