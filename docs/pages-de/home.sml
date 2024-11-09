Page {
	padding: "8"

	Column {
		padding: "8"
						
		Markdown {
			color: "#4C9BD9"
			text: "# Willkommen"
  		}
  		Markdown { 
      		fontSize: 16
			text :"
				Wir freuen uns, dass Sie die NoCode-Lösung 
				verwenden. Bitte beachten Sie, dass dies noch 
				eine frühe Version ist, die möglicherweise 
				einige Fehler enthält.
				Der Inhalt dieser App wurde mit dem 
				**NoCodeDesigner** erstellt, einer App für den 
				Desktop."
		}
		Spacer { amount: 16 }
		Markdown {
			color: "#4C9BD9"
			text: "### NoCodeDesigner"
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

		Spacer { weight: 1 }
		Button { label: "Über NoCode" link: "page:about"}
	}
}