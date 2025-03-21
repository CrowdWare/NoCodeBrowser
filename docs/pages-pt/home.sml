Page {
    padding: "8"
    title: "NoCodeLibMobile"

    Column {
        padding: "8"
                        
        Markdown {
            color: "#4C9BD9"
            text: "# Bem-vindo"
          }
          Markdown { 
              fontSize: 16
            text :"
                Ficamos felizes que você esteja usando a solução FreeBook.
                Por favor, note que esta é ainda uma versão incipiente, que pode conter alguns erros.
                O conteúdo deste aplicativo foi criado com **FreeBookDesigner**, um aplicativo para desktop."
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
            text: "Comece agora, crie um e-book ou até mesmo um aplicativo e produza conteúdos para outras pessoas, o que ajudará você a se aproximar de seus sonhos."}

        Spacer { weight: 1 }
        Button { label: "Sobre FreeBook" link: "page:app.about"}
    }
}