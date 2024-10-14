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
    Keep in mind that this is still an early version, which might include some glitches."
		}
  
		Spacer { amount: 8 }
		Image { src: "ship.png" }
		Spacer { amount: 8 }
  Markdown {color: "#000000" text: "Believe me or not, the motivation for this project was to buy myself a sailing yacht and sail to the caribean sea.
You also got a dream?
Start right now, create a book or even an app and help people with their business and they will help you fulfilling your dream."}
  Spacer { weight: 1 }
		Button { label: "About NoCode" link: "page:about"}
		Button { label: "Introduction Video" link: "page:video"}
  
	}
}