Page {
	padding: "8"
	title: "Acerca de"

	Column {
		padding: "8"

		Markdown { text: "# Acerca de esta App" }
		Spacer { amount: 8 }
		Markdown {
			text: "Puede diseñar este tipo de aplicaciones con el FreeBookDesigner.
				Para probar una aplicación de este tipo, necesita esta aplicación de navegador." 
		}
		Spacer {amount: 8}
		Row {
			Markdown {
				text:"Abra el menú (icono de ham
					burguesa) y seleccione 
					Configuración.
					En la página de configuración, 
					añada un enlace.
					Asigne un título único al enlace, 
					que se mostrará en el menú de 
					configuración.
					Ahora ingrese la URL de su 
					servidor web."
				}
			Spacer {amount: 8}
			Image { src: "menu.png" scale:"fit"}
		}
		Spacer {amount: 8}
		Markdown { 
			text:"Esta URL normalmente se ve similar a 
				**http://192.168.178.21:8000/**, que es la ruta
				a su servidor web local.
				Esta URL aparece cuando inicia su servidor 
				web de prueba basado en Python en su carpeta 
				de proyecto con: **python server.py** 
				Ahora simplemente haga clic en el botón [Añadir enlace], 
				y el enlace debería guardarse.
				Cuando vuelva a abrir el menú, debería ver el nuevo elemento.
				Haga clic en este elemento y su aplicación se cargará automáticamente.

				**La interfaz de usuario de la aplicación de navegador desaparecerá hasta que reinicie el navegador.**"
		}
    		Spacer { weight: 1 }
   		Button { label: "Volver a la página de inicio" link: "page:app.home" }
	}
}