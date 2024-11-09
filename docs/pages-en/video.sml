Page {
	padding: "8"

	Column {
		padding: "8"

		Markdown { text: "# Intro videos" }
		Markdown { 
			text: "Here are some youtube videos to show you how to use the NoCodeDesigner." 
		}
		Spacer { amount: 16 }
		Markdown { 
			text: "### First contact
				Just a placeholder at the moment."
		}
		Spacer { amount: 8 }
		Youtube { id: "eG3MZ-TaAbw" }
		Spacer { weight: 1 }
		Button { label: "Back Home" link: "page:home" }
	}
}