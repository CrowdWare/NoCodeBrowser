Page {
	padding: "8"
	title: "NoCodeBrowser"

	Column {
		padding: "8"
						
		Markdown {
			color: "#4C9BD9"
			text: "# Bienvenido"
  		}
  		Markdown { 
      		fontSize: 16
			text :"
				Nos alegra que esté usando la solución NoCode.
				Tenga en cuenta que esta es aún una versión temprana, que podría contener algunos errores.
				El contenido de esta aplicación ha sido creado con **NoCodeDesigner**, una aplicación para escritorio."
		}
		Spacer { amount: 16 }
		Markdown {
			color: "#4C9BD9"
			text: "### NoCodeDesigner"
 		}
		Spacer { amount: 8 }
		Image { src: "desktop.png" }
		Spacer { amount: 16 }
		Markdown {
			fontSize: 16
			text: "Empiece ahora, cree un E-Book o incluso una aplicación, y cree contenido para otras personas que le ayudarán a acercarse a sus sueños."}

		Spacer { weight: 1 }
		Button { label: "Acerca de NoCode" link: "page:app.about"}
	}
}