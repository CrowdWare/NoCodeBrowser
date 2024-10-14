Page {
  backgroundColor: "#FFFFFF"
  color: "#000000"
  padding: "8"

  Column {
    padding: "8"

    Markdown { 
      color: "#000000" 
      text: "# Intro videos" }
    Markdown { 
      color: "#000000" 
      fontSize: 16
      text: "Here are some youtube videos to show you how to use the NoCodeDesigner." }
    Spacer { amount: 16 }
    
    
    Markdown { 
      color: "#000000"
      fontSize: 16
      text: "### First contact
Just a placeholder at the moment."}
    Spacer { amount: 8 }
    Youtube { id: "eG3MZ-TaAbw" }
    Spacer { weight: 1 }
    Button { label: "Back Home" link: "page:home" }
  }
}