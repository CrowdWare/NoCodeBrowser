Page {
  backgroundColor: "#FFFFFF"
  color: "#ffffFF"
  padding: "8"

  Column {
    padding: "8"

    Markdown {
      color: "#4C9BD9"
      text: "# About the author"
    }

    Spacer { amount: 8 }
    Image { src: "olaf.jpg" }
    Spacer { amount: 8 }
    Markdown {
      color: "#000000"
      fontSize: 16
      text: "Olaf, the founder of CrowdWare and the inventor of NoCode was born 1963 in Hamburg, Germany. 
                He studied graphics- and 
																human computer interaction design and he has been working as a software freelance developer for more than 30 years.

The initial idea for this app was to build a new form of ebook with dynamic content like buttons, videos, sound and the like."
    }
    Spacer { weight: 1 }
    Button { label: "Back Home" link: "page:home" }
  }
}