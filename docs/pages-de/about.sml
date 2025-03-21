Page {
	    padding: "8"
	    title: "Über"
	    scrollable: "true"
	
	    Column {
	    	    padding: "8"

	    	    Markdown { text: "# Über diese App" }
	    	    Spacer { amount: 8 }
	    	    Markdown {
	    	    	    text: "Sie können diese Art von Apps mit dem FreeBookDesigner entwerfen.
	    	    	    	    Um eine solche App zu testen, benötigen Sie diese Browser-App." 
	    	    }
	    	    
	    	    Spacer {amount: 8}
	    	    Row {
	    	    Markdown {
	    	    	    text:"Öffnen Sie das Menü (Ham-
	    	    	    	    burger-Symbol) und 
	    	    	    	    wählen Sie Einstellungen. 
	    	    	    	    Fügen Sie auf der Einstellungs-
	    	    	    	    seite einen Link hinzu.
	    	    	    	    Geben Sie dem Link einen 
	    	    	    	    eindeutigen Titel, 
	    	    	    	    der später im Menü unter 
	    	    	    	    Einstellungen angezeigt wird.
	    	    	    	    Geben Sie nun die URL zu 
	    	    	    	    Ihrem Webserver ein."
	    	    }
	    	    Spacer {amount: 8}
	    	    Image { src: "menu.png" scale:"fit"}
	    }
	    Spacer {amount: 8}
	    Markdown { 
	    	    text:"Diese URL sieht normalerweise ähnlich aus wie
	    	    **http://192.168.178.21:8000/**, was der Pfad 
	    	    zu Ihrem lokalen Webserver ist. 
	    	    Diese URL wird angezeigt, wenn Sie Ihren 
	    	    Python-basierten Test-Webserver 
	    	    innerhalb Ihres Projektordners starten mit: 
	    	    **python server.py** 
	    	    Klicken Sie nun einfach auf die Schaltfläche [Link hinzufügen], 
	    	    und der Link sollte gespeichert werden.

	    	    Wenn Sie nun Ihr Menü erneut öffnen, sollten Sie das neu erstellte Menüelement
sehen.
	    	    Klicken Sie auf dieses Element und Ihre App wird automatisch geladen.

	    	    **Die Benutzeroberfläche der Browser-App verschwindet, bis Sie den Browser neu starten. Oder in der BuchApp so lange auf die Oberfläche drücken und gedrückt halten, bis ein Dialog aufpoppt, mit dem sie zurück navigieren können.**"
	    	    }
	    	    Spacer { amount: 8 }
	    	    Button { label: "Zurück zur Startseite" link: "page:app.home" }
	    }
}