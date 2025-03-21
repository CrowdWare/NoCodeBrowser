Page {
	padding: "8"
	title: "About"

	Column {
		padding: "8"

		Markdown { text: "# About this app" }
		Spacer { amount: 8 }
		Markdown {
			text: "You can design these kind of apps using the FreeBookDesigner.
				To test such an app you need this browser app." 
		}
		Spacer {amount: 8}
		Row {
			Markdown {
				text:"Open the menu (ham-
					burger icon) and 
					select Settings. 
					On the settings page 
					add a link.
					Give the link a unique title, 
					which will later be displayed 
					in the menu below Settings.
					Now enter the the url to you 
					web server."
				}
			Spacer {amount: 8}
			Image { src: "menu.png" scale:"fit"}
		}
		Spacer {amount: 8}
		Markdown { 
			text:"This url normally looks similar to
				**http://192.168.178.21:8000/** which is the path 
				to your local web server. This url will be displayed 
				when you start your Python based test web server 
				inside your project folder using: **python server.py** 
				Now just click on [Add Link] button and the link
				should be stored.
				When you now open your menu again you should see the newly created 
				menuitem.
				Click this item and your app will be load automatically.

				**The user interface for the browser app will disappear until you restart the browser.**"
		}
    		Spacer { weight: 1 }
   		Button { label: "Back Home" link: "page:app.home" }
	}
}