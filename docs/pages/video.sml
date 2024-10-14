Page {
  backgroundColor: "#FFFFFF"
  color: "#000000"
  padding: "8"

  Column {
    padding: "8"

    Markdown { color: "#000000" text: "# Intro videos" }
    Markdown { color: "#000000" text: "Here are some youtube videos to show you how to user the NoCodeDesigner." }
    Spacer { amount: 16 }
    
    
    Markdown { color: "#000000"text: "### First contact"}
    Spacer { amount: 8 }
    Youtube { id: "eG3MZ-TaAbw" }
    Spacer { weight: 1 }
    Button { label: "Back Home" link: "page:home" }
  }
}