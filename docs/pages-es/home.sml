Page {
    padding: "8"
    title: "NoCodeLibMobile"

    Column {
        padding: "8"
                        
        Markdown {
            color: "#4C9BD9"
            text: "# Bienvenido"
          }
          Markdown { 
              fontSize: 16
            text :"
                Nos alegra que esté usando la solución FreeBook.
                Tenga en cuenta que esta es aún una versión temprana, que podría contener algunos errores.
                El contenido de esta aplicación ha sido creado con **FreeBookDesigner**, una aplicación para escritorio."
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
            text: "Empiece ahora, cree un E-Book o incluso una aplicación, y cree contenido para otras personas que le ayudarán a acercarse a sus sueños."}

        Spacer { weight: 1 }
        Button { label: "Acerca de FreeBook" link: "page:app.about"}
    }
}