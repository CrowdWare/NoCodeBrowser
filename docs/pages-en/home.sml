Page {
    padding: "8"
    title: "NoCodeLibMobile"

    Column {
        padding: "8"
                        
        Markdown {
            color: "#4C9BD9"
            text: "# Welcome"
          }
          Markdown { 
              fontSize: 16
            text :"
                We are glad that you are using the FreeBook Solution.
                    Keep in mind that this is still an early version, 
                    which might include some glitches.
                The content of this app has been designed using the FreeBookDesigner, 
                which is an app for the desktop."
        }
        Spacer { amount: 16 }
        Markdown {
            color: "#4C9BD9"
            text: "### FreeBookDesigner"
         }
        Spacer { amount: 8 }
        Image { src: "desktop.png" }
        Spacer { amount: 16 }
        Markdown {
            fontSize: 16
            text: "Start right now, create an ebook or even an app and create 
                content for other people and they will help you getting closer 
                to your dreams."}

        Spacer { weight: 1 }
        Button { label: "About FreeBook" link: "page:app.about"}
    }
}
