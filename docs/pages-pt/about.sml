Page {
	padding: "8"
	title: "Sobre"

	Column {
		padding: "8"

		Markdown { text: "# Sobre este App" }
		Spacer { amount: 8 }
		Markdown {
			text: "Você pode criar este tipo de aplicação com o FreeBookDesigner.
				Para testar uma aplicação deste tipo, você precisa deste aplicativo de navegador." 
		}
		Spacer {amount: 8}
		Row {
			Markdown {
				text:"Abra o menu (ícone de 
					hambúrguer) e selecione 
					Configurações.
					Na página de configurações, 
					adicione um link.
					Forneça um título exclusivo 
					para o link, que aparecerá 
					no menu de configurações.
					Agora insira a URL do seu 
					servidor web."
				}
			Spacer {amount: 8}
			Image { src: "menu.png" scale:"fit"}
		}
		Spacer {amount: 8}
		Markdown { 
			text:"Este URL geralmente se parece com **http://192.168.178.21:8000/**, que é o caminho para o seu servidor web local.
				Este URL aparece quando você inicia seu servidor web de teste baseado em Python na 
				sua pasta de projeto com: **python server.py** 
				Agora, basta clicar no botão [Adicionar link], 
				e o link deverá ser salvo.
				Quando você reabrir o menu, deverá ver o novo item.
				Clique nesse item e sua aplicação será carregada automaticamente.

				**A interface de usuário do aplicativo de navegador desaparecerá até que você reinicie o navegador.**"
		}
    		Spacer { weight: 1 }
   		Button { label: "Voltar à página inicial" link: "page:app.home" }
	}
}