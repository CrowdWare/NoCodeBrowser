Page {
    padding: "8"
    title: "NoCodeLibMobile"

    Column {
        padding: "8"
                        
        Markdown {
            color: "#4C9BD9"
            text: "# Favoriten"
          }
        LazyRow {
            url: "https://artanidos.pythonanywhere.com/crowdware/items?type=book&filter=favourite"
            height: 220

            Column { 
                weight:1
                 Image { 
                    src: "<pictureurl>" 
                    width: 120
                    link: "<link>"
                }
            }
            Spacer {amount: 8}
        }  
        
        Markdown {
            color: "#4C9BD9"
            text: "# Neuerscheinungen"
          }
        LazyRow {
            url: "https://artanidos.pythonanywhere.com/crowdware/items?type=book&filter=new"
            height: 220

            Column { 
                weight:1
                 Image { 
                    src: "<pictureurl>" 
                    width: 120
                    link: "<link>"
                }
            }
            Spacer {amount: 8}
        }  

        Spacer { weight: 1 }
        Button { label: "Bücher Finden" link: "page:app.books" }
    }
}