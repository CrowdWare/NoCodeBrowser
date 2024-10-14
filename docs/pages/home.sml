Page {
	padding: "8"
 backgroundColor: "#FFFFFF"
 color: "#000000"

	Column {
		padding: "8"
						
		Markdown {
   color: "#4C9BD9"
			text: "# Welcome"
  }
  Markdown { color: "#000000" text :"
We are glad that you are using the NoCode Solution.
    Keep in mind that this is still an early version, which might include some glitches.
The content of this app has been designed using the NoCodeDesigner, which is an app for the desktop.
"
		}
  Markdown {
   color: "#4C9BD9"
			text: "### NoCodeDesigner"
  }
		Spacer { amount: 8 }
		Image { src: "desktop.png" }
  Markdown {color: "#000000" text: "
Start right now, create an ebook or even an app and create content for other people and they will help you getting closer to your dreams."}
  Spacer { weight: 1 }
		Button { label: "About NoCode" link: "page:about"}
		Button { label: "Introduction Video" link: "page:video"}
  
	}
}